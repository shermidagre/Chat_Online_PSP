package com.example.chat.utils;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Clase estática para cumplir el Nivel 7.
 * Escribe eventos en 'security.log'.
 */
public class SecurityLog {

    private static final String LOG_FILE = "security.log";
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static synchronized void log(String ip, String username, String evento) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
            String timestamp = LocalDateTime.now().format(formatter);
            // Formato: FECHA | IP | USUARIO | EVENTO
            writer.println(String.format("%s | %s | %s | %s", timestamp, ip, username, evento));
        } catch (IOException e) {
            System.err.println("Error escribiendo en log de seguridad: " + e.getMessage());
        }
    }
}