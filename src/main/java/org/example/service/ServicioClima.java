package org.example.service;

import org.example.service.weather.RespuestaClima;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ServicioClima {

    private static final Logger registrador = LoggerFactory.getLogger(ServicioClima.class);
    private final WebClient clienteWeb;
    private final String claveApi;

    // Puedes obtener una clave API gratuita en OpenWeatherMap.org
    private static final String URL_BASE = "https://api.openweathermap.org/data/2.5/weather";

    public ServicioClima(@Value("${openweathermap.api-key:YOUR_OPENWEATHERMAP_API_KEY}") String claveApi) {
        this.claveApi = claveApi;
        this.clienteWeb = WebClient.builder()
                .baseUrl(URL_BASE)
                .build();
    }

    public Mono<String> obtenerResumenClima(String ciudad) {
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", ciudad)
                        .queryParam("appid", claveApi)
                        .queryParam("units", "metric") // Para obtener temperatura en Celsius
                        .queryParam("lang", "es")    // Para obtener descripción en español
                        .build())
                .retrieve()
                .bodyToMono(RespuestaClima.class)
                .map(respuesta -> {
                    if (respuesta.clima().length > 0) { // Usar respuesta.clima()
                        String descripcion = respuesta.clima()[0].descripcion(); // Usar respuesta.clima()[0].descripcion()
                        double temp = respuesta.principal().temperatura(); // Usar respuesta.principal().temperatura()
                        double sensacionTermica = respuesta.principal().sensacionTermica(); // Usar respuesta.principal().sensacionTermica()
                        return String.format("El tiempo en %s: %s, %.1f°C (sensación térmica %.1f°C).",
                                respuesta.nombre(), descripcion, temp, sensacionTermica); // Usar respuesta.nombre()
                    }
                    return "No se pudo obtener el tiempo para " + ciudad;
                })
                .doOnError(e -> registrador.error("Error al llamar a la API del tiempo para {}: {}", ciudad, e.getMessage()))
                .onErrorResume(e -> Mono.just("No se pudo obtener el tiempo para " + ciudad + ". Error: " + e.getMessage()));
    }
}
