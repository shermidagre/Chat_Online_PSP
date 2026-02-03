package com.example.chat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Clase de configuración de seguridad para el microservicio de chat.
 * Define la cadena de filtros de seguridad HTTP, incluyendo la configuración CSRF,
 * las reglas de autorización para las peticiones y el tipo de autenticación.
 */
@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad {

    /**
     * Configura la cadena de filtros de seguridad HTTP.
     * @param http El objeto HttpSecurity para configurar la seguridad web.
     * @return La {@link SecurityFilterChain} configurada.
     * @throws Exception Si ocurre un error durante la configuración de seguridad.
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Deshabilita CSRF (Cross-Site Request Forgery) para la simplicidad en un microservicio API.
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Permite todas las peticiones a los endpoints del API de chat y Swagger UI por ahora.
                        .requestMatchers("/api/chat/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Cualquier otra petición requiere autenticación.
                        .anyRequest().authenticated()
                )
                // Habilita la autenticación HTTP Basic con la configuración por defecto.
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
