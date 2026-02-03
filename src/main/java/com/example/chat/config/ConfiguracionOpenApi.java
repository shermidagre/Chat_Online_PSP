package com.example.chat.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Clase de configuración para OpenAPI (Swagger UI).
 * Define la información básica y los metadatos de la API del microservicio de chat.
 */
@Configuration
public class ConfiguracionOpenApi {

    /**
     * Configura el bean {@link OpenAPI} con la información personalizada de la API.
     * @return Una instancia de {@link OpenAPI} con los detalles del microservicio.
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API del Microservicio de Chat") // Título de la API
                        .version("1.0.0") // Versión de la API
                        .description("API para las funcionalidades de chat en tiempo real, convertida de una aplicación Java legacy.") // Descripción de la API
                        .contact(new Contact() // Información de contacto
                                .name("terrenaitor")
                                .email("shermidagre@gmail.com"))
                        .license(new License() // Información de licencia
                                .name("Uso Interno")));
    }
}
