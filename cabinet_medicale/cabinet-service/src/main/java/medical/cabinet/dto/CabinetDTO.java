package medical.cabinet.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * DTO pour l'entité Cabinet
 * Utilisé pour les réponses API
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CabinetDTO {

    private Long id;
    private String logo;
    private String nom;
    private String specialite;
    private String adresse;
    private String tel;
    private String email;
    private LocalDate dateCreation;
    private Boolean actif;

    // Statistiques
    private Long nombreUtilisateurs;
    private Long nombreMedecins;
}