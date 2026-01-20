package org.example.dto.Login;

public class PeticionLoginDTO {

    private Long idUsuario;
    private String contrasena;

    // Constructor vacío (necesario para que Spring/Jackson funcione)
    public PeticionLoginDTO() {
    }

    // Constructor con campos (opcional, pero útil)
    public PeticionLoginDTO(Long idUsuario, String contrasena) {
        this.idUsuario = idUsuario;
        this.contrasena = contrasena;
    }

    // --- Getters y Setters ---

    public Long getIdUsuario() { // Cambiado a getIdUsuario
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) { // Cambiado a setIdUsuario
        this.idUsuario = idUsuario;
    }

    public String getContrasena() { // Cambiado a getContrasena
        return contrasena;
    }

    public void setContrasena(String contrasena) { // Cambiado a setContrasena
        this.contrasena = contrasena;
    }
}