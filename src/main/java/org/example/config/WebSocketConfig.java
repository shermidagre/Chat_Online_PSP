package org.example.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.example.controller.ChatController; // Importamos el ChatController

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

    private final ChatController chatController; // Inyectamos el ChatController

    public WebSocketConfig(ChatController chatController) {
        this.chatController = chatController;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(chatController, "/chat") // Registramos nuestro ChatController para la ruta /chat
                .setAllowedOriginPatterns("*"); // Permitir conexiones desde cualquier origen (CORS para WebSockets)
    }
}
