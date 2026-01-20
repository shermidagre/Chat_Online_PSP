package org.example.dto.Usuarios;

import org.example.model.Usuarios;

public class UsuariosResponseDTO {

    private Long idUsuario;
    private String nombre;
    private String tipoUsuario; // <-- Campo Ajustado

    public UsuariosResponseDTO() {}

    public UsuariosResponseDTO(Usuarios usuario) {
        this.idUsuario = usuario.getIdUsuario();
        this.nombre = usuario.getNombre();
        this.tipoUsuario = usuario.getTipoUsuario(); // <-- Mapeo del campo
    }

    // --- Getters y Setters ---

    public Long getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Long idUsuario) { this.idUsuario = idUsuario; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTipoUsuario() { return tipoUsuario; } // <-- Get y Set ajustados
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }
}