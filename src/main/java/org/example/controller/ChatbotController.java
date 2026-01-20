package org.example.controller;

import org.example.dto.ChatBot.ChatRequest; // Importamos los DTOs para el endpoint
import org.example.dto.ChatBot.ChatResponse;
import org.example.service.ChatbotService; // Importamos el servicio
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Chatbot Gemini", description = "Endpoints para interactuar con el modelo Gemini.")
public class ChatbotController {

    private final ChatbotService chatbotService;

    // Spring inyecta automáticamente el servicio.
    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @Operation(summary = "Envía un mensaje al chatbot y recibe la respuesta de Gemini.")
    @PostMapping("/chat")
    public ChatResponse responderChat(@RequestBody ChatRequest request) {
        // Llama al servicio para obtener la respuesta de la IA.
        String respuestaAI = chatbotService.obtenerRespuesta(request.mensaje());

        // Devuelve el DTO de respuesta.
        return new ChatResponse(respuestaAI);
    }
}