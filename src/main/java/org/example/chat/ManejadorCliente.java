package org.example.chat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.chat.dto.ProtocolMessage;
import org.example.logging.SecurityLogger;
import org.example.service.ServidorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Optional;

public class ManejadorCliente implements Runnable {

    private static final Logger registrador = LoggerFactory.getLogger(ManejadorCliente.class);
    private final Socket socketCliente;
    private final UserManager gestorUsuarios;
    private final MessageBroadcaster difusorMensajes;
    private final SecurityLogger registradorSeguridad;
    private final ChatServer servidorChat;
    private final ServidorService servicioServidor;
    private BufferedReader entrada;
    private PrintWriter salida;
    private String usuarioLogueado;
    private String rolUsuario;
    private final ObjectMapper mapeadorObjetos;

    public ManejadorCliente(Socket socketCliente, UserManager gestorUsuarios, MessageBroadcaster difusorMensajes, SecurityLogger registradorSeguridad, ChatServer servidorChat, ServidorService servicioServidor) {
        this.socketCliente = socketCliente;
        this.gestorUsuarios = gestorUsuarios;
        this.difusorMensajes = difusorMensajes;
        this.registradorSeguridad = registradorSeguridad;
        this.servidorChat = servidorChat;
        this.servicioServidor = servicioServidor;
        this.mapeadorObjetos = new ObjectMapper();
        this.mapeadorObjetos.findAndRegisterModules();
    }

    public String obtenerUsuarioLogueado() {
        return usuarioLogueado;
    }

    public void establecerUsuarioLogueado(String usuarioLogueado) {
        this.usuarioLogueado = usuarioLogueado;
    }

    public String obtenerRolUsuario() {
        return rolUsuario;
    }

    public void establecerRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public Socket obtenerSocketCliente() {
        return socketCliente;
    }

    @Override
    public void run() {
        try {
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(socketCliente.getOutputStream(), true);

            solicitarAutenticacion();

            String mensajeCliente;
            while (usuarioLogueado != null && (mensajeCliente = entrada.readLine()) != null) {
                registrador.debug("Mensaje recibido de {}: {}", usuarioLogueado, mensajeCliente);
                manejarMensajeCliente(mensajeCliente);
            }
        } catch (IOException e) {
            registrador.error("Error en ManejadorCliente para {}: {}", (usuarioLogueado != null ? usuarioLogueado : socketCliente.getInetAddress().getHostAddress()), e.getMessage());
        } finally {
            desconectarCliente();
        }
    }

    private void solicitarAutenticacion() throws IOException {
        enviarMensaje("AUTENTICACION_REQUERIDA");
        String intentoAutenticacion;
        int intentos = 0;
        final int MAX_INTENTOS_AUTENTICACION = 3;

        while (intentos < MAX_INTENTOS_AUTENTICACION) {
            enviarMensaje("Introduce tu nombre de usuario y contraseña (ej: usuario:contraseña):");
            intentoAutenticacion = entrada.readLine();
            if (intentoAutenticacion == null) {
                registrador.warn("Cliente desconectado durante la autenticación.");
                return;
            }

            Optional<String[]> credenciales = parsearCredenciales(intentoAutenticacion);
            if (credenciales.isPresent()) {
                String nombreUsuario = credenciales.get()[0];
                String contrasena = credenciales.get()[1];
                String ipCliente = socketCliente.getInetAddress().getHostAddress();

                if (gestorUsuarios.authenticate(nombreUsuario, contrasena, ipCliente, this)) {
                    enviarMensaje("AUTENTICACION_EXITOSA");
                    difusorMensajes.broadcast(usuarioLogueado + " se ha unido al chat.", "Sistema");
                    registrador.info("Usuario '{}' autenticado con éxito desde IP: {}", usuarioLogueado, ipCliente);
                    return;
                } else {
                    enviarMensaje("AUTENTICACION_FALLIDA");
                    intentos++;
                    registrador.warn("Intento de autenticación fallido para {} (intentos restantes: {})", nombreUsuario, MAX_INTENTOS_AUTENTICACION - intentos);
                }
            } else {
                enviarMensaje("ERROR_FORMATO: Formato incorrecto. Usa 'usuario:contraseña'.");
                intentos++;
            }
        }
        enviarMensaje("AUTENTICACION_BLOQUEADA: Demasiados intentos fallidos. Conexión cerrada.");
        registrador.warn("Cliente {} bloqueado por exceso de intentos de login.", socketCliente.getInetAddress().getHostAddress());
        registradorSeguridad.logFailedLoginAttempt("IP_BLOQUEADA", socketCliente.getInetAddress().getHostAddress(), "Cliente bloqueado por exceso de intentos.");
        desconectarCliente();
    }

    private Optional<String[]> parsearCredenciales(String cadenaAutenticacion) {
        String[] partes = cadenaAutenticacion.split(":", 2);
        if (partes.length == 2 && !partes[0].trim().isEmpty() && !partes[1].trim().isEmpty()) {
            return Optional.of(new String[]{partes[0].trim(), partes[1].trim()});
        }
        return Optional.empty();
    }

