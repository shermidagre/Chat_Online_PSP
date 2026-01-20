package org.example.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.chat.dto.ProtocolMessage;
import org.example.logging.SecurityLogger;
import org.example.service.WeatherService; // Importar WeatherService
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Optional;

public class ClientHandler implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(ClientHandler.class);
    private final Socket clientSocket;
    private final UserManager userManager;
    private final MessageBroadcaster messageBroadcaster;
    private final SecurityLogger securityLogger;
    private final ChatServer chatServer;
    private final WeatherService weatherService; // Inyectar WeatherService
    private BufferedReader in;
    private PrintWriter out;
    private String loggedInUser;
    private String userRole;
    private final ObjectMapper objectMapper;

    public ClientHandler(Socket clientSocket, UserManager userManager, MessageBroadcaster messageBroadcaster, SecurityLogger securityLogger, ChatServer chatServer, WeatherService weatherService) {
        this.clientSocket = clientSocket;
        this.userManager = userManager;
        this.messageBroadcaster = messageBroadcaster;
        this.securityLogger = securityLogger;
        this.chatServer = chatServer;
        this.weatherService = weatherService; // Inicializar WeatherService
        this.objectMapper = new ObjectMapper();
        this.objectMapper.findAndRegisterModules();
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(String loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public Socket getClientSocket() {
        return clientSocket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            requestAuth();

            String clientMessage;
            while (loggedInUser != null && (clientMessage = in.readLine()) != null) {
                logger.debug("Mensaje recibido de {}: {}", loggedInUser, clientMessage);
                handleClientMessage(clientMessage);
            }
        } catch (IOException e) {
            logger.error("Error en ClientHandler para {}: {}", (loggedInUser != null ? loggedInUser : clientSocket.getInetAddress().getHostAddress()), e.getMessage());
        } finally {
            disconnectClient();
        }
    }

    private void requestAuth() throws IOException {
        sendMessage("AUTH_REQUIRED");
        String authAttempt;
        int attempts = 0;
        final int MAX_AUTH_ATTEMPTS = 3;

        while (attempts < MAX_AUTH_ATTEMPTS) {
            sendMessage("Introduce tu nombre de usuario y contraseña (ej: usuario:contraseña):");
            authAttempt = in.readLine();
            if (authAttempt == null) {
                logger.warn("Cliente desconectado durante la autenticación.");
                return;
            }

            Optional<String[]> credentials = parseCredentials(authAttempt);
            if (credentials.isPresent()) {
                String username = credentials.get()[0];
                String password = credentials.get()[1];
                String clientIp = clientSocket.getInetAddress().getHostAddress();

                if (userManager.authenticate(username, password, clientIp, this)) {
                    sendMessage("AUTH_SUCCESS");
                    messageBroadcaster.broadcast(loggedInUser + " se ha unido al chat.", "Sistema");
                    logger.info("Usuario '{}' autenticado con éxito desde IP: {}", loggedInUser, clientIp);
                    return;
                } else {
                    sendMessage("AUTH_FAILED");
                    attempts++;
                    logger.warn("Intento de autenticación fallido para {} (intentos restantes: {})", username, MAX_AUTH_ATTEMPTS - attempts);
                }
            } else {
                sendMessage("FORMAT_ERROR: Formato incorrecto. Usa 'usuario:contraseña'.");
                attempts++;
            }
        }
        sendMessage("AUTH_BLOCKED: Demasiados intentos fallidos. Conexión cerrada.");
        logger.warn("Cliente {} bloqueado por exceso de intentos de login.", clientSocket.getInetAddress().getHostAddress());
        securityLogger.logFailedLoginAttempt("BLOCKED_IP", clientSocket.getInetAddress().getHostAddress(), "Cliente bloqueado por exceso de intentos.");
        disconnectClient();
    }

    private Optional<String[]> parseCredentials(String authString) {
        String[] parts = authString.split(":", 2);
        if (parts.length == 2 && !parts[0].trim().isEmpty() && !parts[1].trim().isEmpty()) {
            return Optional.of(new String[]{parts[0].trim(), parts[1].trim()});
        }
        return Optional.empty();
    }

    private void handleClientMessage(String rawMessage) {
        try {
            ProtocolMessage message = objectMapper.readValue(rawMessage, ProtocolMessage.class);
            switch (message.type()) {
                case "MESSAGE":
                    messageBroadcaster.broadcast(message.content(), loggedInUser);
                    break;
                case "COMMAND":
                    handleCommand(message.content());
                    break;
                default:
                    sendMessage("ERROR: Tipo de mensaje no reconocido.");
                    break;
            }
        } catch (IOException e) {
            logger.warn("Error parseando JSON de {}: {} - Mensaje original: {}", loggedInUser, e.getMessage(), rawMessage);
            sendMessage("ERROR: Mensaje con formato JSON inválido.");
        }
    }

    private void handleCommand(String command) {
        String clientIp = clientSocket.getInetAddress().getHostAddress();
        if (command.startsWith("/bye")) {
            sendMessage("BYE: Desconectando...");
            securityLogger.logAdminCommand(loggedInUser, clientIp, command);
            disconnectClient();
        } else if (command.startsWith("/list")) {
            sendMessage("Usuarios online: " + String.join(", ", userManager.getOnlineUsers().keySet()));
            securityLogger.logAdminCommand(loggedInUser, clientIp, command);
        } else if (command.startsWith("/ping")) {
            sendMessage("PONG " + LocalDateTime.now());
            securityLogger.logAdminCommand(loggedInUser, clientIp, command);
        } else if (command.startsWith("/weather")) {
            securityLogger.logAdminCommand(loggedInUser, clientIp, command); // Log command usage
            String[] parts = command.split(" ", 2);
            String city = (parts.length == 2) ? parts[1].trim() : "madrid"; // Default to Madrid if no city given
            weatherService.getWeatherSummary(city).subscribe(this::sendMessage);
        }
        else if (command.startsWith("/kick")) {
            if (userManager.isAdmin(loggedInUser)) {
                String[] parts = command.split(" ", 2);
                if (parts.length == 2) {
                    String targetUser = parts[1].trim();
                    ClientHandler targetHandler = userManager.getClientHandler(targetUser);
                    if (targetHandler != null) {
                        targetHandler.sendMessage("KICK: Has sido expulsado por un administrador.");
                        targetHandler.disconnectClient();
                        messageBroadcaster.broadcast(targetUser + " ha sido expulsado por " + loggedInUser + ".", "Sistema");
                        securityLogger.logAdminCommand(loggedInUser, clientIp, "Kicked user: " + targetUser);
                    } else {
                        sendMessage("ERROR: Usuario '" + targetUser + "' no encontrado o no online.");
                        securityLogger.logAdminCommand(loggedInUser, clientIp, "Attempt to kick non-existent/offline user: " + targetUser);
                    }
                } else {
                    sendMessage("ERROR: Uso: /kick <nombre_usuario>");
                    securityLogger.logAdminCommand(loggedInUser, clientIp, "Invalid /kick command format.");
                }
            } else {
                sendMessage("ERROR: Permiso denegado. Solo administradores pueden usar /kick.");
                securityLogger.logAdminCommand(loggedInUser, clientIp, "Unauthorized /kick attempt.");
            }
        } else if (command.startsWith("/shutdown")) {
            if (userManager.isAdmin(loggedInUser)) {
                messageBroadcaster.broadcast("El servidor se está apagando por orden de " + loggedInUser + ".", "Sistema");
                securityLogger.logAdminCommand(loggedInUser, clientIp, "Server shutdown initiated.");
                chatServer.stop();
            } else {
                sendMessage("ERROR: Permiso denegado. Solo administradores pueden usar /shutdown.");
                securityLogger.logAdminCommand(loggedInUser, clientIp, "Unauthorized /shutdown attempt.");
            }
        } else {
            sendMessage("ERROR: Comando no reconocido.");
            securityLogger.logAdminCommand(loggedInUser, clientIp, "Unrecognized command: " + command);
        }
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    private void disconnectClient() {
        if (loggedInUser != null) {
            userManager.userLoggedOut(loggedInUser, clientSocket.getInetAddress().getHostAddress());
            messageBroadcaster.broadcast(loggedInUser + " ha abandonado el chat.", "Sistema");
            logger.info("Usuario '{}' desconectado.", loggedInUser);
            securityLogger.logUserStatusChange(loggedInUser, clientSocket.getInetAddress().getHostAddress(), "DISCONNECT");
        } else {
            logger.info("Cliente anónimo desconectado desde IP: {}", clientSocket.getInetAddress().getHostAddress());
            securityLogger.logUserStatusChange("anonymous", clientSocket.getInetAddress().getHostAddress(), "DISCONNECT");
        }

        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (clientSocket != null && !clientSocket.isClosed()) clientSocket.close();
        } catch (IOException e) {
            logger.error("Error al cerrar recursos del cliente: {}", e.getMessage());
        }
    }
}