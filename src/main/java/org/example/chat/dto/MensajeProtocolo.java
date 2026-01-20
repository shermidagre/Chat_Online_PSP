package org.example.chat.dto;

import java.time.LocalDateTime;

public record ProtocolMessage(
        String tipo,
        String remitente,
        String contenido,
        LocalDateTime fechaHora
) {}
