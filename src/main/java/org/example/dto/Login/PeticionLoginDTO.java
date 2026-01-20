package org.example.dto.Login;

public class PeticionLoginDTO { // Renombrado a PeticionLoginDTO

    private Long idUsuario;
    private String contrasena; // Renombrado a contrasena

    // Constructor vacío (necesario para que Spring/Jackson funcione)
    public PeticionLoginDTO() {
    }

    // Constructor con campos (opcional, pero útil)
    public PeticionLoginDTO(Long idUsuario, String contrasena) { // Renombrado a contrasena
        this.idUsuario = idUsuario;
        this.contrasena = contrasena; // Renombrado a contrasena
    }

    // --- Getters y Setters ---

    public Long obtenerIdUsuario() { // Renombrado a obtenerIdUsuario
        return idUsuario;
    }

    public void establecerIdUsuario(Long idUsuario) { // Renombrado a establecerIdUsuario
        this.idUsuario = idUsuario;
    }

    public String obtenerContrasena() { // Renombrado a obtenerContrasena
        return contrasena;
    }

    public void establecerContrasena(String contrasena) { // Renombrado a establecerContrasena
        this.contrasena = contrasena;
    }
}
