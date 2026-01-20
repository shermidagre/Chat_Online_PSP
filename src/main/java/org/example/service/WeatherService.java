package org.example.service;

import org.example.service.weather.WeatherResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class WeatherService {

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);
    private final WebClient webClient;
    private final String apiKey;

    // Puedes obtener una clave API gratuita en OpenWeatherMap.org
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather";

    public WeatherService(@Value("${openweathermap.api-key:YOUR_OPENWEATHERMAP_API_KEY}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl(BASE_URL)
                .build();
    }

    public Mono<String> getWeatherSummary(String city) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .queryParam("q", city)
                        .queryParam("appid", apiKey)
                        .queryParam("units", "metric") // Para obtener temperatura en Celsius
                        .queryParam("lang", "es")    // Para obtener descripción en español
                        .build())
                .retrieve()
                .bodyToMono(WeatherResponse.class)
                .map(response -> {
                    if (response.weather().length > 0) {
                        String description = response.weather()[0].description();
                        double temp = response.main().temp();
                        double feelsLike = response.main().feelsLike();
                        return String.format("El tiempo en %s: %s, %.1f°C (sensación térmica %.1f°C).",
                                response.name(), description, temp, feelsLike);
                    }
                    return "No se pudo obtener el tiempo para " + city;
                })
                .doOnError(e -> logger.error("Error al llamar a la API del tiempo para {}: {}", city, e.getMessage()))
                .onErrorResume(e -> Mono.just("No se pudo obtener el tiempo para " + city + ". Error: " + e.getMessage()));
    }
}
