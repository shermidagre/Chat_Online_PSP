package com.example.chat.client;

import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

/**
 * The client-side socket implementation for the chat application.
 * This class handles the connection to the server, sending and receiving messages,
 * and updating the UI.
 */
public class ClienteSocket {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean escuchando = false;

    // Callback to send messages to the GUI
    private Consumer<String> onMessageReceived;

    /**
     * Sets the callback for when a message is received from the server.
     * @param onMessageReceived the callback function.
     */
    public void setOnMessageReceived(Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    /**
     * Connects to the server with the given host, port, and username.
     * @param host the server host.
     * @param puerto the server port.
     * @param usuario the username.
     * @throws IOException if an I/O error occurs when creating the socket.
     */
    public void conectar(String host, int puerto, String usuario) throws IOException {
        socket = new Socket(host, puerto);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Send LOGIN command according to protocol
        out.println("LOGIN|" + usuario);

        escuchando = true;

        // Start the listening thread (IMPORTANT FOR LEVEL 5)
        new Thread(this::escucharServidor).start();
    }

    /**
     * Listens for messages from the server in a separate thread.
     */
    private void escucharServidor() {
        try {
            String linea;
            while (escuchando && (linea = in.readLine()) != null) {
                final String mensajeProcesado = procesarMensaje(linea);

                // Update the UI from the JavaFX thread
                Platform.runLater(() -> {
                    if (onMessageReceived != null) {
                        onMessageReceived.accept(mensajeProcesado);
                    }
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> onMessageReceived.accept("Disconnected from the server."));
        }
    }

    /**
     * Processes a message received from the server.
     * @param linea the message line.
     * @return the processed message to be displayed in the UI.
     */
    private String procesarMensaje(String linea) {
        // Parse what comes from the server (e.g., MSG|Pepe|Hola)
        String[] partes = linea.split("\\|", 3);
        String comando = partes[0];

        if ("MSG".equals(comando) && partes.length > 2) {
            return partes[1] + ": " + partes[2];
        } else if ("INFO".equals(comando) && partes.length > 1) {
            return "[SYSTEM]: " + partes[1];
        } else if ("ERROR".equals(comando) && partes.length > 1) {
            return "[ERROR]: " + partes[1];
        } else {
            return linea; // Raw message if it does not comply with the protocol
        }
    }

    /**
     * Sends a message to the server.
     * @param usuario the username of the sender.
     * @param contenido the content of the message.
     */
    public void enviarMensaje(String usuario, String contenido) {
        if (out != null) {
            // Protocol: MSG|User|Content
            out.println("MSG|" + usuario + "|" + contenido);
        }
    }

    /**
     * Disconnects from the server.
     */
    public void desconectar() {
        escuchando = false;
        try {
            if (out != null) out.println("LOGOUT");
            if (socket != null) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
