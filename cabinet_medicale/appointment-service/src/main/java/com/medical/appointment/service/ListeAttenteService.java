package com.medical.appointment.service;

import com.medical.appointment.dto.CreateListeAttenteRequest;
import com.medical.appointment.dto.ListeAttenteDTO;
import com.medical.appointment.enums.StatutListeAttente;

import java.util.List;

/**
 * Interface du service de gestion de la liste d'attente
 * Méthodes selon le diagramme de classe UML:
 * - ajouterPatient()
 * - retirerPatient()
 * - envoyerAuMedecin()
 * - obtenirSuivant()
 */
public interface ListeAttenteService {
    ListeAttenteDTO ajouterPatient(CreateListeAttenteRequest request);
    void retirerPatient(Long id);
    ListeAttenteDTO envoyerAuMedecin(Long id, Long medecinId);
    ListeAttenteDTO obtenirSuivant(Long cabinetId); // Récupérer le prochain patient pour un cabinet
    List<ListeAttenteDTO> getAllEnAttente();
    List<ListeAttenteDTO> getAllEnAttenteByCabinet(Long cabinetId);
    List<ListeAttenteDTO> getByMedecin(Long medecinId); // Déprécié, utiliser getAllEnAttenteByCabinet
}
