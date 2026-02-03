package com.example.chat.socket;

import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Helper class for managing active socket connections.
 * Allows sending messages to all connected clients (Broadcast).
 */
public class GestorClientes {
    // Synchronized set to avoid concurrency issues with multiple threads
    private static final Set<ManejadorCliente> clientesConectados = Collections.synchronizedSet(new HashSet<>());

    /**
     * Adds a client to the set of connected clients.
     * @param cliente the client handler to add.
     */
    public static void agregarCliente(ManejadorCliente cliente) {
        clientesConectados.add(cliente);
    }

    /**
     * Removes a client from the set of connected clients.
     * @param cliente the client handler to remove.
     */
    public static void eliminarCliente(ManejadorCliente cliente) {
        clientesConectados.remove(cliente);
    }

    /**
     * Sends a message to all connected clients except the sender (optional).
     * @param mensaje the message to send.
     * @param remitente the client handler of the sender.
     */
    public static void broadcast(String mensaje, ManejadorCliente remitente) {
        synchronized (clientesConectados) {
            for (ManejadorCliente cliente : clientesConectados) {
                // We send to everyone (including the sender so they see their own confirmed message,
                // or you can add an if (cliente != remitente) if you prefer).
                cliente.enviarMensaje(mensaje);
            }
        }
    }
}
