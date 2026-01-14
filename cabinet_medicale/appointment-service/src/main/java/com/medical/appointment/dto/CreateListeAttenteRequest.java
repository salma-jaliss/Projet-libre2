package com.medical.appointment.dto;

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
public class CreateListeAttenteRequest {
    
    @NotNull(message = "L'ID du patient est obligatoire")
    private Long patientId;
    
    @NotNull(message = "L'ID du cabinet est obligatoire")
    private Long cabinetId;
    
    private LocalTime heureArrivee; // Optionnel, par défaut maintenant
    
    private LocalDate dateAttente; // Optionnel, par défaut aujourd'hui
}
