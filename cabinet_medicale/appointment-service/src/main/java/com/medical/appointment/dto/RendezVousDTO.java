package com.medical.appointment.dto;

import com.medical.appointment.enums.MotifRendezvous;
import com.medical.appointment.enums.StatutRendezVous;
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
public class RendezVousDTO {
    private Long idRendezVous;
    private LocalDate dateRdv;
    private LocalTime heureRdv;
    private MotifRendezvous motif;
    private StatutRendezVous statut;
    private String notes;
    private Long patientId;
    private Long utilisateurId; // ID de l'utilisateur (médecin ou secrétaire)
    private Long cabinetId;
}
