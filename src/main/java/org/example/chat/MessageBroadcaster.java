package org.example.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
public class MessageBroadcaster {

    private static final Logger logger = LoggerFactory.getLogger(MessageBroadcaster.class);

    // Inyectaremos UserManager para acceder a la lista de usuarios online
    private final UserManager userManager;

    public MessageBroadcaster(UserManager userManager) {
        this.userManager = userManager;
    }

    /**
     * Envía un mensaje a todos los usuarios conectados.
     * @param message El mensaje a enviar.
     * @param sender El nombre del remitente (puede ser null para mensajes del sistema).
     */
    public void broadcast(String message, String sender) {
        String fullMessage = (sender != null ? sender + ": " : "") + message;
        ConcurrentMap<String, ClientHandler> onlineUsers = userManager.getOnlineUsers();

        logger.info("Broadcasting: {}", fullMessage);

        onlineUsers.forEach((username, handler) -> {
            try {
                handler.sendMessage(fullMessage);
            } catch (Exception e) {
                logger.error("Error enviando mensaje a {}: {}", username, e.getMessage());
                // Considerar desconectar al cliente si el error es persistente
            }
        });
    }

    /**
     * Envía un mensaje privado a un usuario específico.
     * @param targetUsername El nombre del usuario objetivo.
     * @param message El mensaje a enviar.
     * @param sender El nombre del remitente (puede ser null para mensajes del sistema).
     * @return true si el mensaje fue enviado, false si el usuario objetivo no está online.
     */
    public boolean sendPrivateMessage(String targetUsername, String message, String sender) {
        ClientHandler handler = userManager.getClientHandler(targetUsername);
        if (handler != null) {
            String fullMessage = "(DM de " + (sender != null ? sender : "Sistema") + "): " + message;
            try {
                handler.sendMessage(fullMessage);
                logger.info("Mensaje privado de {} a {}: {}", sender, targetUsername, message);
                return true;
            } catch (Exception e) {
                logger.error("Error enviando mensaje privado a {}: {}", targetUsername, e.getMessage());
                return false;
            }
        } else {
            logger.warn("Intento de mensaje privado a usuario '{}' no online.", targetUsername);
            return false;
        }
    }
}
