// Archivo: src/main/java/org/example/config/ConfiguracionCors.java
package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class ConfiguracionCors { // Renombrado a ConfiguracionCors
    @Bean
    public WebMvcConfigurer configuradorCors() { // Renombrado a configuradorCors
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registro) { // Renombrado a agregarMapeosCors, registro
                // Permite el acceso a TODOS los endpoints de tu API
                registro.addMapping("/**")
                        .allowedOrigins("*")
                        // Permite los métodos que usa tu API (GET, POST, etc.)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*");
            }
        };
    }
}
