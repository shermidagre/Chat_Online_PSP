// org.example.dto.Usuarios.UsuariosRequestDTO.java (Ajustado)

package org.example.dto.Usuarios;

import jakarta.validation.constraints.NotBlank;

public class UsuariosRequestDTO {

    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String nombre;

    private String tipoUsuario;

    // --- Getters y Setters ---

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}