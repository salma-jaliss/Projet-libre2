package com.medical.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotRequest {
    private String message;
    private Long patientId;
    private Long cabinetId;
    private String sessionId; // Optional session identifier for anonymous users
}
