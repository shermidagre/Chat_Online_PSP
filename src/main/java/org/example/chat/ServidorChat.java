package org.example.chat;

import org.example.logging.SecurityLogger;
import org.example.service.ServidorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServidorChat implements Runnable { // Renombrado a ServidorChat

    private static final Logger registrador = LoggerFactory.getLogger(ServidorChat.class); // Renombrado a registrador
    private static final int PUERTO = 8081; // Renombrado a PUERTO
    private static final int MAX_CONEXIONES = 10; // Renombrado a MAX_CONEXIONES

    private final ExecutorService piscinaManejadoresClientes; // Renombrado a piscinaManejadoresClientes
    private SSLServerSocket socketServidor; // Renombrado a socketServidor
    private boolean enEjecucion = false; // Renombrado a enEjecucion

    private final GestorUsuarios gestorUsuarios; // Renombrado a gestorUsuarios
    private final DifusorMensajes difusorMensajes; // Renombrado a difusorMensajes
    private final SecurityLogger registradorSeguridad; // Renombrado a registradorSeguridad
    private final ServidorService servicioServidor; // Renombrado a servicioServidor

    public ServidorChat(GestorUsuarios gestorUsuarios, DifusorMensajes difusorMensajes, SecurityLogger registradorSeguridad, ServidorService servicioServidor) {
        this.gestorUsuarios = gestorUsuarios;
        this.difusorMensajes = difusorMensajes;
        this.registradorSeguridad = registradorSeguridad;
        this.servicioServidor = servicioServidor;
        this.piscinaManejadoresClientes = Executors.newFixedThreadPool(MAX_CONEXIONES);
    }

    public void iniciar() { // Renombrado a iniciar
        enEjecucion = true;
        try {
            // Configuración SSL/TLS
            System.setProperty("javax.net.ssl.keyStore", "src/main/resources/keystore.jks");
            System.setProperty("javax.net.ssl.keyStorePassword", "password"); // TODO: Usar un mecanismo más seguro
            SSLServerSocketFactory ssf = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            socketServidor = (SSLServerSocket) ssf.createServerSocket(PUERTO);
            registrador.info("ServidorChat iniciado en el puerto {} con SSL/TLS.", PUERTO); // logger a registrador
            new Thread(this).start(); // Inicia el servidor en un nuevo hilo
        } catch (IOException e) {
            registrador.error("Error al iniciar el ServidorChat: " + e.getMessage(), e);
            enEjecucion = false;
        }
    }

    @Override
    public void run() { // run no se traduce porque es de la interfaz Runnable
        while (enEjecucion) {
            try {
                Socket socketCliente = socketServidor.accept(); // Renombrado a socketCliente
                registrador.info("Nuevo cliente conectado desde: {}", socketCliente.getInetAddress().getHostAddress());
                // Pasa 'this' (la instancia de ServidorChat) y servicioServidor al ManejadorCliente
                piscinaManejadoresClientes.execute(new ManejadorCliente(socketCliente, gestorUsuarios, difusorMensajes, registradorSeguridad, this, servicioServidor)); // Renombrado a ManejadorCliente
            } catch (IOException e) {
                if (enEjecucion) { // Solo loguear si el servidor aún debería estar corriendo
                    registrador.error("Error al aceptar conexión de cliente: " + e.getMessage(), e);
                }
            }
        }
    }

    public void detener() { // Renombrado a detener
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