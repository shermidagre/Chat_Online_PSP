package com.example.chat.client;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;

/**
 * The controller for the chat client's user interface.
 * This class handles user interactions and communicates with the socket client.
 */
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

    /**
     * Initializes the controller class.
     * This method is automatically called after the fxml file has been loaded.
     */
    @FXML
    public void initialize() {
        cliente = new ClienteSocket();

        // Define what to do when a message arrives from the socket
        cliente.setOnMessageReceived(mensaje -> {
            areaChat.appendText(mensaje + "\n");
        });

        // Initial visual state
        panelChat.setDisable(true);
    }

    /**
     * Handles the connect button click event.
     * It connects the client to the server and switches to the chat view.
     */
    @FXML
    protected void onConectarClick() {
        String usuario = txtUsuario.getText().trim();
        if (usuario.isEmpty()) return;

        try {
            // Connect to port 9000 (Socket Server), NOT 8080 (Web)
            cliente.conectar("localhost", 9000, usuario);

            this.usuarioActual = usuario;
            panelLogin.setDisable(true);
            panelLogin.setVisible(false); // Hide login
            panelChat.setDisable(false);  // Enable chat

            areaChat.appendText("Connected as " + usuario + "\n");

        } catch (IOException e) {
            areaChat.appendText("Error connecting to server: " + e.getMessage() + "\n");
        }
    }

    /**
     * Handles the send button click event.
     * It sends the message from the text field to the server.
     */
    @FXML
    protected void onEnviarClick() {
        String mensaje = txtMensaje.getText().trim();
        if (!mensaje.isEmpty()) {
            cliente.enviarMensaje(usuarioActual, mensaje);
            txtMensaje.clear();
        }
    }

    /**
     * Closes the connection to the server.
     * This method is called when the application window is closed.
     */
    public void cerrarConexion() {
        if (cliente != null) {
            cliente.desconectar();
        }
    }
}