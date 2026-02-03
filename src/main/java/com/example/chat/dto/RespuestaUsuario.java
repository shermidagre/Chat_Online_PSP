package com.example.chat.dto;

import com.example.chat.model.Usuario; // Importa la entidad Usuario
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) para la respuesta de un usuario.
 * Contiene la información pública de un usuario para ser enviada al cliente, como su nombre y la última vez que estuvo activo.
 */
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class RespuestaUsuario {
    /**
     * El nombre de usuario.
     */
    private String username;
    /**
     * Marca de tiempo que indica la última vez que el usuario estuvo activo.
     */
    private LocalDateTime lastSeen;

    /**
     * Constructor que crea una {@code RespuestaUsuario} a partir de una entidad {@link Usuario}.
     * Mapea los campos relevantes de la entidad al DTO.
     * @param usuario La entidad {@link Usuario} de la que se extraen los datos.
     */
    public RespuestaUsuario(Usuario usuario) {
        this.username = usuario.getUsername();
        this.lastSeen = usuario.getLastSeen();
    }
}
