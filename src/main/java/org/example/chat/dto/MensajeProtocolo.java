package org.example.chat.dto;

import java.time.LocalDateTime;

public record MensajeProtocolo( // Renombrado a MensajeProtocolo
        String tipo,
        String remitente,
        String contenido,
        LocalDateTime fechaHora
) {}