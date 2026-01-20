package org.example.dto.Usuarios;

import org.example.model.Usuarios;

public class RespuestaUsuariosDTO { // Renombrado a RespuestaUsuariosDTO

    private Long idUsuario; // Ya está en español
    private String nombre; // Ya está en español
    private String tipoUsuario; // Ya está en español

    public RespuestaUsuariosDTO() {}

    public RespuestaUsuariosDTO(Usuarios usuario) {
        this.idUsuario = usuario.obtenerIdUsuario(); // Renombrado a obtenerIdUsuario
        this.nombre = usuario.obtenerNombre(); // Renombrado a obtenerNombre
        this.tipoUsuario = usuario.obtenerTipoUsuario(); // Renombrado a obtenerTipoUsuario
    }

    // --- Getters y Setters ---

    public Long obtenerIdUsuario() { return idUsuario; } // Renombrado a obtenerIdUsuario
    public void establecerIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; } // Renombrado a establecerIdUsuario

    public String obtenerNombre() { return nombre; } // Renombrado a obtenerNombre
    public void establecerNombre(String nombre) { this.nombre = nombre; } // Renombrado a establecerNombre

    public String obtenerTipoUsuario() { return tipoUsuario; } // Renombrado a obtenerTipoUsuario
    public void establecerTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; } // Renombrado a establecerTipoUsuario
}
