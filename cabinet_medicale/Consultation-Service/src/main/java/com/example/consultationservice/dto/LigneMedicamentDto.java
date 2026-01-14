package com.example.consultationservice.dto;

import lombok.Data;

@Data
public class LigneMedicamentDto {
    private Long id;
    private Long medicamentId;
    private String posologie;
    private Integer quantite;
    private String instructions;
    private Long ordonnanceId;
}