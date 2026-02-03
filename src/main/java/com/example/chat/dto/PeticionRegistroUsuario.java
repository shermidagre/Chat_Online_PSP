package com.example.chat.dto;

/**
 * DTO para la solicitud de registro o identificación de un usuario.
 */
public class PeticionRegistroUsuario {

    private String username;

    // Constructor sin argumentos (necesario para JSON/Jackson)
    public PeticionRegistroUsuario() {
    }

    // Constructor con todos los argumentos
    public PeticionRegistroUsuario(String username) {
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}