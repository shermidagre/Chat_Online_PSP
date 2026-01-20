package org.example.dto.Usuarios;

import org.example.model.Usuario; // Usar Usuario

public class RespuestaUsuariosDTO {

    private Long idUsuario;
    private String nombre;
    private String tipoUsuario;

    public RespuestaUsuariosDTO() {}

    public RespuestaUsuariosDTO(Usuario usuario) { // Usar Usuario
        this.idUsuario = usuario.getIdUsuario(); // Usar getIdUsuario()
        this.nombre = usuario.getNombre(); // Usar getNombre()
        this.tipoUsuario = usuario.getTipoUsuario(); // Usar getTipoUsuario()
    }

    // --- Getters y Setters ---

    public Long getIdUsuario() { return idUsuario; } // Cambiado a getIdUsuario
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; } // Cambiado a setIdUsuario

    public String getNombre() { return nombre; } // Cambiado a getNombre
    public void setNombre(String nombre) { this.nombre = nombre; } // Cambiado a setNombre

    public String getTipoUsuario() { return tipoUsuario; } // Cambiado a getTipoUsuario
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; } // Cambiado a setTipoUsuario
}