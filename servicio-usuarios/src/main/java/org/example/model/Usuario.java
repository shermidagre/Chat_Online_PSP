package org.example.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios") // Nombre de la tabla en la base de datos
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    @Column(nullable = false) // La contraseña no puede ser nula
    private String password;

    @Column(nullable = false)
    private String tipoUsuario = "user"; // Valor por defecto

    // Constructor vacío (necesario para JPA)
    public Usuario() {
    }

    // Constructor con campos
    public Usuario(String nombre, String password) {
        this.nombre = nombre;
        this.password = password;
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Usuario{"
               + "id=" + id +
               ", nombre='" + nombre + "'" +
               '}';
    }
}
