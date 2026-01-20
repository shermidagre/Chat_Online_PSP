package org.example.controller;

import org.example.dto.ChatBot.PeticionChat; // Usar PeticionChat
import org.example.dto.ChatBot.RespuestaChat; // Usar RespuestaChat
import org.example.service.ServicioChatbot;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Chatbot Gemini", description = "Endpoints para interactuar con el modelo Gemini.")
public class ControladorChatbot {

    private final ServicioChatbot servicioChatbot;

    public ControladorChatbot(ServicioChatbot servicioChatbot) {
        this.servicioChatbot = servicioChatbot;
    }

    @Operation(summary = "Envía un mensaje al chatbot y recibe la respuesta de Gemini.")
    @PostMapping("/chat")
    public RespuestaChat responderChat(@RequestBody PeticionChat peticion) { // Usar RespuestaChat, PeticionChat
        // Llama al servicio para obtener la respuesta de la IA.
        String respuestaIA = servicioChatbot.obtenerRespuesta(peticion.mensaje());

        // Devuelve el DTO de respuesta.
        return new RespuestaChat(respuestaIA); // Usar RespuestaChat
    }
}