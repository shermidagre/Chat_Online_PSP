package com.example.chat.socket;

import com.example.chat.service.ServicioChat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * TCP server that starts with the Spring Boot application.
 * Listens on port 9000 (different from the web's 8080).
 */
@Component
public class ServidorSocket implements CommandLineRunner {

    private final ServicioChat servicioChat;
    private static final int PUERTO_SOCKET = 9000;

    /**
     * Constructs a new socket server.
     * @param servicioChat the chat service.
     */
    public ServidorSocket(ServicioChat servicioChat) {
        this.servicioChat = servicioChat;
    }

    /**
     * This method is executed when the Spring Boot application starts.
     * It starts the TCP socket server in a new thread.
     * @param args command line arguments.
     * @throws Exception if an error occurs.
     */
    @Override
    public void run(String... args) throws Exception {
        // We start the server in a new Thread so as not to block the startup of Spring Web
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PUERTO_SOCKET)) {
                System.out.println("=========================================");
                System.out.println("TCP SOCKET SERVER STARTED ON PORT: " + PUERTO_SOCKET);
                System.out.println("WAITING FOR JAVAFX CLIENTS...");
                System.out.println("=========================================");

                while (true) {
                    Socket clienteSocket = serverSocket.accept();
                    // For each client, we launch a new ManejadorCliente thread
                    // We pass the servicioChat so that the thread can save to the DB
                    ManejadorCliente manejador = new ManejadorCliente(clienteSocket, servicioChat);
                    new Thread(manejador).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}