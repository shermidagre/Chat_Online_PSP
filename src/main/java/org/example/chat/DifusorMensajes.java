package org.example.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentMap;

@Component
public class DifusorMensajes { // Renombrado a DifusorMensajes

    private static final Logger registrador = LoggerFactory.getLogger(DifusorMensajes.class); // Renombrado a registrador

    // Inyectaremos GestorUsuarios para acceder a la lista de usuarios online
    private final GestorUsuarios gestorUsuarios; // Renombrado a gestorUsuarios

    public DifusorMensajes(GestorUsuarios gestorUsuarios) { // Renombrado a GestorUsuarios
        this.gestorUsuarios = gestorUsuarios;
    }

    /**
     * Envía un mensaje a todos los usuarios conectados.
     * @param mensaje El mensaje a enviar.
     * @param remitente El nombre del remitente (puede ser null para mensajes del sistema).
     */
    public void difundir(String mensaje, String remitente) { // Renombrado a difundir, mensaje, remitente
        String mensajeCompleto = (remitente != null ? remitente + ": " : "") + mensaje; // Renombrado a mensajeCompleto
        ConcurrentMap<String, ManejadorCliente> usuariosOnline = gestorUsuarios.obtenerUsuariosOnline(); // Renombrado a usuariosOnline y ManejadorCliente

        registrador.info("Difundiendo: {}", mensajeCompleto);

        usuariosOnline.forEach((nombreUsuario, manejador) -> { // Renombrado a nombreUsuario, manejador
            try {
                manejador.enviarMensaje(mensajeCompleto); // Renombrado a enviarMensaje
            } catch (Exception e) {
                registrador.error("Error enviando mensaje a {}: {}", nombreUsuario, e.getMessage());
                // Considerar desconectar al cliente si el error es persistente
            }
        });
    }

    /**
     * Envía un mensaje privado a un usuario específico.
     * @param nombreUsuarioObjetivo El nombre del usuario objetivo.
     * @param mensaje El mensaje a enviar.
     * @param remitente El nombre del remitente (puede ser null para mensajes del sistema).
     * @return true si el mensaje fue enviado, false si el usuario objetivo no está online.
     */
    public boolean enviarMensajePrivado(String nombreUsuarioObjetivo, String mensaje, String remitente) { // Renombrado a enviarMensajePrivado, nombreUsuarioObjetivo, mensaje, remitente
        ManejadorCliente manejador = gestorUsuarios.obtenerManejadorCliente(nombreUsuarioObjetivo); // Renombrado a manejador y obtenerManejadorCliente
        if (manejador != null) {
            String mensajeCompleto = "(DM de " + (remitente != null ? remitente : "Sistema") + "): " + mensaje; // Renombrado a mensajeCompleto
            try {
                manejador.enviarMensaje(mensajeCompleto); // Renombrado a enviarMensaje
                registrador.info("Mensaje privado de {} a {}: {}", remitente, nombreUsuarioObjetivo, mensaje);
                return true;
            } catch (Exception e) {
                registrador.error("Error enviando mensaje privado a {}: {}", nombreUsuarioObjetivo, e.getMessage());
                return false;
            }
        } else {
            registrador.warn("Intento de mensaje privado a usuario '{}' no online.", nombreUsuarioObjetivo);
            return false;
        }
    }
}