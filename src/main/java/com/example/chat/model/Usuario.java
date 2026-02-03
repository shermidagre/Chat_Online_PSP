package com.example.chat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa a un usuario en el sistema de chat.
 * Almacena información básica del usuario, como su identificador, nombre de usuario y la última vez que estuvo activo.
 */
@Entity
@Table(name = "usuarios") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class Usuario {

    /**
     * Identificador único del usuario.
     * Es la clave primaria de la entidad y se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre de usuario único.
     * No puede ser nulo y debe ser único en la tabla 'usuarios'.
     */
    @Column(unique = true, nullable = false)
    private String username;

    /**
     * Marca de tiempo que registra la última actividad del usuario.
     * Utilizada para determinar si un usuario está 'online'.
     */
    private LocalDateTime lastSeen;

    /**
     * Constructor para crear un nuevo usuario con un nombre de usuario dado.
     * La marca de tiempo 'lastSeen' se inicializa automáticamente con la hora actual en el momento de la creación.
     * @param username El nombre de usuario.
     */
    public Usuario(String username) {
        this.username = username;
        this.lastSeen = LocalDateTime.now(); // Establece la última vez visto al momento de la creación
    }
    public Usuario() {
        // Constructor vacío necesario para Hibernate
    }


    // Getters y setters


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
