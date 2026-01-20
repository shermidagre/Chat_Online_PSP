package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class ConfiguracionSeguridad { // Renombrado a ConfiguracionSeguridad

    @Bean
    public SecurityFilterChain cadenaFiltros(HttpSecurity http) throws Exception { // Renombrado a cadenaFiltros
        http
                .csrf(csrf -> csrf.disable()) 
                .authorizeHttpRequests(auth -> auth
                        // 1. REGLA DE ORO: Protegemos la documentación
                        // Usamos las rutas que pusimos en application.properties
                        .requestMatchers("/elvan-instalaciones/**", "/v3/elvan-docs/**").authenticated()
                        
                        // 2. El resto de la API sigue libre (permitAll) como lo tenías
                        .anyRequest().permitAll()
                )
                // 3. Activamos el login básico (la ventanita que sale en el navegador)
                .httpBasic(Customizer.withDefaults());
        
        return http.build();
    }

    // Definimos el usuario que tiene permiso para ver el Swagger
    @Bean
    public UserDetailsService servicioDetallesUsuario(PasswordEncoder codificador) { // Renombrado a servicioDetallesUsuario, codificador
        UserDetails admin = User.withUsername("admin")
                .password(codificador.encode("elvan.2021")) // Esta es la contraseña de acceso
                .roles("ADMIN")
                .build();
        return new InMemoryUserDetailsManager(admin);
    }

    @Bean
    public PasswordEncoder codificadorContrasenas() { // Renombrado a codificadorContrasenas
        return new BCryptPasswordEncoder();
    }
}
