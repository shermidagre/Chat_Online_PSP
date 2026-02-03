package com.example.chat.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entidad que representa un mensaje de chat en el sistema.
 * Almacena el contenido del mensaje, la marca de tiempo de envío y el usuario remitente.
 */
@Entity
@Table(name = "mensajes_chat") // Nombre de la tabla en la base de datos
@Data // Genera getters, setters, toString, equals y hashCode automáticamente
@AllArgsConstructor // Genera un constructor con todos los argumentos
public class MensajeChat {

    /**
     * Identificador único del mensaje.
     * Es la clave primaria de la entidad y se genera automáticamente.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Contenido textual del mensaje.
     * No puede ser nulo y tiene una longitud máxima de 1024 caracteres.
     */
    @Column(nullable = false, length = 1024)
    private String content;

    /**
     * Marca de tiempo que registra la fecha y hora exacta en que se envió el mensaje.
     * No puede ser nulo.
     */
    @Column(nullable = false)
    private LocalDateTime timestamp;

    /**
     * Usuario remitente del mensaje.
     * Es una relación Many-to-One con la entidad {@link Usuario}.
     * No puede ser nulo.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_id", nullable = false)
    private Usuario sender; // Referencia a la entidad Usuario

    /**
     * Constructor para crear un nuevo mensaje de chat.
     * La marca de tiempo 'timestamp' se inicializa automáticamente con la hora actual en el momento de la creación.
     * @param content El contenido textual del mensaje.
     * @param sender El usuario que envía el mensaje.
     */
    public MensajeChat(String content, Usuario sender) {
        this.content = content;
        this.sender = sender;
        this.timestamp = LocalDateTime.now();
    }

    public MensajeChat() {
        // Constructor vacío necesario para Hibernate
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Usuario getSender() {
        return sender;
    }

    public void setSender(Usuario sender) {
        this.sender = sender;
    }
}
