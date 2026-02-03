package com.example.chat.socket;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Clase auxiliar para gestionar las conexiones activas de sockets.
 * Permite enviar mensajes a todos los clientes conectados (Broadcast).
 */
public class GestorClientes {
    // Set sincronizado para evitar problemas de concurrencia con múltiples hilos
    private static final Set<ManejadorCliente> clientesConectados = Collections.synchronizedSet(new HashSet<>());

    public static void agregarCliente(ManejadorCliente cliente) {
        clientesConectados.add(cliente);
    }

    public static void eliminarCliente(ManejadorCliente cliente) {
        clientesConectados.remove(cliente);
    }

    /**
     * Envía un mensaje a todos los clientes conectados excepto al remitente (opcional).
     */
    public static void broadcast(String mensaje, ManejadorCliente remitente) {
        synchronized (clientesConectados) {
            for (ManejadorCliente cliente : clientesConectados) {
                // Enviamos a todos (incluido el remitente para que vea su propio mensaje confirmado,
                cliente.enviarMensaje(mensaje);
            }
        }
    }
    /**
     * Busca un usuario por nombre y cierra su conexión.
     * Método necesario para funcionalidad ADMIN (Nivel 3).
     */
    public static boolean expulsarUsuario(String targetUsername) {
        synchronized (clientesConectados) {
            for (ManejadorCliente cliente : clientesConectados) {
                if (cliente.getUsername() != null && cliente.getUsername().equals(targetUsername)) {
                    cliente.enviarMensaje("INFO|Has sido expulsado por el administrador.");
                    try {
                        cliente.getSocket().close(); // Esto provocará excepción en su hilo y cerrará limpieza
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
