package com.example.chat.client;

import javafx.application.Platform;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.function.Consumer;

public class ClienteSocket {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private boolean escuchando = false;

    // Callback para enviar mensajes a la Interfaz Gráfica
    private Consumer<String> onMessageReceived;

    public void setOnMessageReceived(Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void conectar(String host, int puerto, String usuario, String password) throws IOException {
        socket = new Socket(host, puerto);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // PROTOCOLO ACTUALIZADO: Enviamos también la contraseña
        // Formato: LOGIN|Usuario|Contraseña
        out.println("LOGIN|" + usuario + "|" + password);

        escuchando = true;
        new Thread(this::escucharServidor).start();
    }

    private void escucharServidor() {
        try {
            String linea;
            while (escuchando && (linea = in.readLine()) != null) {
                final String mensajeProcesado = procesarMensaje(linea);

                // Actualizar la UI desde el hilo de JavaFX
                Platform.runLater(() -> {
                    if (onMessageReceived != null) {
                        onMessageReceived.accept(mensajeProcesado);
                    }
                });
            }
        } catch (IOException e) {
            Platform.runLater(() -> onMessageReceived.accept("Desconectado del servidor."));
        }
    }

    private String procesarMensaje(String linea) {
        // Parsear lo que viene del servidor (ej: MSG|Pepe|Hola)
        String[] partes = linea.split("\\|", 3);
        String comando = partes[0];

        if ("MSG".equals(comando) && partes.length > 2) {
            return partes[1] + ": " + partes[2];
        } else if ("INFO".equals(comando) && partes.length > 1) {
            return "[SISTEMA]: " + partes[1];
        } else if ("ERROR".equals(comando) && partes.length > 1) {
            return "[ERROR]: " + partes[1];
        } else {
            return linea; // Mensaje crudo si no cumple protocolo
        }
    }

    public void enviarMensaje(String usuario, String contenido) {
        if (out != null) {
            // Protocolo: MSG|Usuario|Contenido
            out.println("MSG|" + usuario + "|" + contenido);
        }
    }

    // Método genérico para enviar comandos crudos
    public void enviarComando(String cabecera, String argumentos) {
        if (out != null) {
            out.println(cabecera + "|" + argumentos);
        }
    }



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
