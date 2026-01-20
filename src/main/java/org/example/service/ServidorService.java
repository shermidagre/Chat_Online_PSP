package org.example.service;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class ServidorService {

    private final WeatherService weatherService;

    public ServidorService(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public Mono<String> getWeather(String city) {
        return weatherService.getWeatherSummary(city);
    }
}