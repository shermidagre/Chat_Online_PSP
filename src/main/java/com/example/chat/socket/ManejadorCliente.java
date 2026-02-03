package com.example.chat.socket;

import com.example.chat.model.Usuario;
import com.example.chat.service.ServicioChat;
import com.example.chat.utils.SecurityLog;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final ServicioChat servicioChat;
    private PrintWriter out;
    private BufferedReader in;

    private String username = null;
    private String role = "GUEST";
    private boolean autenticado = false;

    public ManejadorCliente(Socket socket, ServicioChat servicioChat) {
        this.socket = socket;
        this.servicioChat = servicioChat;
    }

    public String getUsername() { return username; }
    public PrintWriter getOut() { return out; }
    public Socket getSocket() { return socket; }

    @Override
    public void run() {
        String ip = socket.getInetAddress().getHostAddress();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            String linea;
            while ((linea = in.readLine()) != null) {
                String[] partes = linea.split("\\|", 3);
                String comando = partes[0];
                switch (comando) {
                    case "LOGIN":
                        this.username = partes[1];
                        out.println("INFO|Bienvenido " + this.username);
                        GestorClientes.broadcast("INFO|El usuario " + this.username + " ha entrado.", this);
                        break;

                    case "MSG":
                        if (partes.length > 2) {
                            String remitente = partes[1];
                            String contenido = partes[2];
                            // Guardar en BD (Tu código existente)
                            try {
                                servicioChat.sendMessage(remitente, contenido);
                            } catch (Exception e) { /*...*/ }

                            GestorClientes.broadcast("MSG|" + remitente + "|" + contenido, this);
                        }
                        break;

                    case "WHO":
                        // El cliente pidió la lista de usuarios
                        String lista = GestorClientes.obtenerListaUsuarios();
                        out.println("INFO|Usuarios conectados: " + lista);
                        break;

                    case "KICK":
                        // Formato: KICK|Solicitante|Victima
                        if (partes.length > 2) {
                            String solicitante = partes[1];
                            String victima = partes[2];

                            // AQUÍ DEBERÍAS COMPROBAR SI 'solicitante' ES ADMIN EN LA BD
                            // Por ahora lo permitimos a todos para probar:
                            boolean expulsado = GestorClientes.expulsarUsuario(victima);

                            if (expulsado) {
                                GestorClientes.broadcast("INFO|El usuario " + victima + " ha sido expulsado por " + solicitante, null);
                            } else {
                                out.println("ERROR|No se encontró al usuario " + victima);
                            }
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

        } catch (IOException e) {
            System.err.println("Conexión interrumpida con " + ip);
        } finally {
            desconectar();
        }
    }

    private void desconectar() {
        if (autenticado) {
            GestorClientes.eliminarCliente(this);
            GestorClientes.broadcast("INFO|El usuario " + this.username + " ha salido.", this);
            autenticado = false;
        }
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // CORRECCIÓN 3: Restaurado el método que necesita GestorClientes
    public void enviarMensaje(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }
}