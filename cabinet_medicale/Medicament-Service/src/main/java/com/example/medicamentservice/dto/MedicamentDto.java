package com.example.medicamentservice.dto;

import lombok.Data;

@Data
public class MedicamentDto {
    private Long id;
    private String nomCommercial;
    private String dci;
    private String dosage;
    private String forme;
    private String classeTherapeutique;
    private Double prix;
    private Boolean remboursable;
}