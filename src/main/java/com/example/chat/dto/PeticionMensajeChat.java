package com.example.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO (Data Transfer Object) para la solicitud de envío de un nuevo mensaje de chat.
 * Contiene la información necesaria para que un usuario envíe un mensaje al chat.
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class PeticionMensajeChat {
    /**
     * El nombre de usuario del remitente del mensaje.
     */
    private String username;
    /**
     * El contenido textual del mensaje a enviar.
     */
    private String content;
}
