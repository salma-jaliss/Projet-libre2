package medical.cabinet.dto;

import medical.cabinet.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO pour la création d'un utilisateur
 * Utilisé par l'Administrateur lors de la création d'un cabinet
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUtilisateurRequest {

    @NotBlank(message = "Le login est obligatoire")
    @Size(min = 3, max = 50, message = "Le login doit contenir entre 3 et 50 caractères")
    private String login;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String pwd;

    @NotBlank(message = "Le nom est obligatoire")
    @Size(max = 50, message = "Le nom ne peut pas dépasser 50 caractères")
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Size(max = 50, message = "Le prénom ne peut pas dépasser 50 caractères")
    private String prenom;

    @Size(max = 20, message = "Le numéro de téléphone ne peut pas dépasser 20 caractères")
    private String numTel;

    private String signature; // Optionnel, réservé aux médecins

    @NotNull(message = "Le rôle est obligatoire")
    private Role role;

    @NotNull(message = "L'ID du cabinet est obligatoire")
    private Long cabinetId;
}