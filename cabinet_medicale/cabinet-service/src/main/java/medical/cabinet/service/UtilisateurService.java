package medical.cabinet.service;

import medical.cabinet.dto.UtilisateurDTO;
import medical.cabinet.enums.Role;

import java.util.List;

/**
 * Interface du service Utilisateur
 * Définit les opérations métier pour la gestion des utilisateurs
 */
public interface UtilisateurService {

    /**
     * Crée un nouvel utilisateur
     */
    UtilisateurDTO creerUtilisateur(UtilisateurDTO utilisateurDTO, String password);

    /**
     * Modifie un utilisateur existant
     */
    UtilisateurDTO modifierUtilisateur(Long id, UtilisateurDTO utilisateurDTO);

    /**
     * Supprime un utilisateur
     */
    void supprimerUtilisateur(Long id);

    /**
     * Récupère un utilisateur par son ID
     */
    UtilisateurDTO obtenirUtilisateurParId(Long id);

    /**
     * Récupère un utilisateur par son login
     */
    UtilisateurDTO obtenirUtilisateurParLogin(String login);

    /**
     * Récupère tous les utilisateurs d'un cabinet
     */
    List<UtilisateurDTO> obtenirUtilisateursParCabinet(Long cabinetId);

    /**
     * Récupère les utilisateurs par rôle
     */
    List<UtilisateurDTO> obtenirUtilisateursParRole(Role role);

    /**
     * Récupère les utilisateurs d'un cabinet par rôle
     */
    List<UtilisateurDTO> obtenirUtilisateursParCabinetEtRole(Long cabinetId, Role role);

    /**
     * Active ou désactive un utilisateur
     */
    UtilisateurDTO changerStatutUtilisateur(Long id, boolean actif);

    /**
     * Vérifie si un login existe
     */
    boolean loginExiste(String login);
}