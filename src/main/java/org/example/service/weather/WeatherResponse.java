package org.example.service.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO para la respuesta de la API del tiempo (ej. OpenWeatherMap)
public record WeatherResponse(
    Coord coord,
    Weather[] weather,
    String base,
    Main main,
    int visibility,
    Wind wind,
    Clouds clouds,
    long dt,
    Sys sys,
    int timezone,
    long id,
    String name,
    int cod
) {
    public record Coord(
        double lon,
        double lat
    ) {}

    public record Weather(
        int id,
        String main,
        String description,
        String icon
    ) {}

    public record Main(
        double temp,
        @JsonProperty("feels_like") double feelsLike,
        @JsonProperty("temp_min") double tempMin,
        @JsonProperty("temp_max") double tempMax,
        int pressure,
        int humidity,
        @JsonProperty("sea_level") Integer seaLevel, // Puede ser nulo
        @JsonProperty("grnd_level") Integer grndLevel // Puede ser nulo
    ) {}

    public record Wind(
        double speed,
        int deg,
        double gust
    ) {}

    public record Clouds(
        int all
    ) {}

    public record Sys(
        int type,
        long id,
        String country,
        long sunrise,
        long sunset
    ) {}
}
