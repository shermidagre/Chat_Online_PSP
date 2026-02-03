package com.example.chat.client;

import javafx.application.Platform;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
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

    // Configuración SSL (Debe coincidir con la del servidor para esta práctica)
    private static final String TRUSTSTORE_PATH = "chat_keystore.jks";
    private static final String TRUSTSTORE_PASSWORD = "123456";

    private Consumer<String> onMessageReceived;

    public void setOnMessageReceived(Consumer<String> onMessageReceived) {
        this.onMessageReceived = onMessageReceived;
    }

    public void conectar(String host, int puerto, String usuario, String password) throws IOException {
        // 1. Decimos al cliente que confíe en nuestro certificado "casero"
        System.setProperty("javax.net.ssl.trustStore", TRUSTSTORE_PATH);
        System.setProperty("javax.net.ssl.trustStorePassword", TRUSTSTORE_PASSWORD);

        // 2. Creamos el Socket Seguro
        SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
        socket = sslFactory.createSocket(host, puerto);

        // Opcional: Forzar inicio del handshake para verificar conexión inmediata
        ((SSLSocket) socket).startHandshake();

        out = new PrintWriter(socket.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        // Enviamos LOGIN
        out.println("LOGIN|" + usuario + "|" + password);

        escuchando = true;
        new Thread(this::escucharServidor).start();
    }

    private void escucharServidor() {
        try {
            String linea;
            while (escuchando && (linea = in.readLine()) != null) {
                String mensajeOriginal = linea;
                String mensajeProcesado = procesarMensaje(linea);

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
                Platform.runLater(() -> onMessageReceived.accept("ERROR|Desconectado del servidor SSL."));
            }
        }
    }

    private String procesarMensaje(String linea) {
        String[] partes = linea.split("\\|", 3);
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
        if (out != null) out.println("MSG|" + usuario + "|" + contenido);
    }

    public void enviarComando(String tipo, String payload) {
        if (out != null) out.println(tipo + "|" + payload);
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