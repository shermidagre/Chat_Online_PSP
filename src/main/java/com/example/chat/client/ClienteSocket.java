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

    // --- CAMBIO IMPORTANTE: AÑADIDO PARÁMETRO PASSWORD ---
    public void conectar(String host, int puerto, String usuario, String password) throws IOException {
        socket = new Socket(host, puerto);
        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // --- CAMBIO PROTOCOLO: Enviamos LOGIN|Usuario|Password ---
        // Si tu servidor aún no valida contraseña, enviarlo así no suele romper nada,
        // pero es necesario si el servidor espera 3 partes.
        out.println("LOGIN|" + usuario + "|" + password);

        escuchando = true;

        // Iniciamos el hilo de escucha
        new Thread(this::escucharServidor).start();
    }

    private void escucharServidor() {
        try {
            String linea;
            // Leemos constantemente lo que llega del servidor
            while (escuchando && (linea = in.readLine()) != null) {

                // Guardamos la línea original para procesarla
                String mensajeOriginal = linea;

                // Procesamos para "embellecer" el texto si es mensaje de chat
                String mensajeProcesado = procesarMensaje(linea);

                // --- CLAVE PARA QUE FUNCIONE EL LOGIN ---
                // Si el mensaje es de protocolo (OK o ERROR), pasamos el ORIGINAL
                // para que el Controller detecte el "OK|".
                // Si es chat, pasamos el procesado.
                final String aEnviar;
                if (mensajeOriginal.startsWith("OK|") || mensajeOriginal.startsWith("ERROR|")) {
                    aEnviar = mensajeOriginal;
                } else {
                    aEnviar = mensajeProcesado;
                }

                if (onMessageReceived != null) {
                    Platform.runLater(() -> onMessageReceived.accept(aEnviar));
                }
            }
        } catch (IOException e) {
            if (escuchando && onMessageReceived != null) {
                Platform.runLater(() -> onMessageReceived.accept("ERROR|Desconectado del servidor."));
            }
        }
    }

    private String procesarMensaje(String linea) {
        // Parsear lo que viene del servidor (ej: MSG|Pepe|Hola)
        String[] partes = linea.split("\\|", 3); // Dividir por pipes

        if (partes.length == 0) return linea;

        String comando = partes[0];

        if ("MSG".equals(comando) && partes.length > 2) {
            return partes[1] + ": " + partes[2];
        } else if ("INFO".equals(comando) && partes.length > 1) {
            return "[SISTEMA]: " + partes[1];
        } else if ("ERROR".equals(comando) && partes.length > 1) {
            return "[ERROR]: " + partes[1];
        } else {
            return linea;
        }
    }

    public void enviarMensaje(String usuario, String contenido) {
        if (out != null) {
            out.println("MSG|" + usuario + "|" + contenido);
        }
    }

    // Método para comandos especiales como /kick o /list
    public void enviarComando(String tipo, String payload) {
        if (out != null) {
            out.println(tipo + "|" + payload);
        }
    }

    public void desconectar() {
        escuchando = false;
        try {
            if (out != null) out.println("LOGOUT");
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}