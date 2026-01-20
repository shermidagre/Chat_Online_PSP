package org.example.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


@Entity
@Table(name = "Usuario") // El nombre de la tabla ya es singular y en español
public class Usuario { // Clase Usuario

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

    public Long getIdUsuario() { // Mantener getIdUsuario
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) { // Mantener setIdUsuario
        this.idUsuario = idUsuario;
    }

    public String getNombre() { // Mantener getNombre
        return nombre;
    }

    public void setNombre(String nombre) { // Mantener setNombre
        this.nombre = nombre;
    }

    public String getTipoUsuario() { // Mantener getTipoUsuario
        return tipoUsuario;
    }
    public void setTipoUsuario(String tipoUsuario) { // Mantener setTipoUsuario
        this.tipoUsuario = tipoUsuario;
    }

    public String getContrasena() { return contrasena; } // get/set para contrasena
    public void setContrasena(String contrasena) { this.contrasena = contrasena; } // get/set para contrasena

    public String getPuerto() { // Mantener getPuerto
        return puerto;
    }
    public void setPuerto(String puerto) { // Mantener setPuerto
        this.puerto = puerto;
    }


}
