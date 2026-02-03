package com.example.chat.controller;

import com.example.chat.dto.PeticionMensajeChat;
import com.example.chat.dto.PeticionRegistroUsuario;
import com.example.chat.dto.RespuestaMensajeChat;
import com.example.chat.dto.RespuestaUsuario;
import com.example.chat.model.MensajeChat;
import com.example.chat.model.Usuario;
import com.example.chat.service.ServicioChat;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@Tag(name = "Microservicio de Chat", description = "Endpoints para la funcionalidad de chat en tiempo real")
public class ControladorChat {

    private final ServicioChat servicioChat;

    public ControladorChat(ServicioChat servicioChat) {
        this.servicioChat = servicioChat;
    }

    @Operation(summary = "Registra o hace login de un usuario (Requiere password)")
    @PostMapping("/usuarios/registrar")
    public ResponseEntity<?> registrarUsuario(@RequestBody PeticionRegistroUsuario request) {
        // Validación básica
        if (request.getPassword() == null || request.getPassword().isEmpty()) {
            return new ResponseEntity<>("La contraseña es obligatoria", HttpStatus.BAD_REQUEST);
        }

        try {
            // Llamamos al servicio con USUARIO y PASSWORD
            Usuario usuario = servicioChat.registerUser(request.getUsername(), request.getPassword());
            return new ResponseEntity<>(new RespuestaUsuario(usuario), HttpStatus.OK);

        } catch (IllegalArgumentException e) {
            // Si la contraseña es incorrecta
            return new ResponseEntity<>("Credenciales inválidas", HttpStatus.UNAUTHORIZED);
        }
    }

    @Operation(summary = "Envía un nuevo mensaje de chat")
    @PostMapping("/mensajes")
    public ResponseEntity<RespuestaMensajeChat> enviarMensaje(@RequestBody PeticionMensajeChat request) {
        try {
            MensajeChat mensaje = servicioChat.sendMessage(request.getUsername(), request.getContent());
            return new ResponseEntity<>(new RespuestaMensajeChat(mensaje), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            // Si el usuario no existe
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    // Los demás métodos (GET mensajes, latido, delete) quedan igual ya que no requieren cambios de lógica interna
    @Operation(summary = "Obtiene los mensajes de chat recientes")
    @GetMapping("/mensajes")
    public ResponseEntity<List<RespuestaMensajeChat>> obtenerMensajesRecientes(@RequestParam(defaultValue = "20") int limit) {
        List<RespuestaMensajeChat> mensajes = servicioChat.getRecentMessages(limit).stream()
                .map(RespuestaMensajeChat::new)
                .collect(Collectors.toList());
        return new ResponseEntity<>(mensajes, HttpStatus.OK);
    }

    @Operation(summary = "Obtiene una lista de usuarios online")
    @GetMapping("/usuarios/online")
    public ResponseEntity<List<String>> obtenerUsuariosOnline() {
        List<String> usuariosOnline = servicioChat.getOnlineUsers();
        return new ResponseEntity<>(usuariosOnline, HttpStatus.OK);
    }

    @Operation(summary = "Actualiza el latido del usuario")
    @PostMapping("/usuarios/latido/{username}")
    public ResponseEntity<Void> latidoUsuario(@PathVariable String username) {
        servicioChat.updateLastSeen(username);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

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