package com.example.chat.repository;

import com.example.chat.model.Usuario; // Importa la entidad Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Usuario}.
 * Proporciona métodos para realizar operaciones CRUD (Crear, Leer, Actualizar, Borrar)
 * y consultas personalizadas sobre la tabla de usuarios en la base de datos.
 */
@Repository
public interface RepositorioUsuario extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por su nombre de usuario.
     * @param username El nombre de usuario a buscar.
     * @return Un {@link Optional} que contiene el usuario si se encuentra, o vacío si no.
     */
    Optional<Usuario> findByUsername(String username);
}
