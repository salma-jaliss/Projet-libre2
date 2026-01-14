package com.medical.chatbot.service;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class SessionContext {
    private ChatState state = ChatState.IDLE;
    private LocalDate tempDate;
    private LocalTime tempTime;
    private Long cabinetId;
    private Long pendingRdvId; // ID du RDV en cours d'annulation
    private List<String> conversationHistory = new ArrayList<>(); // Historique de la conversation
    private int errorCount = 0; // Compteur d'erreurs pour aider l'utilisateur
    private String lastIntent; // Dernière intention détectée
    private List<LocalTime> proposedSlots = new ArrayList<>(); // Créneaux proposés à l'utilisateur
    
    public void reset() {
        this.state = ChatState.IDLE;
        this.tempDate = null;
        this.tempTime = null;
        this.pendingRdvId = null;
        this.errorCount = 0;
        this.proposedSlots.clear();
    }
    
    public void addToHistory(String message) {
        conversationHistory.add(message);
        // Garder seulement les 10 derniers messages
        if (conversationHistory.size() > 10) {
            conversationHistory.remove(0);
        }
    }
    
    public enum ChatState {
        IDLE,
        AWAITING_DATE_FOR_AVAILABILITY,
        AWAITING_DATE_FOR_BOOKING,
        AWAITING_TIME_FOR_BOOKING,
        AWAITING_CONFIRMATION,
        AWAITING_RDV_ID_FOR_CANCELLATION
    }
}
