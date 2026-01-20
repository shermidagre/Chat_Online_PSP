package org.example.config;

import org.example.chat.ServidorChat; // Importar ServidorChat
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionServidorChat { // Renombrado a ConfiguracionServidorChat

    @Bean
    public ApplicationRunner ejecutarServidorChat(ServidorChat servidorChat) { // Renombrado a ejecutarServidorChat, servidorChat
        return new ApplicationRunner() {
            @Override
            public void run(ApplicationArguments args) throws Exception {
                // Iniciar el ServidorChat en un hilo separado
                // El ServidorChat.iniciar() ya lo inicia en un nuevo hilo,
                // así que solo necesitamos llamarlo.
                servidorChat.iniciar(); // Renombrado a iniciar

                // Registrar un 'shutdown hook' para detener el servidor de chat de forma segura
                Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                    System.out.println("Deteniendo ServidorChat...");
                    servidorChat.detener(); // Renombrado a detener
                }));
            }
        };
    }
}