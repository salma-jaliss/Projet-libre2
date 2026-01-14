package medical.cabinet.dto;

import medical.cabinet.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour l'entité Utilisateur
 * Utilisé pour les échanges avec le frontend
 * NOTE: Le mot de passe (pwd) n'est jamais retourné dans les DTOs pour des raisons de sécurité
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UtilisateurDTO {

    private Long id;

    @NotBlank(message = "Le login est obligatoire")
    @Size(max = 50, message = "Le login ne doit pas dépasser 50 caractères")
    private String login;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 100, message = "Le prénom ne doit pas dépasser 100 caractères")
    private String prenom;

    @Size(max = 20, message = "Le numéro de téléphone ne doit pas dépasser 20 caractères")
    private String numTel;

    private String signature; // Signature numérique (Base64) - réservée au médecin

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    private Boolean actif;

    @NotNull(message = "L'ID du cabinet est obligatoire")
    private Long cabinetId;

    // Informations du cabinet (optionnel, pour affichage)
    private String cabinetNom;
}