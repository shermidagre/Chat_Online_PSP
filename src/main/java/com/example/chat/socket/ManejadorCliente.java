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

                // --- FASE DE AUTENTICACIÓN ---
                if (!autenticado) {
                    if ("LOGIN".equals(comando) && partes.length == 3) {
                        String user = partes[1];
                        String pass = partes[2];

                        try {
                            // CORRECCIÓN 1: Usamos registerUser (método unificado)
                            Usuario u = servicioChat.registerUser(user, pass);

                            this.username = u.getUsername();
                            // CORRECCIÓN: Si role es null, poner USER por defecto para evitar errores
                            this.role = (u.getRole() != null) ? u.getRole() : "USER";
                            this.autenticado = true;

                            GestorClientes.agregarCliente(this);
                            out.println("OK|Login correcto. Bienvenido " + role);

                            SecurityLog.log(ip, username, "LOGIN EXITOSO");
                            System.out.println("Cliente autenticado: " + username);

                        } catch (IllegalArgumentException e) {
                            // Si falla la contraseña o usuario
                            out.println("ERROR|Credenciales incorrectas");
                            SecurityLog.log(ip, user, "LOGIN FALLIDO");
                        }
                    } else {
                        out.println("ERROR|Debes autenticarte primero: LOGIN|Usuario|Password");
                    }
                    continue;
                }

                // --- FASE AUTENTICADA ---
                switch (comando) {
                    case "MSG":
                        if (partes.length > 2) {
                            String contenido = partes[2];
                            // CORRECCIÓN 2: Usamos sendMessage (nombre nuevo)
                            servicioChat.sendMessage(this.username, contenido);

                            GestorClientes.broadcast("MSG|" + this.username + "|" + contenido, this);
                        }
                        break;

                    case "KICK":
                        if ("ADMIN".equals(this.role) && partes.length > 1) {
                            String usuarioAExpulsar = partes[1];
                            boolean resultado = GestorClientes.expulsarUsuario(usuarioAExpulsar);
                            if (resultado) {
                                out.println("INFO|Usuario " + usuarioAExpulsar + " expulsado.");
                                SecurityLog.log(ip, username, "ADMIN KICK USER: " + usuarioAExpulsar);
                            } else {
                                out.println("ERROR|Usuario no encontrado.");
                            }
                        } else {
                            out.println("ERROR|No tienes permisos de ADMIN.");
                        }
                        break;

                    case "LOGOUT":
                        desconectar();
                        break;

                    default:
                        out.println("ERROR|Comando no reconocido");
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