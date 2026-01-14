package com.example.consultationservice.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrdonnanceDto {
    private Long id;
    private String type;
    private LocalDateTime dateOrdonnance;
    private Long consultationId;
    private String signaturePath;
    private List<LigneMedicamentDto> lignesMedicaments;
}