package org.example.dto.ChatBot;

// Lo que el usuario envía a tu endpoint POST /api/chatbot/chat
public record ChatRequest(String mensaje) {}