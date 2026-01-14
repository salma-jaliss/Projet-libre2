package com.medical.appointment.service;

import com.medical.appointment.dto.CreateRendezVousRequest;
import com.medical.appointment.dto.RendezVousDTO;
import com.medical.appointment.enums.StatutRendezVous;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface du service de gestion des rendez-vous
 * Méthodes selon le diagramme de classe UML:
 * - prendreRendezVous()
 * - modifierRendezvous()
 * - annulerRendezVous()
 * - confirmerRendezvous()
 * - obtenirRendezVousDujour()
 */
public interface RendezVousService {
    // CRUD de base
    RendezVousDTO prendreRendezVous(CreateRendezVousRequest request);
    RendezVousDTO getRendezVousById(Long id);
    List<RendezVousDTO> getAllRendezVous();
    RendezVousDTO modifierRendezvous(Long id, CreateRendezVousRequest request);
    void annulerRendezVous(Long id);
    RendezVousDTO confirmerRendezvous(Long id);
    RendezVousDTO updateStatut(Long id, StatutRendezVous statut);
    
    // Méthodes de recherche
    List<RendezVousDTO> getRendezVousByPatient(Long patientId);
    List<RendezVousDTO> getRendezVousByUtilisateur(Long utilisateurId);
    List<RendezVousDTO> getRendezVousByDateAndUtilisateur(LocalDate date, Long utilisateurId);
    
    // Méthode selon diagramme UML: obtenirRendezVousDujour()
    List<RendezVousDTO> obtenirRendezVousDujour(LocalDate date, Long cabinetId);
    
    // Vérifier disponibilité
    boolean verifierDisponibilite(LocalDate date, java.time.LocalTime heure, Long cabinetId);
}
