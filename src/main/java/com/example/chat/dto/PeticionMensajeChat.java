package com.example.chat.dto;

/**
 * Data Transfer Object (DTO) for chat message requests.
 * This class encapsulates the username and content of a message sent by a client.
 */
public class PeticionMensajeChat {
    private String username;
    private String content;

    /**
     * Default constructor.
     * Required for JSON/Jackson serialization/deserialization.
     */
    public PeticionMensajeChat() {}

    /**
     * Returns the username of the sender.
     * @return the username.
     */
    public String getUsername() { return username; }

    /**
     * Returns the content of the message.
     * @return the message content.
     */
    public String getContent() { return content; }

    /**
     * Sets the username of the sender.
     * @param username the username to set.
     */
    public void setUsername(String username) { this.username = username; }

    /**
     * Sets the content of the message.
     * @param content the message content to set.
     */
    public void setContent(String content) { this.content = content; }
}