// org.example.dto.Usuarios.PeticionUsuariosDTO.java

package org.example.dto.Usuarios;

import jakarta.validation.constraints.NotBlank;

public class PeticionUsuariosDTO {

    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String nombre;

    private String tipoUsuario;

    // --- Getters y Setters ---

    public String getNombre() { return nombre; } // Cambiado a getNombre
    public void setNombre(String nombre) { this.nombre = nombre; } // Cambiado a setNombre

    public String getTipoUsuario() { return tipoUsuario; } // Cambiado a getTipoUsuario
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; } // Cambiado a setTipoUsuario
}