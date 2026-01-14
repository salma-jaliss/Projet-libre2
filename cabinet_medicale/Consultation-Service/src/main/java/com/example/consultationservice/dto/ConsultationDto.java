package com.example.consultationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ConsultationDto {
    private Long id;
    private String type;
    private LocalDateTime dateConsultation;
    private String examenClinique;
    private String examenSupplementaire;
    private String diagnostic;
    private String traitement;
    private String observations;
    private Long patientId;
    private List<OrdonnanceDto> ordonnances;
}