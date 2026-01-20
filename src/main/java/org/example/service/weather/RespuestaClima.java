package org.example.service.weather;

import com.fasterxml.jackson.annotation.JsonProperty;

// DTO para la respuesta de la API del tiempo (ej. OpenWeatherMap)
public record RespuestaClima( // Renombrado a RespuestaClima
    Coordenadas coordenadas, // Renombrado a coordenadas
    Clima[] clima, // Renombrado a clima
    String base,
    Principal principal, // Renombrado a principal
    int visibilidad, // Renombrado a visibilidad
    Viento viento, // Renombrado a viento
    Nubes nubes, // Renombrado a nubes
    long dt,
    Sistema sistema, // Renombrado a sistema
    int zonaHoraria, // Renombrado a zonaHoraria
    long id,
    String nombre, // Renombrado a nombre
    int codigo // Renombrado a codigo
) {
    public record Coordenadas( // Renombrado a Coordenadas
        double longitud, // Renombrado a longitud
        double latitud // Renombrado a latitud
    ) {}

    public record Clima( // Renombrado a Clima
        int id,
        String principal, // Renombrado a principal
        String descripcion, // Renombrado a descripcion
        String icono // Renombrado a icono
    ) {}

    public record Principal( // Renombrado a Principal
        double temperatura, // Renombrado a temperatura
        @JsonProperty("feels_like") double sensacionTermica, // Renombrado a sensacionTermica
        @JsonProperty("temp_min") double temperaturaMinima, // Renombrado a temperaturaMinima
        @JsonProperty("temp_max") double temperaturaMaxima, // Renombrado a temperaturaMaxima
        int presion, // Renombrado a presion
        int humedad, // Renombrado a humedad
        @JsonProperty("sea_level") Integer nivelDelMar, // Renombrado a nivelDelMar
        @JsonProperty("grnd_level") Integer nivelDelSuelo // Renombrado a nivelDelSuelo
    ) {}

    public record Viento( // Renombrado a Viento
        double velocidad, // Renombrado a velocidad
        int grados, // Renombrado a grados
        double rafaga // Renombrado a rafaga
    ) {}

    public record Nubes( // Renombrado a Nubes
        int todo // Renombrado a todo
    ) {}

    public record Sistema( // Renombrado a Sistema
        int tipo, // Renombrado a tipo
        long id,
        String pais, // Renombrado a pais
        long amanecer, // Renombrado a amanecer
        long atardecer // Renombrado a atardecer
    ) {}
}