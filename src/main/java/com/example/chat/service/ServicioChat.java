package com.example.chat.service;

import com.example.chat.model.MensajeChat;
import com.example.chat.model.Usuario;
import com.example.chat.repository.RepositorioMensajeChat;
import com.example.chat.repository.RepositorioUsuario;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioChat {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioMensajeChat repositorioMensajeChat;

    public ServicioChat(RepositorioUsuario repositorioUsuario, RepositorioMensajeChat repositorioMensajeChat) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioMensajeChat = repositorioMensajeChat;
    }

    /**
     * Registra un usuario o lo devuelve si ya existe.
     * Método necesario para el endpoint /usuarios/registrar
     */
    @Transactional
    public Usuario registerUser(String username) {
        return repositorioUsuario.findByUsername(username)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario(username);
                    return repositorioUsuario.save(nuevo);
                });
    }

    @Transactional
    public void updateLastSeen(String username) {
        Usuario usuario = repositorioUsuario.findByUsername(username)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario(username);
                    return repositorioUsuario.save(nuevo);
                });
        usuario.setLastSeen(LocalDateTime.now());
        repositorioUsuario.save(usuario);
    }

    @Transactional
    public MensajeChat sendMessage(String username, String content) {
        Usuario sender = repositorioUsuario.findByUsername(username)
                .orElseGet(() -> repositorioUsuario.save(new Usuario(username)));

        MensajeChat mensaje = new MensajeChat(content, sender);
        return repositorioMensajeChat.save(mensaje);
    }

    /**
     * Obtiene los mensajes recientes.
     * Método necesario para el endpoint GET /mensajes
     */
    @Transactional
    public List<MensajeChat> getRecentMessages(int limit) {
        // Nota: 'limit' se ignora aquí porque el repositorio tiene fijo el Top 20,
        // pero mantenemos la firma para que coincida con el controlador.
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
    public void deleteUser(String username) {
        repositorioUsuario.findByUsername(username).ifPresent(usuario -> {
            List<MensajeChat> mensajes = repositorioMensajeChat.findAll().stream()
                    .filter(m -> m.getSender().getId().equals(usuario.getId()))
                    .collect(Collectors.toList());
            repositorioMensajeChat.deleteAll(mensajes);
            repositorioUsuario.delete(usuario);
        });
    }
}