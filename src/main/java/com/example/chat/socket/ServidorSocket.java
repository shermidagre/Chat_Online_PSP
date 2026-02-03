package com.example.chat.socket;

import com.example.chat.service.ServicioChat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.Socket;

/**
 * Servidor TCP SEGURO (SSL/TLS) que se inicia junto con la aplicación Spring Boot.
 * Escucha en el puerto 9000.
 */
@Component
public class ServidorSocket implements CommandLineRunner {

    private final ServicioChat servicioChat;
    private static final int PUERTO_SOCKET = 9000;

    // Rutas y contraseñas del certificado (Hardcoded para la práctica)
    private static final String KEYSTORE_PATH = "chat_keystore.jks";
    private static final String KEYSTORE_PASSWORD = "123456";

    public ServidorSocket(ServicioChat servicioChat) {
        this.servicioChat = servicioChat;
    }

    @Override
    public void run(String... args) throws Exception {
        // Configuramos las propiedades del sistema para SSL
        System.setProperty("javax.net.ssl.keyStore", KEYSTORE_PATH);
        System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);

        // Iniciamos el servidor en un Hilo nuevo
        new Thread(() -> {
            try {
                // Usamos la fábrica de Sockets SSL en lugar del "new ServerSocket" normal
                SSLServerSocketFactory sslFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                SSLServerSocket serverSocket = (SSLServerSocket) sslFactory.createServerSocket(PUERTO_SOCKET);

                System.out.println("=========================================");
                System.out.println("SERVIDOR SSL (SEGURO) INICIADO EN PUERTO: " + PUERTO_SOCKET);
                System.out.println("ESPERANDO CLIENTES...");
                System.out.println("=========================================");

                while (true) {
                    // accept() devuelve un Socket normal, pero internamente ya está encriptado
                    Socket clienteSocket = serverSocket.accept();

                    ManejadorCliente manejador = new ManejadorCliente(clienteSocket, servicioChat);
                    new Thread(manejador).start();
                }
            } catch (IOException e) {
                System.err.println("Error iniciando servidor SSL: " + e.getMessage());
                e.printStackTrace();
            }
        }).start();
    }
}