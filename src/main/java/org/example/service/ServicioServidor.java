package org.example.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ServicioServidor { // Renombrado a ServicioServidor

    private final ServicioClima servicioClima; // Renombrado a servicioClima

    public ServicioServidor(ServicioClima servicioClima) { // Renombrado a ServicioClima
        this.servicioClima = servicioClima;
    }

    public Mono<String> obtenerClima(String ciudad) { // Renombrado a obtenerClima, ciudad
        return servicioClima.obtenerResumenClima(ciudad); // Renombrado a obtenerResumenClima
    }
}
