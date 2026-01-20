package org.example.logging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class RegistradorSeguridad { // Renombrado a RegistradorSeguridad

    private static final Logger registrador = LoggerFactory.getLogger(RegistradorSeguridad.class); // Renombrado a registrador
    private static final String ARCHIVO_LOG = "security.log"; // Renombrado a ARCHIVO_LOG
    private static final DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Renombrado a formateador

    private final PrintWriter escritor; // Renombrado a escritor

    public RegistradorSeguridad() {
        try {
            // true para modo de añadir
            this.escritor = new PrintWriter(new FileWriter(ARCHIVO_LOG, true), true);
            registrador.info("RegistradorSeguridad inicializado. Registrando en: {}", ARCHIVO_LOG); // logger a registrador
        } catch (IOException e) {
            registrador.error("Error al inicializar RegistradorSeguridad: " + e.getMessage(), e);
            throw new RuntimeException("No se pudo inicializar RegistradorSeguridad", e);
        }
    }

    private void registrar(String tipoEvento, String nombreUsuario, String direccionIp, String detalles) { // Renombrado a registrar, tipoEvento, nombreUsuario, direccionIp, detalles
        String entradaLog = String.format("[%s] [%s] Usuario: %s | IP: %s | Detalles: %s", // Renombrado a entradaLog
                LocalDateTime.now().format(formateador), tipoEvento, nombreUsuario, direccionIp, detalles);
        escritor.println(entradaLog);
        registrador.debug("Evento de seguridad registrado: {}", entradaLog);
    }

    public void registrarLoginExitoso(String nombreUsuario, String direccionIp, String detalles) { // Renombrado a registrarLoginExitoso
        registrar("LOGIN_EXITOSO", nombreUsuario, direccionIp, detalles); // Renombrado a registrar
    }

    public void registrarIntentoLoginFallido(String nombreUsuario, String direccionIp, String detalles) { // Renombrado a registrarIntentoLoginFallido
        registrar("LOGIN_FALLIDO", nombreUsuario, direccionIp, detalles); // Renombrado a registrar
    }

    public void registrarComandoAdmin(String nombreUsuarioAdmin, String direccionIp, String comando) { // Renombrado a registrarComandoAdmin
        registrar("COMANDO_ADMIN", nombreUsuarioAdmin, direccionIp, "Comando ejecutado: " + comando); // Renombrado a registrar
    }

    public void registrarCambioEstadoUsuario(String nombreUsuario, String direccionIp, String estado) { // Renombrado a registrarCambioEstadoUsuario
        registrar("ESTADO_USUARIO", nombreUsuario, direccionIp, "Estado: " + estado); // Renombrado a registrar
    }

    // Asegurarse de cerrar el escritor cuando la aplicación se apague
    public void cerrar() { // Renombrado a cerrar
        if (escritor != null) {
            escritor.close();
            registrador.info("RegistradorSeguridad cerrado.");
        }
    }
}