package org.example.dto.ChatBot;

// La API de Gemini puede devolver múltiples "candidatos" de respuesta.
// Solo necesitamos el primer 'content'.
public record Candidate(Content content) {}