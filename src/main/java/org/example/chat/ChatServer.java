package org.example.chat;

import org.example.logging.SecurityLogger;
import org.example.service.ServidorService; // Importar ServidorService
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ChatServer implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ChatServer.class);
    private static final int PORT = 8081; // Puerto para el servidor de chat
    private static final int MAX_CONNECTIONS = 10; // Máximo de conexiones concurrentes

    private final ExecutorService clientHandlingPool;
    private SSLServerSocket serverSocket;
    private boolean running = false;

    private final UserManager userManager;
    private final MessageBroadcaster messageBroadcaster;
    private final SecurityLogger securityLogger;
    private final ServidorService servidorService; // Inyectar ServidorService

    public ChatServer(UserManager userManager, MessageBroadcaster messageBroadcaster, SecurityLogger securityLogger, ServidorService servidorService) {
        this.userManager = userManager;
        this.messageBroadcaster = messageBroadcaster;
        this.securityLogger = securityLogger;
        this.servidorService = servidorService; // Inicializar ServidorService
        this.clientHandlingPool = Executors.newFixedThreadPool(MAX_CONNECTIONS);
    }

    public void start() {
        running = true;
        try {
            // Configuración SSL/TLS
            System.setProperty("javax.net.ssl.keyStore", "src/main/resources/keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "password"); // TODO: Usar un mecanismo más seguro
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            serverSocket = (SSLServerSocket) ssf.createServerSocket(PORT);
            logger.info("ChatServer iniciado en el puerto {} con SSL/TLS.", PORT);
            new Thread(this).start(); // Inicia el servidor en un nuevo hilo
        } catch (IOException e) {
            logger.error("Error al iniciar el ChatServer: " + e.getMessage(), e);
            running = false;
        }
    }

    @Override
    public void run() {
        while (running) {
            try {
                Socket clientSocket = serverSocket.accept();
                logger.info("Nuevo cliente conectado desde: {}", clientSocket.getInetAddress().getHostAddress());
                // Pasa 'this' (la instancia de ChatServer) y servidorService al ClientHandler
                clientHandlingPool.execute(new ClientHandler(clientSocket, userManager, messageBroadcaster, securityLogger, this, servidorService));
            } catch (IOException e) {
                if (running) { // Solo loguear si el servidor aún debería estar corriendo
                    logger.error("Error al aceptar conexión de cliente: " + e.getMessage(), e);
                }
            }
        }
    }

    public void stop() {
        running = false;
        clientHandlingPool.shutdownNow();
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
                logger.info("ChatServer detenido.");
            } catch (IOException e) {
                logger.error("Error al cerrar el ServerSocket: " + e.getMessage(), e);
            }
        }
        securityLogger.close(); // Cerrar el SecurityLogger al detener el servidor
    }
}
