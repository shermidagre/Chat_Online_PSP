package com.example.chat.dto;

public class PeticionMensajeChat {
    private String username;
    private String content;

    // Constructor vacío
    public PeticionMensajeChat() {}

    // Getters manuales (esto arreglará el error del Controlador)
    public String getUsername() { return username; }
    public String getContent() { return content; }

    // Setters manuales
    public void setUsername(String username) { this.username = username; }
    public void setContent(String content) { this.content = content; }
}