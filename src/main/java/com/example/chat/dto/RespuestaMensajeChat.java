package com.example.chat.dto;

import com.example.chat.model.MensajeChat; // Importa la entidad MensajeChat
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la respuesta de un mensaje de chat.
 * Contiene la información relevante de un mensaje de chat para ser enviada al cliente.
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class RespuestaMensajeChat {
    /**
     * Identificador único del mensaje de chat.
     */
    private Long id;
    /**
     * Nombre de usuario del remitente del mensaje.
     */
    private String username; // Nombre de usuario del remitente
    /**
     * Contenido textual del mensaje.
     */
    private String content;
    /**
     * Marca de tiempo que indica cuándo se envió el mensaje.
     */
    private LocalDateTime timestamp;


    /**
     * Constructor que crea una {@code RespuestaMensajeChat} a partir de una entidad {@link MensajeChat}.
     * Mapea los campos relevantes de la entidad al DTO.
     * @param mensajeChat La entidad {@link MensajeChat} de la que se extraen los datos.
     */
    public RespuestaMensajeChat(MensajeChat mensajeChat) {
        this.id = mensajeChat.getId();
        this.username = mensajeChat.getSender().getUsername(); // Obtiene el nombre de usuario del remitente
        this.content = mensajeChat.getContent();
        this.timestamp = mensajeChat.getTimestamp();
    }
}
