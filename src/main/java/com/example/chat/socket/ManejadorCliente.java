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

                // --- FASE 1: AUTENTICACIÓN ---
                if (!autenticado) {
                    if ("LOGIN".equals(comando) && partes.length == 3) {
                        String user = partes[1];
                        String pass = partes[2];

                        try {
                            // 1. Validar contra Base de Datos (Hash y Usuario)
                            Usuario u = servicioChat.registerUser(user, pass);

                            // 2. Si no lanza excepción, es correcto:
                            this.username = u.getUsername();
                            this.role = (u.getRole() != null) ? u.getRole() : "USER";
                            this.autenticado = true;

                            // 3. Añadir a la lista de conectados
                            GestorClientes.agregarCliente(this);

                            // 4. ENVIAR 'OK' (CRÍTICO PARA QUE JAVAFX CAMBIE DE PANTALLA)
                            out.println("OK|Bienvenido al chat " + this.username);

                            // 5. Notificar a los demás y Loguear
                            GestorClientes.broadcast("INFO|El usuario " + this.username + " ha entrado.", this);
                            SecurityLog.log(ip, username, "LOGIN EXITOSO");

                        } catch (IllegalArgumentException e) {
                            // Contraseña incorrecta o error de servicio
                            out.println("ERROR|Credenciales incorrectas");
                            SecurityLog.log(ip, user, "INTENTO FALLIDO (Pass incorrecta)");
                        }
                    } else {
                        out.println("ERROR|Debes identificarte: LOGIN|Usuario|Password");
                    }
                    continue; // Vuelve a esperar mensaje si no se autenticó
                }

                // --- FASE 2: USUARIO YA AUTENTICADO ---
                switch (comando) {
                    case "MSG":
                        if (partes.length > 2) {
                            String contenido = partes[2];
                            // Guardar en BD
                            try {
                                servicioChat.sendMessage(this.username, contenido);
                            } catch (Exception e) {
                                System.err.println("Error guardando mensaje en BD: " + e.getMessage());
                            }
                            // Enviar a todos
                            GestorClientes.broadcast("MSG|" + this.username + "|" + contenido, this);
                        }
                        break;

                    case "WHO":
                        String lista = GestorClientes.obtenerListaUsuarios();
                        out.println("INFO|Usuarios conectados: " + lista);
                        break;

                    case "KICK":
                        // Solo ADMIN puede expulsar
                        if (partes.length > 1 && "ADMIN".equals(this.role)) {
                            String victima = partes[1];
                            boolean expulsado = GestorClientes.expulsarUsuario(victima);

                            if (expulsado) {
                                GestorClientes.broadcast("INFO|El usuario " + victima + " ha sido expulsado por el admin.", null);
                                SecurityLog.log(ip, username, "ADMIN KICK: " + victima);
                            } else {
                                out.println("ERROR|Usuario no encontrado.");
                            }
                        } else {
                            out.println("ERROR|No tienes permisos de administrador.");
                            SecurityLog.log(ip, username, "INTENTO NO AUTORIZADO DE KICK");
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

    public void enviarMensaje(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }
}