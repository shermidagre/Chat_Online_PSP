package org.example.dto.ChatBot;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.util.List;

@JsonInclude(Include.NON_NULL)
public record GeminiRequest(
        List<Content> contents,
        @JsonProperty("system_instruction") Content systemInstruction
) {}