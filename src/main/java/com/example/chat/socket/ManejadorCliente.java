package com.example.chat.socket;
import com.example.chat.service.ServicioChat;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * A thread that handles a single client connected via a socket.
 * Implements the defined plain text protocol.
 */
public class ManejadorCliente implements Runnable {

    private final Socket socket;
    private final ServicioChat servicioChat; // Injected to use the DB
    private PrintWriter out;
    private BufferedReader in;
    private String username = "Anónimo";

    /**
     * Constructs a new client handler.
     * @param socket the client socket.
     * @param servicioChat the chat service.
     */
    public ManejadorCliente(Socket socket, ServicioChat servicioChat) {
        this.socket = socket;
        this.servicioChat = servicioChat;
    }

    /**
     * The main execution method for the client handler thread.
     * It listens for incoming messages from the client and processes them.
     */
    @Override
    public void run() {
        try {
            // Establish communication channels (Plain text, NOT ObjectStream)
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            // Add to the list of active clients
            GestorClientes.agregarCliente(this);
            System.out.println("New client connected from: " + socket.getInetAddress());

            String linea;
            while ((linea = in.readLine()) != null) {
                procesarComando(linea);
            }

        } catch (IOException e) {
            System.err.println("Error in connection with client: " + e.getMessage());
        } finally {
            desconectar();
        }
    }

    /**
     * Processes a command received from the client.
     * The expected format is: COMMAND|ARGUMENT1|ARGUMENT2
     * @param linea the command line received from the client.
     */
    private void procesarComando(String linea) {
        // Example protocol: MSG|Juan|Hola mundo
        String[] partes = linea.split("\\|", 3);
        String comando = partes[0];

        switch (comando) {
            case "LOGIN":
                if (partes.length > 1) {
                    this.username = partes[1];
                    // CORRECTION: Register in DB immediately
                    servicioChat.updateLastSeen(this.username);

                    out.println("OK|Bienvenido " + this.username);
                    GestorClientes.broadcast("INFO|El usuario " + this.username + " ha entrado.", this);
                }
                break;

            case "MSG":
                // Format: MSG|Username|Content
                if (partes.length > 2) {
                    String remitente = partes[1];
                    String contenido = partes[2];

                    // 1. Save to Database (Persistence requirement) using your existing service
                    try {
                        servicioChat.sendMessage(remitente, contenido);
                    } catch (Exception e) {
                        out.println("ERROR|Could not save message to DB");
                    }

                    // 2. Forward to all connected sockets (Real time)
                    GestorClientes.broadcast("MSG|" + remitente + "|" + contenido, this);
                }
                break;

            case "LOGOUT":
                desconectar();
                break;

            default:
                out.println("ERROR|Unknown command");
                break;
        }
    }

    /**
     * Sends a message to the client.
     * @param mensaje the message to send.
     */
    public void enviarMensaje(String mensaje) {
        if (out != null) {
            out.println(mensaje);
        }
    }

    /**
     * Disconnects the client.
     * It removes the client from the list of active clients, broadcasts a
     * "user has left" message, and closes the socket.
     */
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