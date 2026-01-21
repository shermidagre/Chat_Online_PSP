package org.example.chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Servidor {

    private static final int PUERTO = 8081;
    private static final int MAX_CONEXIONES = 10;
    private final ExecutorService poolDeHilos;
    private final Set<ManejadorCliente> clientes = ConcurrentHashMap.newKeySet();

    public Servidor() {
        poolDeHilos = Executors.newFixedThreadPool(MAX_CONEXIONES);
    }

    public void iniciar() {
        System.out.println("Servidor de chat iniciando en el puerto " + PUERTO);
        try (ServerSocket socketServidor = new ServerSocket(PUERTO)) {
            while (true) {
                try {
                    Socket socketCliente = socketServidor.accept();
                    ManejadorCliente manejadorCliente = new ManejadorCliente(socketCliente, this);
                    clientes.add(manejadorCliente);
                    poolDeHilos.execute(manejadorCliente);
                } catch (IOException e) {
                    System.err.println("Error al aceptar conexión de cliente: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.err.println("No se pudo iniciar el servidor en el puerto " + PUERTO + ": " + e.getMessage());
        } finally {
            poolDeHilos.shutdown();
        }
    }

    public void difundirMensaje(String mensaje, ManejadorCliente remitente) {
        String nickRemitente = remitente.getNick() != null ? remitente.getNick() : "Anónimo";
        for (ManejadorCliente cliente : clientes) {
            if (cliente != remitente) {
                cliente.enviarMensaje(nickRemitente + ": " + mensaje);
            }
        }
    }

    public void notificarATodos(String notificacion) {
        for (ManejadorCliente cliente : clientes) {
            cliente.enviarMensaje("NOTIFICACION : " + notificacion);
        }
    }

    public void eliminarCliente(ManejadorCliente cliente) {
        clientes.remove(cliente);
        if (cliente.getNick() != null) {
            System.out.println("Cliente '" + cliente.getNick() + "' desconectado. " + clientes.size() + " usuarios restantes.");
            notificarATodos(cliente.getNick() + " ha salido del chat.");
        }
    }

    public Set<ManejadorCliente> getClientes() {
        return clientes;
    }
}
