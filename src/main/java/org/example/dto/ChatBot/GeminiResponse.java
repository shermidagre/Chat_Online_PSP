package org.example.dto.ChatBot;

import java.util.List;

// El cuerpo completo de la respuesta que recibes de la API.
public record GeminiResponse(List<Candidate> candidates) {}