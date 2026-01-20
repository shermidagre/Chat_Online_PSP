package org.example.repository;

import org.example.model.Usuario; // Usar Usuario
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RepositorioUsuarios extends JpaRepository<Usuario, Long> { // Renombrado a RepositorioUsuarios, y Usuario
    Optional<Usuario> findByNombre(String nombre); // Usar Usuario
}