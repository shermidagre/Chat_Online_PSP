package com.example.chat.dto;

import com.example.chat.model.MensajeChat;
import java.time.LocalDateTime;

public class RespuestaMensajeChat {

    private Long id;
    private String username;
    private String content;
    private LocalDateTime timestamp;

    public RespuestaMensajeChat() {
    }

    public RespuestaMensajeChat(Long id, String username, String content, LocalDateTime timestamp) {
        this.id = id;
        this.username = username;
        this.content = content;
        this.timestamp = timestamp;
    }

    public RespuestaMensajeChat(MensajeChat mensajeChat) {
        this.id = mensajeChat.getId();
        // IMPORTANTE: Si aquí te da error en .getUsername(), revisa que la clase Usuario tenga getters
        this.username = mensajeChat.getSender().getUsername();
        this.content = mensajeChat.getContent();
        this.timestamp = mensajeChat.getTimestamp();
    }

    // --- GETTERS Y SETTERS MANUALES ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}