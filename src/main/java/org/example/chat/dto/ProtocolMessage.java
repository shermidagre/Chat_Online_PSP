package org.example.chat.dto;

import java.time.LocalDateTime;

public record ProtocolMessage(
        String type,
        String sender,
        String content,
        LocalDateTime timestamp
) {}
