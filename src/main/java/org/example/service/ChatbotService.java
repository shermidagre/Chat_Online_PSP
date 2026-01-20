package org.example.service;

import org.example.dto.ChatBot.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

@Service
public class ChatbotService {

    private final WebClient webClient;
    private final String apiKey;
    private static final String MODEL = "gemini-1.5-flash";

    public ChatbotService(@Value("${gemini.api-key}") String apiKey) {
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://generativelanguage.googleapis.com/v1beta/models/")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public String obtenerRespuesta(String mensajeUsuario) {
        // Instrucción de sistema (Rol del bot)
        String instruccion = "Eres el Asistente de Inventario de una empresa eléctrica. " +
                "Responde de forma técnica pero breve. Si detectas que el usuario te habla por voz, " +
                "sé aún más directo para facilitar la escucha.";
        
        Content systemInstruction = new Content(List.of(new Part(instruccion)));
        Content userContent = new Content(List.of(new Part(mensajeUsuario)));

        GeminiRequest request = new GeminiRequest(List.of(userContent), systemInstruction);

        Mono<GeminiResponse> responseMono = webClient.post()
                .uri(uriBuilder -> uriBuilder.path(MODEL + ":generateContent").queryParam("key", apiKey).build())
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class);

        GeminiResponse response = responseMono.block();
        if (response != null && !response.candidates().isEmpty()) {
            return response.candidates().get(0).content().parts().get(0).text();
        }
        return "No puedo responder en este momento.";
    }
}