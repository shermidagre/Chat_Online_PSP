package org.example.chat;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;

public class ManejadorCliente implements Runnable {

    private final Socket socketCliente;
    private final Servidor servidor;
    private PrintWriter salida;
    private BufferedReader entrada;
    private String nick;

    public ManejadorCliente(Socket socketCliente, Servidor servidor) {
        this.socketCliente = socketCliente;
        this.servidor = servidor;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(socketCliente.getOutputStream(), true);

            // 1. Pedir nick
            salida.println("Bienvenido al chat. Por favor, introduce tu nick:");
            nick = entrada.readLine();
            if (nick == null || nick.trim().isEmpty() || nick.startsWith("/")) {
                nick = "Anónimo-" + (int)(Math.random() * 1000);
                salida.println("Nick inválido. Se te ha asignado: " + nick);
            }
            
            System.out.println("Nuevo cliente conectado: " + nick + ". " + (servidor.getClientes().size()) + " usuarios en total.");
            servidor.notificarATodos(nick + " se ha unido al chat.");
            salida.println("Conectado a la sala. Escribe /bye para salir.");


            // 2. Escuchar mensajes
            String linea;
            while ((linea = entrada.readLine()) != null) {
                if (linea.startsWith("/")) {
                    manejarComando(linea);
                } else {
                    servidor.difundirMensaje(linea, this);
                }
            }
        } catch (IOException e) {
            // Ignorar errores de "Socket closed" que son esperados al desconectar
            if (!"Socket closed".equals(e.getMessage())) {
                 System.err.println("Error de comunicación con " + (nick != null ? nick : "cliente") + ": " + e.getMessage());
            }
        } finally {
            desconectar();
        }
    }

    private void manejarComando(String comando) {
        if ("/bye".equalsIgnoreCase(comando)) {
            // El bucle principal se romperá y el finally se encargará de la desconexión
            try {
                socketCliente.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar el socket para " + nick + ": " + e.getMessage());
            }
        } else if ("/list".equalsIgnoreCase(comando)) {
            StringBuilder listaUsuarios = new StringBuilder("Usuarios online: ");
            for (ManejadorCliente cliente : servidor.getClientes()) {
                listaUsuarios.append(cliente.getNick()).append(" ");
            }
            salida.println(listaUsuarios);
        } else if ("/ping".equalsIgnoreCase(comando)) {
            salida.println("PONG " + LocalDateTime.now());
        } else {
            salida.println("Comando no reconocido: " + comando);
        }
    }

    private void desconectar() {
        servidor.eliminarCliente(this);
        try {
            if (socketCliente != null && !socketCliente.isClosed()) {
                socketCliente.close();
            }
        } catch (IOException e) {
            System.err.println("Error al cerrar el socket del cliente: " + e.getMessage());
        }
    }

    public void enviarMensaje(String mensaje) {
        if (salida != null) {
            salida.println(mensaje);
        }
    }

    public String getNick() {
        return nick;
    }
}
