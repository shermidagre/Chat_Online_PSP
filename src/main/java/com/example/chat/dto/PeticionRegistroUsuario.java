package com.example.chat.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO (Data Transfer Object) para la solicitud de registro o identificación de un usuario.
 * Contiene la información necesaria para registrar o actualizar la presencia de un usuario en el chat.
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class PeticionRegistroUsuario {
    /**
     * El nombre de usuario que se desea registrar o identificar.
     */
    private String username;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