    private void manejarMensajeCliente(String mensajeCrudo) {
        try {
            ProtocolMessage mensaje = mapeadorObjetos.readValue(mensajeCrudo, ProtocolMessage.class);
            switch (mensaje.tipo()) { // Usar mensaje.tipo()
                case "MENSAJE": // Traducir "MESSAGE"
                    difusorMensajes.broadcast(mensaje.contenido(), usuarioLogueado); // Usar mensaje.contenido()
                    break;
                case "COMANDO": // Traducir "COMMAND"
                    manejarComando(mensaje.contenido()); // Usar mensaje.contenido()
                    break;
                default:
                    enviarMensaje("ERROR: Tipo de mensaje no reconocido.");
                    break;
            }
        } catch (IOException e) {
            registrador.warn("Error parseando JSON de {}: {} - Mensaje original: {}", usuarioLogueado, e.getMessage(), mensajeCrudo);
            enviarMensaje("ERROR: Mensaje con formato JSON inválido.");
        }
    }

    private void manejarComando(String comando) {
        String ipCliente = socketCliente.getInetAddress().getHostAddress();
        if (comando.startsWith("/bye")) {
            enviarMensaje("ADIOS: Desconectando...");
            registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, comando);
            desconectarCliente();
        } else if (comando.startsWith("/list")) {
            enviarMensaje("Usuarios online: " + String.join(", ", gestorUsuarios.getOnlineUsers().keySet()));
            registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, comando);
        } else if (comando.startsWith("/ping")) {
            enviarMensaje("PONG " + LocalDateTime.now());
            registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, comando);
        } else if (comando.startsWith("/weather")) {
            registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, comando);
            String[] partes = comando.split(" ", 2);
            String ciudad = (partes.length == 2) ? partes[1].trim() : "madrid";
            servicioServidor.getWeather(ciudad).subscribe(this::enviarMensaje);
        }
        else if (comando.startsWith("/kick")) {
            if (gestorUsuarios.isAdmin(usuarioLogueado)) {
                String[] partes = comando.split(" ", 2);
                if (partes.length == 2) {
                    String usuarioObjetivo = partes[1].trim();
                    ManejadorCliente manejadorObjetivo = gestorUsuarios.getClientHandler(usuarioObjetivo); // getClientHandler will also need to be translated
                    if (manejadorObjetivo != null) {
                        manejadorObjetivo.enviarMensaje("EXPULSADO: Has sido expulsado por un administrador.");
                        manejadorObjetivo.desconectarCliente();
                        difusorMensajes.broadcast(usuarioObjetivo + " ha sido expulsado por " + usuarioLogueado + ".", "Sistema");
                        registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, "Usuario expulsado: " + usuarioObjetivo);
                    } else {
                        enviarMensaje("ERROR: Usuario '" + usuarioObjetivo + "' no encontrado o no online.");
                        registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, "Intento de expulsar usuario inexistente/desconectado: " + usuarioObjetivo);
                    }
                } else {
                    enviarMensaje("ERROR: Uso: /kick <nombre_usuario>");
                    registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, "Formato de comando /kick inválido.");
                }
            } else {
                enviarMensaje("ERROR: Permiso denegado. Solo administradores pueden usar /kick.");
                registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, "Intento no autorizado de /kick.");
            }
        } else if (comando.startsWith("/shutdown")) {
            if (gestorUsuarios.isAdmin(usuarioLogueado)) {
                difusorMensajes.broadcast("El servidor se está apagando por orden de " + usuarioLogueado + ".", "Sistema");
                registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, "Apagado del servidor iniciado.");
                servidorChat.stop();
            } else {
                enviarMensaje("ERROR: Permiso denegado. Solo administradores pueden usar /shutdown.");
                registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, "Intento no autorizado de /shutdown.");
            }
        } else {
            enviarMensaje("ERROR: Comando no reconocido.");
            registradorSeguridad.logAdminCommand(usuarioLogueado, ipCliente, "Comando no reconocido: " + comando);
        }
    }

    public void enviarMensaje(String mensaje) {
        salida.println(mensaje);
    }

    private void desconectarCliente() {
        if (usuarioLogueado != null) {
            gestorUsuarios.userLoggedOut(usuarioLogueado, socketCliente.getInetAddress().getHostAddress()); // userLoggedOut will also need translation
            difusorMensajes.broadcast(usuarioLogueado + " ha abandonado el chat.", "Sistema");
            registrador.info("Usuario '{}' desconectado.", usuarioLogueado);
            registradorSeguridad.logUserStatusChange(usuarioLogueado, socketCliente.getInetAddress().getHostAddress(), "DESCONEXION");
        } else {
            registrador.info("Cliente anónimo desconectado desde IP: {}", socketCliente.getInetAddress().getHostAddress());
            registradorSeguridad.logUserStatusChange("anonimo", socketCliente.getInetAddress().getHostAddress(), "DESCONEXION");
        }

        try {
            if (entrada != null) entrada.close();
            if (salida != null) salida.close();
            if (socketCliente != null && !socketCliente.isClosed()) socketCliente.close(); // clientSocket here
        } catch (IOException e) {
            registrador.error("Error al cerrar recursos del cliente: {}", e.getMessage());
        }
    }
}