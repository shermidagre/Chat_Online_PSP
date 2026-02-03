package com.example.chat.socket;

import com.example.chat.service.ServicioChat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Servidor TCP que se inicia junto con la aplicación Spring Boot.
 * Escucha en el puerto 9000 (distinto al 8080 de la web).
 */
@Component
public class ServidorSocket implements CommandLineRunner {

    private final ServicioChat servicioChat;
    private static final int PUERTO_SOCKET = 9000;

    // Inyectamos el servicio para pasárselo a los hilos clientes
    public ServidorSocket(ServicioChat servicioChat) {
        this.servicioChat = servicioChat;
    }

    @Override
    public void run(String... args) throws Exception {
        // Iniciamos el servidor en un Hilo nuevo para no bloquear el arranque de Spring Web
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(PUERTO_SOCKET)) {
                System.out.println("=========================================");
                System.out.println("SERVIDOR SOCKET TCP INICIADO EN PUERTO: " + PUERTO_SOCKET);
                System.out.println("ESPERANDO CLIENTES JAVAFX...");
                System.out.println("=========================================");

                while (true) {
                    Socket clienteSocket = serverSocket.accept();
                    // Por cada cliente, lanzamos un nuevo hilo ManejadorCliente
                    // Le pasamos el servicioChat para que el hilo pueda guardar en BD
                    ManejadorCliente manejador = new ManejadorCliente(clienteSocket, servicioChat);
                    new Thread(manejador).start();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}