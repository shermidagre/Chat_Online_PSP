package org.example.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


@Entity
@Table(name = "Usuario") // El nombre de la tabla ya es singular y en español
public class Usuario { // Renombrado a Usuario

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario; // Id usuario

    @Column(nullable = false)
    private String nombre; // Nombre

    @Column(nullable = true)
    private String tipoUsuario; // Rol

    private String contrasena; // Renombrado a contrasena

    private String puerto; // Puerto



    // Constructor vacío para modificar tabla Usuario
    public Usuario() {
    }

    // Getters y Setters

    public Long obtenerIdUsuario() { // Renombrado a obtenerIdUsuario
        return idUsuario;
    }

    public void establecerIdUsuario(Long idUsuario) { // Renombrado a establecerIdUsuario
        this.idUsuario = idUsuario;
    }

    public String obtenerNombre() { // Renombrado a obtenerNombre
        return nombre;
    }

    public void establecerNombre(String nombre) { // Renombrado a establecerNombre
        this.nombre = nombre;
    }

    public String obtenerTipoUsuario() { // Renombrado a obtenerTipoUsuario
        return tipoUsuario;
    }
    public void establecerTipoUsuario(String tipoUsuario) { // Renombrado a establecerTipoUsuario
        this.tipoUsuario = tipoUsuario;
    }

    public String obtenerContrasena() { return contrasena; } // Renombrado a obtenerContrasena
    public void establecerContrasena(String contrasena) { this.contrasena = contrasena; } // Renombrado a establecerContrasena

    public String obtenerPuerto() { // Renombrado a obtenerPuerto
        return puerto;
    }
    public void establecerPuerto(String puerto) { // Renombrado a establecerPuerto
        this.puerto = puerto;
    }


}