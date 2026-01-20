package org.example.service;

import org.example.dto.ChatBot.Candidato; // Importar Candidato
import org.example.dto.ChatBot.Contenido; // Importar Contenido
import org.example.dto.ChatBot.Parte; // Importar Parte
import org.example.dto.ChatBot.PeticionChat; // Importar PeticionChat
import org.example.dto.ChatBot.PeticionGemini; // Importar PeticionGemini
import org.example.dto.ChatBot.RespuestaChat; // Importar RespuestaChat
import org.example.dto.ChatBot.RespuestaGemini; // Importar RespuestaGemini
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class ServicioChatbot { // Renombrado a ServicioChatbot

    private final WebClient clienteWeb; // Renombrado a clienteWeb
    private final String claveApi; // Renombrado a claveApi
    private static final String MODELO = "gemini-1.5-flash"; // Renombrado a MODELO

    public ServicioChatbot(@Value("${gemini.clave-api}") String claveApi) { // Renombrado a ServicioChatbot, claveApi
        this.claveApi = claveApi;
        this.clienteWeb = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String obtenerRespuesta(String mensajeUsuario) { // Ya está en español
        // Instrucción de sistema (Rol del bot)
        String instruccion = "Eres el Asistente de Inventario de una empresa eléctrica. " +
                "Responde de forma técnica pero breve. Si detectas que el usuario te habla por voz, " +
                "sé aún más directo para facilitar la escucha.";
        
        Contenido instruccionSistema = new Contenido(List.of(new Parte(instruccion))); // Usar Contenido, Parte, instruccionSistema
        Contenido contenidoUsuario = new Contenido(List.of(new Parte(mensajeUsuario))); // Usar Contenido, Parte, contenidoUsuario

        PeticionGemini peticion = new PeticionGemini(List.of(contenidoUsuario), instruccionSistema); // Usar PeticionGemini, peticion

        Mono<RespuestaGemini> respuestaMono = clienteWeb.post() // Usar RespuestaGemini, respuestaMono, clienteWeb
                .uri(uriBuilder -> uriBuilder.path(MODELO + ":generateContent").queryParam("key", claveApi).build()) // Usar MODELO, claveApi
                .bodyValue(peticion) // Usar peticion
                .retrieve()
                .bodyToMono(RespuestaGemini.class); // Usar RespuestaGemini

        RespuestaGemini respuesta = respuestaMono.block(); // Usar RespuestaGemini, respuesta
        if (respuesta != null && !respuesta.candidatos().isEmpty()) { // Usar respuesta.candidatos()
            return respuesta.candidatos().get(0).contenido().partes().get(0).texto(); // Usar .contenido().partes().get(0).texto()
        }
        return "No puedo responder en este momento.";
    }
}
