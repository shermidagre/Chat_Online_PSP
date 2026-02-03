package com.example.chat.dto;

/**
 * DTO para la solicitud de registro o identificación de un usuario.
 */
public class PeticionRegistroUsuario {

    private String username;

    private String password;

    // Constructor sin argumentos (necesario para JSON/Jackson)
    public PeticionRegistroUsuario() {
    }

    // Constructor con todos los argumentos
    public PeticionRegistroUsuario(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}