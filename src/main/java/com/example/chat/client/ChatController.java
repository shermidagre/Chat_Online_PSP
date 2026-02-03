package com.example.chat.client;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label; // <--- IMPORTANTE
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class ChatController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtPassword;
    @FXML private Button btnConectar;
    @FXML private Label lblEstado; // <--- FALTABA ESTA VARIABLE

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
            Platform.runLater(() -> {
                if (mensaje.startsWith("OK|")) {
                    // Solo cambiamos de pantalla si el servidor dice OK
                    panelLogin.setVisible(false);
                    panelLogin.setManaged(false);
                    panelChat.setDisable(false);
                    areaChat.appendText("--- Conectado correctamente ---\n");

                } else if (mensaje.startsWith("ERROR|")) {
                    // Si hay error (bad pass), lo mostramos y reactivamos el botón
                    lblEstado.setText(mensaje.substring(6));
                    btnConectar.setDisable(false);

                } else {
                    // Mensaje normal de chat
                    areaChat.appendText(mensaje + "\n");
                }
            });
        });

        panelChat.setDisable(true);
    }

    @FXML
    protected void onConectarClick() {
        String usuario = txtUsuario.getText().trim();
        String password = txtPassword.getText().trim();

        if (usuario.isEmpty() || password.isEmpty()) {
            lblEstado.setText("Usuario y contraseña obligatorios.");
            return;
        }

        btnConectar.setDisable(true); // Evitar doble click
        lblEstado.setText("Conectando...");

        // La conexión debe ir en un hilo aparte para no congelar la ventana
        new Thread(() -> {
            try {
                // Intentamos conectar
                cliente.conectar("localhost", 9000, usuario, password);
                this.usuarioActual = usuario;

                // Esperamos a que el servidor mande "OK|" y lo capture el initialize()

            } catch (IOException e) {
                // Si falla la conexión de red (servidor apagado)
                Platform.runLater(() -> {
                    lblEstado.setText("Error: Servidor no responde.");
                    btnConectar.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    protected void onEnviarClick() {
        String mensaje = txtMensaje.getText().trim();
        if (mensaje.isEmpty()) return;

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

    public void cerrarConexion() {
        if (cliente != null) {
            cliente.desconectar();
        }
    }
}