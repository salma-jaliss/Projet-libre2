package com.example.billingservice.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FactureDto {
    private Long id;
    private String numeroFacture;
    private LocalDate dateFacture;
    private LocalDateTime datePaiement;
    private String modePaiement;
    private Double montantTotal;
    private Double montantPaye;
    private Double montantRestant;
    private String statut;
    private Long patientId;
    private Long consultationId;
    private String pdfPath;
    private List<String> lignesDescription;
    private List<Double> lignesMontant;
}