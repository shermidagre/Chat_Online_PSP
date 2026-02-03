package com.example.chat.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane; // <--- IMPORTANTE: Este es el nuevo import
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChatController {

    // --- ELEMENTOS DE LA VISTA (FXML) ---
    @FXML
    private BorderPane panelChat;

    @FXML
    private VBox panelLogin;

    @FXML
    private TextField txtUsuario;
    @FXML
    private PasswordField txtPassword; // Usamos PasswordField para la contraseña
    @FXML
    private Label lblEstado;

    @FXML
    private TextArea areaChat;
    @FXML
    private TextField txtMensaje;
    @FXML
    private Button btnEnviar;
    @FXML
    private Button btnConectar;

    // --- LÓGICA DEL CLIENTE ---
    private ClienteSocket cliente;

    public void initialize() {
        // Estado inicial: Login visible, Chat oculto
        panelLogin.setVisible(true);
        panelChat.setVisible(false);

        // Texto de estado vacío al inicio
        lblEstado.setText("");
    }

    @FXML
    protected void onConectarClick() {
        String usuario = txtUsuario.getText().trim();
        String pass = txtPassword.getText().trim();

        if (usuario.isEmpty() || pass.isEmpty()) {
            lblEstado.setText("Debes introducir usuario y contraseña.");
            return;
        }

        lblEstado.setText("Conectando...");
        btnConectar.setDisable(true);

        new Thread(() -> {
            try {
                cliente = new ClienteSocket();

                // Configurar qué hacer cuando llegan mensajes
                cliente.setOnMessageReceived(this::procesarMensajeEntrante);

                // IMPORTANTE: Asegúrate de que HOST y PUERTO coincidan con tu servidor
                // El puerto 9000 es el que definimos para SSL
                cliente.conectar("localhost", 9000, usuario, pass);

            } catch (IOException e) {
                Platform.runLater(() -> {
                    lblEstado.setText("Error de conexión: " + e.getMessage());
                    btnConectar.setDisable(false);
                });
            }
        }).start();
    }

    private void procesarMensajeEntrante(String mensaje) {
        // Todo lo que toque la interfaz gráfica debe ir en Platform.runLater
        Platform.runLater(() -> {
            if (mensaje.startsWith("OK|")) {
                // LOGIN CORRECTO
                cambiarPantallaAChat();
                areaChat.appendText("--- Conectado correctamente ---\n");
            } else if (mensaje.startsWith("ERROR|")) {
                // ERROR DE LOGIN O DE COMANDO
                String errorTexto = mensaje.substring(6);
                if (panelLogin.isVisible()) {
                    lblEstado.setText(errorTexto);
                    btnConectar.setDisable(false);
                    // Si falló el login, desconectamos el socket para limpiar
                    cliente.desconectar();
                } else {
                    areaChat.appendText("[ERROR]: " + errorTexto + "\n");
                }
            } else {
                // MENSAJE NORMAL DE CHAT
                areaChat.appendText(mensaje + "\n");
            }
        });
    }

    private void cambiarPantallaAChat() {
        panelLogin.setVisible(false);
        panelChat.setVisible(true);
    }

    @FXML
    protected void onEnviarClick() {
        String texto = txtMensaje.getText();
        if (texto != null && !texto.trim().isEmpty() && cliente != null) {
            // El usuario ya lo tenemos guardado en el servidor,
            // pero el método enviarMensaje del cliente pide usuario.
            // Podemos mandar el nombre, o refactorizar el cliente.
            // Por ahora mandamos el nombre del campo de texto:
            cliente.enviarMensaje(txtUsuario.getText(), texto);
            txtMensaje.clear();
        }
    }

    @FXML
    protected void cerrarConexion() {
        if (cliente != null) {
            cliente.desconectar();
        }
        // Volver al login
        panelChat.setVisible(false);
        panelLogin.setVisible(true);
        btnConectar.setDisable(false);
        lblEstado.setText("Desconectado.");
        areaChat.clear();
    }
}