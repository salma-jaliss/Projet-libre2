package com.medical.chatbot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CabinetDTO {
    private Long id;
    private String logo;
    private String nom;
    private String specialite;
    private String adresse;
    private String tel;
    private String email; // Ajout du champ manquant
    private LocalDate dateCreation;
    private Boolean actif;
    private Long nombreUtilisateurs;
    private Long nombreMedecins;
}
