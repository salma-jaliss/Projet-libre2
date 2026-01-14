package com.medical.chatbot.dto;

import com.medical.chatbot.enums.MotifRendezvous;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateRendezVousRequest {

    @NotNull(message = "La date du rendez-vous est obligatoire")
    private LocalDate dateRdv;

    @NotNull(message = "L'heure du rendez-vous est obligatoire")
    private LocalTime heureRdv;

    @NotNull(message = "Le motif du rendez-vous est obligatoire")
    private MotifRendezvous motif;
    
    private String notes;

    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;

    @NotNull(message = "L'ID de l'utilisateur est obligatoire")
    private Long utilisateurId; // ID de l'utilisateur (médecin ou secrétaire)

    @NotNull(message = "L'ID du cabinet est obligatoire")
    private Long cabinetId;
}
