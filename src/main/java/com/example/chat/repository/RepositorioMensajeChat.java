package com.example.chat.repository;

import com.example.chat.model.MensajeChat; // Importa la entidad MensajeChat
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link MensajeChat}.
 * Proporciona métodos para realizar operaciones CRUD y consultas personalizadas
 * sobre los mensajes de chat en la base de datos.
 */
@Repository
public interface RepositorioMensajeChat extends JpaRepository<MensajeChat, Long> {

    /**
     * Recupera los últimos 20 mensajes de chat, ordenados por marca de tiempo de forma descendente.
     * @return Una lista de los 20 {@link MensajeChat} más recientes.
     */
    List<MensajeChat> findTop20ByOrderByTimestampDesc();
}
