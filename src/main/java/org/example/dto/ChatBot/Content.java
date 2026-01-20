package org.example.dto.ChatBot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

// 🚨 DEBE TENER ESTA ANOTACIÓN
@JsonInclude(Include.NON_NULL)
public record Content(
        List<Part> parts         // Se omite si es nulo (e.g., si se usa functionCall)
        // Se omite si es nulo (e.g., si se usa parts)
) {}