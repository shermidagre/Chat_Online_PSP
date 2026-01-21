package org.example.repository;

import org.example.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioUsuarios extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByNombre(String nombre);
}
