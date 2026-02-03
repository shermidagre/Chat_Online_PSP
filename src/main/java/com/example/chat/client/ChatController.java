package com.example.chat.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChatController {

    @FXML private TextField txtUsuario;
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

        // Definimos qué hacer cuando llega un mensaje del socket
        cliente.setOnMessageReceived(mensaje -> {
            areaChat.appendText(mensaje + "\n");
        });

        // Estado inicial visual
        panelChat.setDisable(true);
    }

    @FXML
    protected void onConectarClick() {
        String usuario = txtUsuario.getText().trim();
        if (usuario.isEmpty()) return;

        try {
            // Conectar al puerto 9000 (Socket Server), NO al 8080 (Web)
            cliente.conectar("localhost", 9000, usuario);

            this.usuarioActual = usuario;
            panelLogin.setDisable(true);
            panelLogin.setVisible(false); // Ocultar login
            panelChat.setDisable(false);  // Habilitar chat

            areaChat.appendText("Conectado como " + usuario + "\n");

        } catch (IOException e) {
            areaChat.appendText("Error conectando al servidor: " + e.getMessage() + "\n");
        }
    }

    @FXML
    protected void onEnviarClick() {
        String mensaje = txtMensaje.getText().trim();
        if (!mensaje.isEmpty()) {
            cliente.enviarMensaje(usuarioActual, mensaje);
            txtMensaje.clear();
        }
    }

    // Método para llamar al cerrar la ventana
    public void cerrarConexion() {
        if (cliente != null) {
            cliente.desconectar();
        }
    }
}