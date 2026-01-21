package org.example.controller;

import org.example.model.Usuario;
import org.example.repository.RepositorioUsuarios;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component // Importante: Spring debe gestionarlo para la inyección de dependencias
@RestController
@CrossOrigin(origins = "*") // Permitir peticiones desde cualquier origen para el desarrollo del frontend
public class ChatController extends TextWebSocketHandler {

    private final RepositorioUsuarios repositorioUsuarios;
    private final Map<String, WebSocketSession> sesionesActivas = new ConcurrentHashMap<>(); // Map<ID_SESION, SESION>
    private final Map<String, String> usuariosLogueados = new ConcurrentHashMap<>(); // Map<ID_SESION, NOMBRE_USUARIO>
    private static final int MAX_MESSAGE_LENGTH = 200;

    public ChatController(RepositorioUsuarios repositorioUsuarios) {
        this.repositorioUsuarios = repositorioUsuarios;
    }

    // --- API REST para Conexión/Login ---
    @PostMapping("/api/conexion")
    public Map<String, String> conexion(@RequestBody Map<String, String> credenciales) {
        String nombre = credenciales.get("nombre");
        String password = credenciales.get("password");

        if (nombre == null || password == null || nombre.isEmpty() || password.isEmpty()) {
            return Map.of("estado", "error", "mensaje", "Nombre de usuario y contraseña son obligatorios.");
        }

        Optional<Usuario> usuarioOpt = repositorioUsuarios.findByNombre(nombre);

        if (usuarioOpt.isEmpty()) {
            return Map.of("estado", "error", "mensaje", "Usuario no encontrado.");
        }

        Usuario usuario = usuarioOpt.get();
        // A SIMPLIFICAR: Para este ejemplo, la contraseña es texto plano.
        // En un caso real, aquí se usaría un PasswordEncoder como BCrypt para verificar contraseñas hasheadas.
        if (usuario.getPassword().equals(password)) {
            // Lógica para registrar al usuario como "conectado" o darle un token si fuera necesario.
            // Para este ejemplo, simplemente devolvemos éxito.
            return Map.of("estado", "ok", "mensaje", "Login exitoso", "usuario", nombre);
        } else {
            return Map.of("estado", "error", "mensaje", "Contraseña incorrecta.");
        }
    }

    // --- WebSocket Handling ---

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Nueva conexión WebSocket establecida: " + session.getId());
        sesionesActivas.put(session.getId(), session);
        // Aquí podrías enviar un mensaje de bienvenida o solicitar credenciales si no vienen de la API REST
        session.sendMessage(new TextMessage("Bienvenido al chat. Por favor, identifícate con tu nombre de usuario."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Mensaje recibido de " + session.getId() + ": " + payload);

        // Primer mensaje del cliente tras conexión: su nick
        if (!usuariosLogueados.containsKey(session.getId())) {
            // Asumimos que el primer mensaje es el nombre de usuario tras un login REST exitoso
            // O podríamos pedir que envíen un JSON con "type": "auth", "username": "..."
            String username = payload.trim(); // Simplemente tomamos el mensaje como el nombre de usuario

            // Por ahora, solo permitimos usuarios que existen en la BDD
            Optional<Usuario> userInDB = repositorioUsuarios.findByNombre(username);
            if (userInDB.isPresent()) {
                usuariosLogueados.put(session.getId(), username);
                broadcastMessage("SISTEMA: " + username + " se ha unido al chat.");
                session.sendMessage(new TextMessage("Conectado como: " + username + ". ¡Empieza a chatear!"));
                System.out.println("Usuario " + username + " logueado via WebSocket.");
            } else {
                session.sendMessage(new TextMessage("Usuario '" + username + "' no reconocido. Desconectando."));
                session.close(CloseStatus.NOT_ACCEPTABLE);
            }
            return;
        }

        String sender = usuariosLogueados.get(session.getId());
        if (sender == null) {
            session.sendMessage(new TextMessage("Error: No estás identificado."));
            session.close(CloseStatus.POLICY_VIOLATION);
            return;
        }

        // Validación de longitud del mensaje
        if (payload.length() > MAX_MESSAGE_LENGTH) {
            session.sendMessage(new TextMessage("ERROR: El mensaje no puede superar los " + MAX_MESSAGE_LENGTH + " caracteres."));
            return;
        }

        // Difundir el mensaje a todos los usuarios conectados
        String fullMessage = sender + ": " + payload;
        broadcastMessage(fullMessage);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Conexión WebSocket cerrada: " + session.getId() + " con estado " + status.getCode());
        sesionesActivas.remove(session.getId());
        String disconnectedUser = usuariosLogueados.remove(session.getId());
        if (disconnectedUser != null) {
            broadcastMessage("SISTEMA: " + disconnectedUser + " ha abandonado el chat.");
        }
    }

    private void broadcastMessage(String message) {
        sesionesActivas.forEach((id, session) -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                System.err.println("Error enviando mensaje a la sesión " + id + ": " + e.getMessage());
            }
        });
    }
}
