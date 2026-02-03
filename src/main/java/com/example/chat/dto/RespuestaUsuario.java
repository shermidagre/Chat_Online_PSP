package com.example.chat.dto;

import com.example.chat.model.Usuario;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for user responses.
 * This class encapsulates the user information that is sent back to the client.
 */
public class RespuestaUsuario {

    private String username;
    private LocalDateTime lastSeen;

    /**
     * Default constructor.
     * Required for JSON/Jackson serialization/deserialization.
     */
    public RespuestaUsuario() {
    }

    /**
     * Constructs a new user response with the given username and last seen timestamp.
     * @param username the username of the user.
     * @param lastSeen the last time the user was seen.
     */
    public RespuestaUsuario(String username, LocalDateTime lastSeen) {
        this.username = username;
        this.lastSeen = lastSeen;
    }

    /**
     * Constructs a new user response from a Usuario entity.
     * @param usuario the Usuario entity.
     */
    public RespuestaUsuario(Usuario usuario) {
        this.username = usuario.getUsername();
        this.lastSeen = usuario.getLastSeen();
    }

    // --- GETTERS Y SETTERS MANUALES ---

    /**
     * Returns the username of the user.
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the user.
     * @param username the username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the last time the user was seen.
     * @return the last seen timestamp.
     */
    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    /**
     * Sets the last time the user was seen.
     * @param lastSeen the last seen timestamp to set.
     */
    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
}