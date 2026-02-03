package com.example.chat.socket;

import com.example.chat.service.ServicioChat;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class ServidorSocket implements CommandLineRunner {

    private final ServicioChat servicioChat;
    private static final int PUERTO_SOCKET = 9000;

    // Rutas del certificado SSL
    private static final String KEYSTORE_PATH = "chat_keystore.jks";
    private static final String KEYSTORE_PASSWORD = "123456";

    // 2. DEFINIMOS EL LÍMITE DE USUARIOS (POOL DE HILOS)
    // Esto crea un grupo de 10 huecos. Si hay 10 ocupados, el 11 espera.
    private final ExecutorService pool = Executors.newFixedThreadPool(10);

    public ServidorSocket(ServicioChat servicioChat) {
        this.servicioChat = servicioChat;
    }

    @Override
    public void run(String... args) throws Exception {
        System.setProperty("javax.net.ssl.keyStore", KEYSTORE_PATH);
        System.setProperty("javax.net.ssl.keyStorePassword", KEYSTORE_PASSWORD);

        new Thread(() -> {
            try {
                SSLServerSocketFactory sslFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                SSLServerSocket serverSocket = (SSLServerSocket) sslFactory.createServerSocket(PUERTO_SOCKET);

                System.out.println("=== SERVIDOR SSL INICIADO (MÁX 10 USUARIOS) ===");

                while (true) {
                    Socket clienteSocket = serverSocket.accept();
                    ManejadorCliente manejador = new ManejadorCliente(clienteSocket, servicioChat);

                    // 3. EN LUGAR DE 'new Thread(manejador).start()', USAMOS EL POOL:
                    pool.execute(manejador);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
}