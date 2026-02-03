package com.example.chat.socket;
import com.example.chat.service.ServicioChat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Hilo encargado de atender a un único cliente conectado por Socket.
 * Implementa el protocolo de texto plano definido.
 */
public class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final ServicioChat servicioChat; // Inyectado para usar la BD
    private PrintWriter out;
    private BufferedReader in;
    private String username = "Anónimo";

    public ManejadorCliente(Socket socket, ServicioChat servicioChat) {
        this.socket = socket;
        this.servicioChat = servicioChat;
    }

    @Override
    public void run() {
        try {
            // Establecer canales de comunicación (Texto puro, NO ObjectStream)
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Añadir a la lista de activos
            GestorClientes.agregarCliente(this);
            System.out.println("Nuevo cliente conectado desde: " + socket.getInetAddress());

            String linea;
            while ((linea = in.readLine()) != null) {
                procesarComando(linea);
            }

        } catch (IOException e) {
            System.err.println("Error en conexión con cliente: " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    /**
     * PROTOCOLO DE COMUNICACIÓN
     * Formato esperado: COMANDO|ARGUMENTO1|ARGUMENTO2
     */
    private void procesarComando(String linea) {
        // Ejemplo protocolo: MSG|Juan|Hola mundo
        String[] partes = linea.split("\\|", 3);
        String comando = partes[0];

        switch (comando) {
            case "LOGIN":
                if (partes.length > 1) {
                    this.username = partes[1];
                    // CORRECCIÓN: Registrar en BD inmediatamente
                    servicioChat.updateLastSeen(this.username);

                    out.println("OK|Bienvenido " + this.username);
                    GestorClientes.broadcast("INFO|El usuario " + this.username + " ha entrado.", this);
                }
                break;

            case "MSG":
                // Formato: MSG|Username|Contenido
                if (partes.length > 2) {
                    String remitente = partes[1];
                    String contenido = partes[2];

                    // 1. Guardar en Base de Datos (Requisito persistencia) usando tu servicio existente
                    try {
                        servicioChat.sendMessage(remitente, contenido);
                    } catch (Exception e) {
                        out.println("ERROR|No se pudo guardar el mensaje en BD");
                    }

                    // 2. Reenviar a todos los sockets conectados (Tiempo real)
                    GestorClientes.broadcast("MSG|" + remitente + "|" + contenido, this);
                }
                break;

            case "LOGOUT":
                desconectar();
                break;

            default:
                out.println("ERROR|Comando desconocido");
                break;
        }
    }

    public void enviarMensaje(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }

    private void desconectar() {
        try {
            GestorClientes.eliminarCliente(this);
            GestorClientes.broadcast("INFO|El usuario " + this.username + " ha salido.", this);
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}