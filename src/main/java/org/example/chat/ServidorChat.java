package org.example.chat;

import org.example.logging.SecurityLogger;
import org.example.repository.UsuarioRepository; // Importar UsuarioRepository
import org.example.service.ServidorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder; // Importar PasswordEncoder
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServidorChat implements Runnable {

    private static final Logger registrador = LoggerFactory.getLogger(ServidorChat.class);
    private static final int PUERTO = 8081;
    private static final int MAX_CONEXIONES = 10;

    private final ExecutorService piscinaManejadoresClientes;
    private SSLServerSocket socketServidor;
    private boolean enEjecucion = false;

    private final GestorUsuarios gestorUsuarios;
    private final DifusorMensajes difusorMensajes;
    private final SecurityLogger registradorSeguridad;
    private final ServidorService servicioServidor;

    // Constructor actualizado para inyectar UsuarioRepository y PasswordEncoder
    public ServidorChat(UsuarioRepository repositorioUsuarios, PasswordEncoder codificadorContrasenas, GestorUsuarios gestorUsuarios, DifusorMensajes difusorMensajes, SecurityLogger registradorSeguridad, ServidorService servicioServidor) {
        this.gestorUsuarios = new GestorUsuarios(repositorioUsuarios, codificadorContrasenas, registradorSeguridad); // Se instancia aquí
        this.difusorMensajes = difusorMensajes;
        this.registradorSeguridad = registradorSeguridad;
        this.servicioServidor = servicioServidor;
        this.piscinaManejadoresClientes = Executors.newFixedThreadPool(MAX_CONEXIONES);
    }

    public void iniciar() {
        enEjecucion = true;
        try {
            // Configuración SSL/TLS
            System.setProperty("javax.net.ssl.keyStore", "src/main/resources/keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "password"); // TODO: Usar un mecanismo más seguro
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            socketServidor = (SSLServerSocket) ssf.createServerSocket(PUERTO);
            registrador.info("ServidorChat iniciado en el puerto {} con SSL/TLS.", PUERTO);
            new Thread(this).start();
        } catch (IOException e) {
            registrador.error("Error al iniciar el ServidorChat: " + e.getMessage(), e);
            enEjecucion = false;
        }
    }

    @Override
    public void run() {
        while (enEjecucion) {
            try {
                Socket socketCliente = socketServidor.accept();
                registrador.info("Nuevo cliente conectado desde: {}", socketCliente.getInetAddress().getHostAddress());
                // Pasa 'this' (la instancia de ServidorChat) y servicioServidor al ManejadorCliente
                piscinaManejadoresClientes.execute(new ManejadorCliente(socketCliente, gestorUsuarios, difusorMensajes, registradorSeguridad, this, servicioServidor));
            } catch (IOException e) {
                if (enEjecucion) { // Solo loguear si el servidor aún debería estar corriendo
                    registrador.error("Error al aceptar conexión de cliente: " + e.getMessage(), e);
                }
            }
        }
    }

    public void detener() {
        enEjecucion = false;
        piscinaManejadoresClientes.shutdownNow();
        if (socketServidor != null && !socketServidor.isClosed()) {
            try {
                socketServidor.close();
                registrador.info("ServidorChat detenido.");
            } catch (IOException e) {
                registrador.error("Error al cerrar el SocketServidor: " + e.getMessage(), e);
            }
        }
        registradorSeguridad.close(); // Cerrar el RegistradorSeguridad al detener el servidor
    }
}
