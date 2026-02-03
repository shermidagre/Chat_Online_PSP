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
                // o puedes añadir un if (cliente != remitente) si prefieres).
                cliente.enviarMensaje(mensaje);
            }
        }
    }
}
