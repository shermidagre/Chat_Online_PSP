package org.example.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    private final Map<WebSocketSession, String> sesionesActivas = new ConcurrentHashMap<>(); // Map<SESION, NOMBRE_USUARIO>
    private final WebClient webClient; // Inyectaremos el cliente HTTP para comunicarnos con servicio-usuarios

    public ChatWebSocketHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("Nueva conexión WebSocket: " + session.getId());
        session.sendMessage(new TextMessage("Por favor, envía tu token JWT para autenticarte."));
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();

        // Si el usuario no está autenticado, el primer mensaje debe ser el token
        if (!sesionesActivas.containsKey(session)) {
            // Lógica para validar el token llamando a servicio-usuarios
            webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/auth/validate").queryParam("token", payload).build())
                    .retrieve()
                    .bodyToMono(Map.class)
                    .subscribe(
                            response -> {
                                if (response != null && (Boolean) response.getOrDefault("valid", false)) {
                                    String username = (String) response.get("username");
                                    sesionesActivas.put(session, username);
                                    broadcastMessage("SISTEMA: " + username + " se ha unido al chat.");
                                    try {
                                        session.sendMessage(new TextMessage("¡Autenticación exitosa! Bienvenido " + username));
                                    } catch (IOException e) {
                                        System.err.println("Error enviando mensaje de bienvenida: " + e.getMessage());
                                    }
                                } else {
                                    try {
                                        session.sendMessage(new TextMessage("Token inválido. Desconectando."));
                                        session.close(CloseStatus.POLICY_VIOLATION);
                                    } catch (IOException e) {
                                        System.err.println("Error enviando mensaje o cerrando sesión por token inválido: " + e.getMessage());
                                    }
                                }
                            },
                            error -> {
                                System.err.println("Error en la validación del token con servicio-usuarios: " + error.getMessage());
                                try {
                                    session.sendMessage(new TextMessage("Error interno al validar token. Desconectando."));
                                    session.close(CloseStatus.SERVER_ERROR);
                                } catch (IOException e) {
                                    System.err.println("Error enviando mensaje o cerrando sesión por error interno: " + e.getMessage());
                                }
                            }
                    );
            return;
        }

        // Si el usuario ya está autenticado, es un mensaje de chat
        String sender = sesionesActivas.get(session);
        broadcastMessage(sender + ": " + payload);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("Conexión cerrada: " + session.getId() + " con estado " + status.getCode());
        String disconnectedUser = sesionesActivas.remove(session);
        if (disconnectedUser != null) {
            broadcastMessage("SISTEMA: " + disconnectedUser + " ha abandonado el chat.");
        }
    }

    private void broadcastMessage(String message) {
        sesionesActivas.keySet().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(new TextMessage(message));
                }
            } catch (IOException e) {
                System.err.println("Error enviando mensaje a la sesión " + session.getId() + ": " + e.getMessage());
            }
        });
    }
}
