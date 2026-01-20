package org.example.dto.ChatBot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

// 🚨 DEBE TENER ESTA ANOTACIÓN
@JsonInclude(Include.NON_NULL)
public record Contenido( // Renombrado a Contenido
        List<Parte> partes         // Renombrado a partes, y Parte
        // Se omite si es nulo (e.g., si se usa partes)
) {}