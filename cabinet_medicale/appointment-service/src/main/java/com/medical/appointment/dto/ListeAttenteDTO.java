package com.medical.appointment.dto;

import com.medical.appointment.enums.StatutListeAttente;
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
public class ListeAttenteDTO {
    private Long idAttente;
    private Integer position;
    private LocalTime heureArrivee;
    private LocalDate dateAttente;
    private StatutListeAttente statut;
    private Long patientId;
    private Long cabinetId;
}
