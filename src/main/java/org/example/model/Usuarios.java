package org.example.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;


@Entity
@Table(name = "Usuario")
public class Usuarios {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idUsuario; // Empl

    @Column(nullable = false)
    private String nombre; // Nombre

    @Column(nullable = true)
    private String tipoUsuario;

    private String password;

    @Column(nullable = true)
    private String tipoEmpleado; // Tipo Empleado

    @Column(nullable = true)
    private String categoria;

    @Column(nullable = true)
    private String dni; // DNI

    @Column(nullable = true)
    private String fechaNacimiento; // F. Nacim.

    @Column(nullable = true)
    private String telefono; // Teléfono

    @Column(nullable = true)
    private String numeroSeguridadSocial; // Núm. S.S.

    @Column(nullable = true)
    private String coste; // Coste

    @Column(nullable = true)
    private String bloqueado; // Blq.






    // Constructor vacio para modificar tabla Usuarios
    public Usuarios() {
    }

    // Getters e Setters

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }
    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getTipoEmpleado() {
        return tipoEmpleado;
    }

    public void setTipoEmpleado(String tipoEmpleado) {
        this.tipoEmpleado = tipoEmpleado;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(String fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getNumeroSeguridadSocial() {
        return numeroSeguridadSocial;
    }

    public void setNumeroSeguridadSocial(String numeroSeguridadSocial) {
        this.numeroSeguridadSocial = numeroSeguridadSocial;
    }

    public String getCoste() {
        return coste;
    }

    public void setCoste(String coste) {
        this.coste = coste;
    }

    public String getBloqueado() {
        return bloqueado;
    }

    public void setBloqueado(String bloqueado) {
        this.bloqueado = bloqueado;
    }
}
