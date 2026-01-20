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

    // Constructor vacío
    public Servidor() {
    }

    // Getters y Setters
    public long getIdServidor() {
        return idServidor;
    }

    public void setIdServidor(long idServidor) {
        this.idServidor = idServidor;
    }

    public String getNombreServidor() {
        return nombreServidor;
    }

    public void setNombreServidor(String nombreServidor) {
        this.nombreServidor = nombreServidor;
    }

    public String[] getComandosGenerales() {
        return comandosGenerales;
    }

    public void setComandosGenerales(String[] comandosGenerales) {
        this.comandosGenerales = comandosGenerales;
    }

    public String[] getComandosAdmin() {
        return comandosAdmin;
    }

    public void setComandosAdmin(String[] comandosAdmin) {
        this.comandosAdmin = comandosAdmin;
    }
}
