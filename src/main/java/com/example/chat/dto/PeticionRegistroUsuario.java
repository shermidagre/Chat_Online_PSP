package com.example.chat.dto;

/**
 * Data Transfer Object (DTO) for user registration or identification requests.
 * This class encapsulates the username provided by the client when they
 * want to register or log in to the chat service.
 */
public class PeticionRegistroUsuario {

    private String username;

    /**
     * Default constructor.
     * Required for JSON/Jackson serialization/deserialization.
     */
    public PeticionRegistroUsuario() {
    }

    /**
     * Constructs a new user registration request with the given username.
     *
     * @param username the username of the user to register or log in.
     */
    public PeticionRegistroUsuario(String username) {
        this.username = username;
    }

    /**
     * Returns the username from the request.
     *
     * @return the username.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username for the request.
     *
     * @param username the username to set.
     */
    public void setUsername(String username) {
        this.username = username;
    }
}