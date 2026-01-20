package org.example.logging;

import org.slf44j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SecurityLogger {

    private static final Logger logger = LoggerFactory.getLogger(SecurityLogger.class);
    private static final String LOG_FILE = "security.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PrintWriter writer;

    public SecurityLogger() {
        try {
            // true para append mode
            this.writer = new PrintWriter(new FileWriter(LOG_FILE, true), true);
            logger.info("SecurityLogger inicializado. Logueando en: {}", LOG_FILE);
        } catch (IOException e) {
            logger.error("Error al inicializar SecurityLogger: " + e.getMessage(), e);
            throw new RuntimeException("No se pudo inicializar SecurityLogger", e);
        }
    }

    private void log(String eventType, String username, String ipAddress, String details) {
        String logEntry = String.format("[%s] [%s] User: %s | IP: %s | Details: %s",
                LocalDateTime.now().format(formatter), eventType, username, ipAddress, details);
        writer.println(logEntry);
        logger.debug("Logged security event: {}", logEntry);
    }

    public void logSuccessfulLogin(String username, String ipAddress, String details) {
        log("LOGIN_SUCCESS", username, ipAddress, details);
    }

    public void logFailedLoginAttempt(String username, String ipAddress, String details) {
        log("LOGIN_FAILURE", username, ipAddress, details);
    }

    public void logAdminCommand(String adminUsername, String ipAddress, String command) {
        log("ADMIN_COMMAND", adminUsername, ipAddress, "Executed command: " + command);
    }

    public void logUserStatusChange(String username, String ipAddress, String status) {
        log("USER_STATUS", username, ipAddress, "Status: " + status);
    }

    // Asegurarse de cerrar el writer cuando la aplicación se apague
    public void close() {
        if (writer != null) {
            writer.close();
            logger.info("SecurityLogger cerrado.");
        }
    }
}
