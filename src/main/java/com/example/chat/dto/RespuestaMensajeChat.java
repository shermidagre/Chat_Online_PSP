package com.example.chat.dto;

import com.example.chat.model.MensajeChat;
import java.time.LocalDateTime;

/**
 * Data Transfer Object (DTO) for chat message responses.
 * This class encapsulates the chat message information that is sent back to the client.
 */
public class RespuestaMensajeChat {

    private Long id;
    private String username;
    private String content;
    private LocalDateTime timestamp;

    /**
     * Default constructor.
     * Required for JSON/Jackson serialization/deserialization.
     */
    public RespuestaMensajeChat() {
    }

    /**
     * Constructs a new chat message response with the given details.
     * @param id the message ID.
     * @param username the username of the sender.
     * @param content the content of the message.
     * @param timestamp the timestamp of the message.
     */
    public RespuestaMensajeChat(Long id, String username, String content, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    /**
     * Constructs a new chat message response from a MensajeChat entity.
     * @param mensajeChat the MensajeChat entity.
     */
    public RespuestaMensajeChat(MensajeChat mensajeChat) {
        this.id = mensajeChat.getId();
        // IMPORTANTE: Si aquí te da error en .getUsername(), revisa que la clase Usuario tenga getters
        this.username = mensajeChat.getSender().getUsername();
        this.content = mensajeChat.getContent();
        this.timestamp = mensajeChat.getTimestamp();
    }

    // --- GETTERS Y SETTERS MANUALES ---

    /**
     * Returns the message ID.
     * @return the message ID.
     */
    public Long getId() {
        return id;
    }

    /**
     * Sets the message ID.
     * @param id the message ID to set.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * Returns the username of the sender.
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username of the sender.
     * @param username the username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Returns the content of the message.
     * @return the message content.
     */
    public String getContent() {
        return content;
    }

    /**
     * Sets the content of the message.
     * @param content the message content to set.
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Returns the timestamp of the message.
     * @return the timestamp.
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the timestamp of the message.
     * @param timestamp the timestamp to set.
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}