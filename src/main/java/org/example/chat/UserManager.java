package org.example.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.logging.SecurityLogger; // Importar SecurityLogger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class UserManager {

    private static final Logger logger = LoggerFactory.getLogger(UserManager.class);

    // Clase interna para representar un usuario de users.json
    private record User(String nombre, String password_sha256, String tipoUsuario) {}

    private final Map<String, User> authorizedUsers; // Usuarios cargados de users.json

    // Almacena los ClientHandler de usuarios conectados, mapeados por su nombre de usuario
    private final ConcurrentMap<String, ClientHandler> onlineUsers = new ConcurrentHashMap<>();
    // Almacena los intentos fallidos de login por dirección IP
    private final ConcurrentMap<String, Integer> failedLoginAttempts = new ConcurrentHashMap<>();
    private static final int MAX_LOGIN_ATTEMPTS = 3;

    private final SecurityLogger securityLogger; // Ahora es un campo final y se inyecta

    public UserManager(SecurityLogger securityLogger) { // Inyectar SecurityLogger
        this.authorizedUsers = loadUsersFromJson();
        this.securityLogger = securityLogger;
    }

    private Map<String, User> loadUsersFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = new ClassPathResource("users.json").getInputStream()) {
            List<User> users = Arrays.asList(mapper.readValue(is, User[].class));
            return users.stream().collect(Collectors.toMap(User::nombre, Function.identity()));
        } catch (IOException e) {
            logger.error("Error al cargar users.json: " + e.getMessage(), e);
            return new ConcurrentHashMap<>(); // Devuelve un mapa vacío si falla
        }
    }

    public boolean authenticate(String username, String password, String clientIp, ClientHandler handler) {
        // Incrementa los intentos fallidos para esta IP
        failedLoginAttempts.merge(clientIp, 1, Integer::sum);

        if (failedLoginAttempts.getOrDefault(clientIp, 0) > MAX_LOGIN_ATTEMPTS) {
            logger.warn("Intentos de login excedidos para IP: {}", clientIp);
            securityLogger.logFailedLoginAttempt(username, clientIp, "Intentos excedidos"); // Usar securityLogger
            return false;
        }

        User user = authorizedUsers.get(username);

        if (user == null) {
            logger.warn("Intento de login fallido: Usuario '{}' no encontrado desde IP: {}", username, clientIp);
            securityLogger.logFailedLoginAttempt(username, clientIp, "Usuario no encontrado"); // Usar securityLogger
            return false;
        }

        String hashedAttemptPassword = generateSha256(password);

        if (hashedAttemptPassword != null && hashedAttemptPassword.equals(user.password_sha256())) {
            logger.info("Login exitoso para usuario '{}' desde IP: {}", username, clientIp);
            securityLogger.logSuccessfulLogin(username, clientIp, "Login exitoso"); // Usar securityLogger
            onlineUsers.put(username, handler);
            handler.setLoggedInUser(username);
            handler.setUserRole(user.tipoUsuario());
            failedLoginAttempts.remove(clientIp); // Resetea intentos fallidos al iniciar sesión
            return true;
        } else {
            logger.warn("Login fallido: Contraseña incorrecta para usuario '{}' desde IP: {}", username, clientIp);
            securityLogger.logFailedLoginAttempt(username, clientIp, "Contraseña incorrecta"); // Usar securityLogger
            return false;
        }
    }

    public void userLoggedIn(String username, ClientHandler handler) {
        onlineUsers.put(username, handler);
        securityLogger.logUserStatusChange(username, handler.getClientSocket().getInetAddress().getHostAddress(), "LOGIN"); // Usar securityLogger
    }

    public void userLoggedOut(String username, String clientIp) {
        onlineUsers.remove(username);
        securityLogger.logUserStatusChange(username, clientIp, "LOGOUT"); // Usar securityLogger
    }

    public boolean isUserOnline(String username) {
        return onlineUsers.containsKey(username);
    }

    public ClientHandler getClientHandler(String username) {
        return onlineUsers.get(username);
    }

    public ConcurrentMap<String, ClientHandler> getOnlineUsers() {
        return onlineUsers;
    }

    public boolean isAdmin(String username) {
        User user = authorizedUsers.get(username);
        return user != null && "ADMIN".equalsIgnoreCase(user.tipoUsuario());
    }

    // Método para resetear los intentos fallidos de una IP (por ejemplo, si el admin la desbloquea)
    public void resetFailedAttempts(String clientIp) {
        failedLoginAttempts.remove(clientIp);
        logger.info("Intentos fallidos reseteados para IP: {}", clientIp);
    }

    // Helper para generar SHA-256
    private String generateSha256(String text) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(text.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            logger.error("Error al generar SHA-256: " + e.getMessage(), e);
            return null;
        }
    }
}
