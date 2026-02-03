package com.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Clase principal de la aplicación Spring Boot para el microservicio de chat.
 * Esta clase es el punto de entrada para la ejecución del microservicio.
 */
@SpringBootApplication
public class AplicacionMicroservicioChat {

    /**
     * Método principal que inicia la aplicación Spring Boot.
     * @param args Argumentos de la línea de comandos.
     */
    public static void main(String[] args) {
        SpringApplication.run(AplicacionMicroservicioChat.class, args);
    }

}
