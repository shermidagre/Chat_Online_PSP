package com.example.chat.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField; // <--- IMPORTANTE IMPORTAR
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChatController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword; // <--- NUEVA VARIABLE
    @FXML private Button btnConectar;
    @FXML private TextArea areaChat;
    @FXML private TextField txtMensaje;
    @FXML private Button btnEnviar;
    @FXML private VBox panelLogin;
    @FXML private VBox panelChat;

    private ClienteSocket cliente;
    private String usuarioActual;

    @FXML
    public void initialize() {
        cliente = new ClienteSocket();
        cliente.setOnMessageReceived(mensaje -> {
            areaChat.appendText(mensaje + "\n");
        });
        panelChat.setDisable(true);
    }

    @FXML
    protected void onConectarClick() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim(); // <--- OBTENER CONTRASEÑA

        if (usuario.isEmpty() || password.isEmpty()) {
            areaChat.appendText("Usuario y contraseña son obligatorios.\n");
            return;
        }

        try {
            // Pasamos ambos datos al método conectar
            cliente.conectar("localhost", 9000, usuario, password);

            this.usuarioActual = usuario;
            panelLogin.setDisable(true);
            panelLogin.setVisible(false);
            panelChat.setDisable(false);

            areaChat.appendText("Intentando conectar como " + usuario + "...\n");

        } catch (IOException e) {
            areaChat.appendText("Error conectando al servidor: " + e.getMessage() + "\n");
        }
    }

    @FXML
    protected void onEnviarClick() {
        String mensaje = txtMensaje.getText().trim();
        if (mensaje.isEmpty()) return;

        // --- LÓGICA DE COMANDOS ---
        if (mensaje.equals("/list")) {
            // Enviar comando WHO (Protocolo: WHO|UsuarioSolicitante)
            cliente.enviarComando("WHO", usuarioActual);

        } else if (mensaje.startsWith("/kick ")) {
            // Protocolo: KICK|Admin|UsuarioAExpulsar
            String usuarioAExpulsar = mensaje.substring(6).trim(); // Quitar "/kick "
            cliente.enviarComando("KICK", usuarioActual + "|" + usuarioAExpulsar);

        } else {
            // Es un mensaje normal
            cliente.enviarMensaje(usuarioActual, mensaje);
        }

        txtMensaje.clear();
    }

    // Método para llamar al cerrar la ventana
    public void cerrarConexion() {
        if (cliente != null) {
            cliente.desconectar();
        }
    }
}