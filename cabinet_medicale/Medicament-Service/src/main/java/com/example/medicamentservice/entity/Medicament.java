package com.example.medicamentservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "medicaments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Medicament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nomCommercial; // Ex: Doliprane

    private String dci; // Denomination Commune Internationale (principe actif)

    private String dosage;

    private String forme; // Comprimé, sirop, etc.

    private String classeTherapeutique;

    private Double prix;

    private Boolean remboursable;
}