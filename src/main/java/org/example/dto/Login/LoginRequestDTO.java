package org.example.dto.Login;

public class LoginRequestDTO {

    private Long idUsuario;
    private String password;

    // Constructor vacío (necesario para que Spring/Jackson funcione)
    public LoginRequestDTO() {
    }

    // Constructor con campos (opcional, pero útil)
    public LoginRequestDTO(Long idUsuario, String password) {
        this.idUsuario = idUsuario;
        this.password = password;
    }

    // --- Getters y Setters ---

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}