package org.example.repository;

import org.example.model.Servidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioServidor extends JpaRepository<Servidor, Long> { // Renombrado a RepositorioServidor
}