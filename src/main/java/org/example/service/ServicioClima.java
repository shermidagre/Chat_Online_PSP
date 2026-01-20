package org.example.service;

import org.example.service.weather.WeatherResponse; // Necesita ser RespuestaClima
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ServicioClima { // Renombrado a ServicioClima

    private static final Logger registrador = LoggerFactory.getLogger(ServicioClima.class); // Renombrado a registrador
    private final WebClient clienteWeb; // Renombrado a clienteWeb
    private final String claveApi; // Renombrado a claveApi

    // Puedes obtener una clave API gratuita en OpenWeatherMap.org
    private static final String URL_BASE = "https://api.openweathermap.org/data/2.5/weather"; // Renombrado a URL_BASE

    public ServicioClima(@Value("${openweathermap.api-key:YOUR_OPENWEATHERMAP_API_KEY}") String claveApi) { // Renombrado a claveApi
        this.claveApi = claveApi;
        this.clienteWeb = WebClient.builder()
                .baseUrl(URL_BASE)
                .build();
    }

    public Mono<String> obtenerResumenClima(String ciudad) { // Renombrado a obtenerResumenClima, ciudad
        return clienteWeb.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", ciudad) // ciudad
                        .queryParam("appid", claveApi) // claveApi
                        .queryParam("units", "metric") // Para obtener temperatura en Celsius
                        .queryParam("lang", "es")    // Para obtener descripción en español
                        .build())
                .retrieve()
                .bodyToMono(RespuestaClima.class) // Necesita ser RespuestaClima
                .map(respuesta -> { // Renombrado a respuesta
                    if (respuesta.weather().length > 0) {
                        String descripcion = respuesta.weather()[0].description(); // Renombrado a descripcion
                        double temp = respuesta.main().temp();
                        double sensacionTermica = respuesta.main().feelsLike(); // Renombrado a sensacionTermica
                        return String.format("El tiempo en %s: %s, %.1f°C (sensación térmica %.1f°C).",
                                respuesta.name(), descripcion, temp, sensacionTermica);
                    }
                    return "No se pudo obtener el tiempo para " + ciudad;
                })
                .doOnError(e -> registrador.error("Error al llamar a la API del tiempo para {}: {}", ciudad, e.getMessage()))
                .onErrorResume(e -> Mono.just("No se pudo obtener el tiempo para " + ciudad + ". Error: " + e.getMessage()));
    }
}