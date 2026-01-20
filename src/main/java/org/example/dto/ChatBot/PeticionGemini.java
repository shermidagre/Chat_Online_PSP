package org.example.dto.ChatBot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

@JsonInclude(Include.NON_NULL)
public record PeticionGemini( // Renombrado a PeticionGemini
        List<Contenido> contenidos, // Renombrado a contenidos, y Contenido
        @JsonProperty("system_instruction") Contenido instruccionSistema // Renombrado a instruccionSistema, y Contenido
) {}