package com.example.billingservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "factures")
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class Facture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String numeroFacture; // ex: FACT-2025-001

    private LocalDate dateFacture;

    private LocalDateTime datePaiement;

    private String modePaiement; // ESPECES, CARTE, ASSURANCE

    private Double montantTotal;

    private Double montantPaye;

    private Double montantRestant;

    private String statut; // PAYEE, PARTIELLE, IMPAYEE

    private Long patientId;

    private Long consultationId; // ou liste si plusieurs

    private String pdfPath; // chemin du PDF généré

    // Optionnel : liste de lignes (acte médical + prix)
    @ElementCollection
    private List<String> lignesDescription = new ArrayList<>();

    @ElementCollection
    private List<Double> lignesMontant = new ArrayList<>();
}