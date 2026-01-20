package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "Servidor") // El nombre de la tabla ya está en español
public class Servidor { // Clase ya está en español

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long idServidor; // Ya está en español

    @Column(nullable = false)
    private String nombreServidor; // Ya está en español


    private String [] comandosGenerales; // Ya está en español


    private String [] comandosAdmin; // Ya está en español
}