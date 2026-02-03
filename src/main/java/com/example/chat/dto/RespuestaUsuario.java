package com.example.chat.dto;

import com.example.chat.model.Usuario;
import java.time.LocalDateTime;

public class RespuestaUsuario {

    private String username;
    private LocalDateTime lastSeen;

    public RespuestaUsuario() {
    }

    public RespuestaUsuario(String username, LocalDateTime lastSeen) {
        this.username = username;
        this.lastSeen = lastSeen;
    }

    public RespuestaUsuario(Usuario usuario) {
        this.username = usuario.getUsername();
        this.lastSeen = usuario.getLastSeen();
    }

    // --- GETTERS Y SETTERS MANUALES ---

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(LocalDateTime lastSeen) {
        this.lastSeen = lastSeen;
    }
}