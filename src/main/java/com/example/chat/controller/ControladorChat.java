package com.example.chat.controller;

import com.example.chat.dto.PeticionMensajeChat;
import com.example.chat.dto.PeticionRegistroUsuario;
import com.example.chat.dto.RespuestaMensajeChat;
import com.example.chat.dto.RespuestaUsuario;
import com.example.chat.model.MensajeChat;
import com.example.chat.model.Usuario;
import com.example.chat.service.ServicioChat; // Importa el servicio renombrado
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para el microservicio de chat.
 * Expone los endpoints HTTP para interactuar con las funcionalidades de chat, como el registro de usuarios,
 * el envío y recepción de mensajes, y la gestión de la presencia de usuarios.
 */
@RestController
@RequestMapping("/api/chat")
@Tag(name = "Microservicio de Chat", description = "Endpoints para la funcionalidad de chat en tiempo real")
public class ControladorChat {

    private final ServicioChat servicioChat; // Referencia al servicio de chat

    /**
     * Constructor para la inyección de dependencias del servicio de chat.
     * @param servicioChat El servicio de chat.
     */
    public ControladorChat(ServicioChat servicioChat) {
        this.servicioChat = servicioChat;
    }

    /**
     * Registra un nuevo usuario en el sistema o actualiza su estado de presencia si ya existe.
     * @param request La {@link PeticionRegistroUsuario} que contiene el nombre de usuario.
     * @return Una {@link ResponseEntity} con la {@link RespuestaUsuario} del usuario registrado/actualizado.
     */
    @Operation(summary = "Registra o actualiza la presencia de un usuario")
    @PostMapping("/usuarios/registrar")
    public ResponseEntity<RespuestaUsuario> registrarUsuario(@RequestBody PeticionRegistroUsuario request) {
        Usuario usuario = servicioChat.registerUser(request.getUsername());
        return new ResponseEntity<>(new RespuestaUsuario(usuario), HttpStatus.OK);
    }

    /**
     * Envía un nuevo mensaje de chat desde un usuario específico.
     * @param request La {@link PeticionMensajeChat} que contiene el nombre de usuario del remitente y el contenido del mensaje.
     * @return Una {@link ResponseEntity} con la {@link RespuestaMensajeChat} del mensaje enviado.
     */
    @Operation(summary = "Envía un nuevo mensaje de chat")
    @PostMapping("/mensajes")
    public ResponseEntity<RespuestaMensajeChat> enviarMensaje(@RequestBody PeticionMensajeChat request) {
        try {
            MensajeChat mensaje = servicioChat.sendMessage(request.getUsername(), request.getContent());
            return new ResponseEntity<>(new RespuestaMensajeChat(mensaje), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Obtiene los mensajes de chat más recientes.
     * @param limit El número máximo de mensajes a recuperar (por defecto 20).
     * @return Una {@link ResponseEntity} con una lista de {@link RespuestaMensajeChat} recientes.
     */
    @Operation(summary = "Obtiene los mensajes de chat recientes")
    @GetMapping("/mensajes")
    public ResponseEntity<List<RespuestaMensajeChat>> obtenerMensajesRecientes(@RequestParam(defaultValue = "20") int limit) {
        List<RespuestaMensajeChat> mensajes = servicioChat.getRecentMessages(limit).stream()
                .map(RespuestaMensajeChat::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(mensajes, HttpStatus.OK);
    }

    /**
     * Obtiene una lista de los nombres de usuario de los usuarios actualmente online.
     * Un usuario se considera online si su última actividad fue en los últimos 5 minutos.
     * @return Una {@link ResponseEntity} con una lista de {@link String} de usuarios online.
     */
    @Operation(summary = "Obtiene una lista de usuarios online (activos en los últimos 5 minutos)")
    @GetMapping("/usuarios/online")
    public ResponseEntity<List<String>> obtenerUsuariosOnline() {
        List<String> usuariosOnline = servicioChat.getOnlineUsers();
        return new ResponseEntity<>(usuariosOnline, HttpStatus.OK);
    }

    /**
     * Actualiza la marca de tiempo 'lastSeen' de un usuario, indicando su actividad (latido).
     * @param username El nombre de usuario cuyo estado 'lastSeen' se va a actualizar.
     * @return Una {@link ResponseEntity} sin contenido.
     */
    @Operation(summary = "Actualiza la marca de tiempo de última actividad del usuario (latido)")
    @PostMapping("/usuarios/latido/{username}")
    public ResponseEntity<Void> latidoUsuario(@PathVariable String username) {
        servicioChat.updateLastSeen(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Elimina un usuario del sistema y todos sus mensajes asociados.
     * @param username El nombre de usuario a eliminar.
     * @return Una {@link ResponseEntity} sin contenido si la eliminación fue exitosa, o con error si el usuario no fue encontrado.
     */
    @Operation(summary = "Elimina un usuario del sistema")
    @DeleteMapping("/usuarios/{username}")
    public ResponseEntity<Void> eliminarUsuario(@PathVariable String username) {
        try {
            servicioChat.deleteUser(username);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
