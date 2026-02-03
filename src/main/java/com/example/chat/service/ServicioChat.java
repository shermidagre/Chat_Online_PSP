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

/**
 * Service class for chat-related operations.
 * This class contains the business logic for user registration, sending messages,
 * and retrieving chat history.
 */
@Service
public class ServicioChat {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioMensajeChat repositorioMensajeChat;

    /**
     * Constructs a new chat service with the given repositories.
     * @param repositorioUsuario the user repository.
     * @param repositorioMensajeChat the chat message repository.
     */
    public ServicioChat(RepositorioUsuario repositorioUsuario, RepositorioMensajeChat repositorioMensajeChat) {
        this.repositorioUsuario = repositorioUsuario;
        this.repositorioMensajeChat = repositorioMensajeChat;
    }

    /**
     * Registers a user or returns the user if they already exist.
     *
     * @param username the username to register.
     * @return the registered or existing user.
     */
    @Transactional
    public Usuario registerUser(String username) {
        return repositorioUsuario.findByUsername(username)
                .orElseGet(() -> {
                    Usuario nuevo = new Usuario(username);
                    return repositorioUsuario.save(nuevo);
                });
    }

    /**
     * Updates the last seen timestamp for a user.
     * If the user does not exist, a new user is created.
     * @param username the username of the user to update.
     */
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

    /**
     * Sends a chat message from a user.
     * If the user does not exist, a new user is created.
     *
     * @param username the username of the sender.
     * @param content  the content of the message.
     * @return the saved chat message.
     */
    @Transactional
    public MensajeChat sendMessage(String username, String content) {
        Usuario sender = repositorioUsuario.findByUsername(username)
                .orElseGet(() -> repositorioUsuario.save(new Usuario(username)));

        MensajeChat mensaje = new MensajeChat(content, sender);
        return repositorioMensajeChat.save(mensaje);
    }

    /**
     * Retrieves the most recent chat messages.
     *
     * @param limit the maximum number of messages to retrieve.
     * @return a list of recent chat messages.
     */
    @Transactional
    public List<MensajeChat> getRecentMessages(int limit) {
        // Nota: 'limit' se ignora aquí porque el repositorio tiene fijo el Top 20,
        // pero mantenemos la firma para que coincida con el controlador.
        return repositorioMensajeChat.findTop20ByOrderByTimestampDesc();
    }

    /**
     * Retrieves a list of online users.
     * A user is considered online if they have been active in the last 5 minutes.
     *
     * @return a list of usernames of online users.
     */
    @Transactional
    public List<String> getOnlineUsers() {
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
        return repositorioUsuario.findAll().stream()
                .filter(u -> u.getLastSeen() != null && u.getLastSeen().isAfter(fiveMinutesAgo))
                .map(Usuario::getUsername)
                .collect(Collectors.toList());
    }

    /**
     * Deletes a user and all their messages.
     * @param username the username of the user to delete.
     */
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