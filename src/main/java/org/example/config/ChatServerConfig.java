package org.example.config;

import org.example.chat.ChatServer;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatServerConfig {

    @Bean
    public ApplicationRunner runChatServer(ChatServer chatServer) {
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                // Iniciar el ChatServer en un hilo separado
                // El ChatServer.start() ya lo inicia en un nuevo hilo,
                // así que solo necesitamos llamarlo.
                chatServer.start();

                // Registrar un shutdown hook para detener el servidor de chat de forma segura
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Deteniendo ChatServer...");
                    chatServer.stop();
                }));
            }
        };
    }
}
