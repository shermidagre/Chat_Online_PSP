package com.example.chat.service;

import com.example.chat.model.MensajeChat;
import com.example.chat.model.Usuario;
import com.example.chat.repository.RepositorioMensajeChat;
import com.example.chat.repository.RepositorioUsuario;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ServicioChat {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioMensajeChat repositorioMensajeChat;
    private final BCryptPasswordEncoder passwordEncoder;

    public ServicioChat(RepositorioUsuario repositorioUsuario, RepositorioMensajeChat repositorioMensajeChat) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioMensajeChat = repositorioMensajeChat;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * MÉTODO UNIFICADO: Sirve tanto para el Socket como para el Controlador.
     * Registra un usuario nuevo con contraseña o autentica uno existente.
     */
    @Transactional
    public Usuario registerUser(String username, String rawPassword) {
        Optional<Usuario> existente = repositorioUsuario.findByUsername(username);

        if (existente.isPresent()) {
            Usuario u = existente.get();
            // Si la contraseña coincide, actualizamos lastSeen y devolvemos
            if (passwordEncoder.matches(rawPassword, u.getPassword())) {
                u.setLastSeen(LocalDateTime.now());
                return repositorioUsuario.save(u);
            } else {
                // Contraseña incorrecta: lanzamos excepción para que el Controller de un 401/403
                throw new IllegalArgumentException("Contraseña incorrecta para el usuario " + username);
            }
        } else {
            // Registro nuevo
            String role = "USER";
            if (username.equalsIgnoreCase("admin")) role = "ADMIN";

            Usuario nuevo = new Usuario(username, passwordEncoder.encode(rawPassword), role);
            return repositorioUsuario.save(nuevo);
        }
    }

    /**
     * Método sendMessage compatible con el Controlador y el Socket.
     */
    @Transactional
    public MensajeChat sendMessage(String username, String content) {
        Usuario sender = repositorioUsuario.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado: " + username));

        MensajeChat mensaje = new MensajeChat(content, sender);
        return repositorioMensajeChat.save(mensaje);
    }

    @Transactional
    public List<MensajeChat> getRecentMessages(int limit) {
        return repositorioMensajeChat.findTop20ByOrderByTimestampDesc();
    }

    @Transactional
    public List<String> getOnlineUsers() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return repositorioUsuario.findAll().stream()
                .filter(u -> u.getLastSeen() != null && u.getLastSeen().isAfter(fiveMinutesAgo))
                .map(Usuario::getUsername)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateLastSeen(String username) {
        repositorioUsuario.findByUsername(username).ifPresent(u -> {
            u.setLastSeen(LocalDateTime.now());
            repositorioUsuario.save(u);
        });
    }

    @Transactional
    public void deleteUser(String username) {
        repositorioUsuario.findByUsername(username).ifPresent(usuario -> {
            // Borrar mensajes primero si no tienes CASCADE en BD
            List<MensajeChat> mensajes = repositorioMensajeChat.findAll().stream()
                    .filter(m -> m.getSender().getId().equals(usuario.getId()))
                    .collect(Collectors.toList());
            repositorioMensajeChat.deleteAll(mensajes);

            repositorioUsuario.delete(usuario);
        });
    }
}