// org.example.dto.Usuarios.PeticionUsuariosDTO.java

package org.example.dto.Usuarios;

import jakarta.validation.constraints.NotBlank;

public class PeticionUsuariosDTO { // Renombrado a PeticionUsuariosDTO

    @NotBlank(message = "El nombre del usuario es obligatorio")
    private String nombre; // Ya está en español

    private String tipoUsuario; // Ya está en español

    // --- Getters y Setters ---

    public String obtenerNombre() { return nombre; } // Renombrado a obtenerNombre
    public void establecerNombre(String nombre) { this.nombre = nombre; } // Renombrado a establecerNombre

    public String obtenerTipoUsuario() { return tipoUsuario; } // Renombrado a obtenerTipoUsuario
    public void establecerTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; } // Renombrado a establecerTipoUsuario
}
