package medical.cabinet.service;

import medical.cabinet.dto.CabinetCreateRequest;
import medical.cabinet.dto.CabinetDTO;

import java.util.List;

/**
 * Interface du service Cabinet
 * Définit les opérations métier pour la gestion des cabinets
 */
public interface CabinetService {

    /**
     * Crée un nouveau cabinet avec ses utilisateurs
     * Conforme au cas d'utilisation administrateur
     */
    CabinetDTO creerCabinet(CabinetCreateRequest request);

    /**
     * Modifie les informations d'un cabinet
     */
    CabinetDTO modifierCabinet(Long id, CabinetDTO cabinetDTO);

    /**
     * Active un cabinet (rend le service disponible)
     */
    CabinetDTO activerCabinet(Long id);

    /**
     * Désactive un cabinet (suspend le service)
     */
    CabinetDTO desactiverCabinet(Long id);

    /**
     * Récupère un cabinet par son ID
     */
    CabinetDTO obtenirCabinetParId(Long id);

    /**
     * Récupère tous les cabinets
     */
    List<CabinetDTO> obtenirTousLesCabinets();

    /**
     * Récupère les cabinets actifs
     */
    List<CabinetDTO> obtenirCabinetsActifs();

    /**
     * Récupère les cabinets par spécialité
     */
    List<CabinetDTO> obtenirCabinetsParSpecialite(String specialite);

    /**
     * Supprime un cabinet
     */
    void supprimerCabinet(Long id);

    /**
     * Vérifie si un cabinet existe par son nom
     */
    boolean cabinetExiste(String nom);
}