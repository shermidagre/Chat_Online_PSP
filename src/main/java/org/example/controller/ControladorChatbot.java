package org.example.controller;

import org.example.dto.ChatBot.ChatRequest; // Importamos los DTOs para el endpoint
import org.example.dto.ChatBot.ChatResponse;
import org.example.service.ServicioChatbot; // Importamos el servicio
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Chatbot Gemini", description = "Endpoints para interactuar con el modelo Gemini.") // Mantener nombres de etiquetas técnicas
public class ControladorChatbot { // Renombrado a ControladorChatbot

    private final ServicioChatbot servicioChatbot; // Renombrado a servicioChatbot

    // Spring inyecta automáticamente el servicio.
    public ControladorChatbot(ServicioChatbot servicioChatbot) { // Renombrado a ServicioChatbot
        this.servicioChatbot = servicioChatbot;
    }

    @Operation(summary = "Envía un mensaje al chatbot y recibe la respuesta de Gemini.") // Mantener summary original
    @PostMapping("/chat")
    public ChatResponse responderChat(@RequestBody ChatRequest peticion) { // Renombrado a peticion
        // Llama al servicio para obtener la respuesta de la IA.
        String respuestaIA = servicioChatbot.obtenerRespuesta(peticion.mensaje()); // Renombrado a respuestaIA y peticion.mensaje()

        // Devuelve el DTO de respuesta.
        return new ChatResponse(respuestaIA);
    }
}
