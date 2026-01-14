package com.medical.appointment.service;

import com.medical.appointment.dto.CreateListeAttenteRequest;
import com.medical.appointment.dto.ListeAttenteDTO;
import com.medical.appointment.entity.ListeAttente;
import com.medical.appointment.enums.StatutListeAttente;
import com.medical.appointment.exception.DuplicateResourceException;
import com.medical.appointment.exception.ResourceNotFoundException;
import com.medical.appointment.mapper.ListeAttenteMapper;
import com.medical.appointment.repository.ListeAttenteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implémentation du service de gestion de la liste d'attente
 * Méthodes selon le diagramme de classe UML:
 * - ajouterPatient()
 * - retirerPatient()
 * - envoyerAuMedecin()
 * - obtenirSuivant()
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ListeAttenteServiceImpl implements ListeAttenteService {

    private final ListeAttenteRepository repository;
    private final ListeAttenteMapper mapper;

    @Override
    public ListeAttenteDTO ajouterPatient(CreateListeAttenteRequest request) {
        // Vérifier si le patient est déjà en attente pour ce cabinet
        LocalDate dateAttente = request.getDateAttente() != null ? request.getDateAttente() : LocalDate.now();
        if (repository.findByPatientIdAndStatut(request.getPatientId(), StatutListeAttente.EN_ATTENTE).isPresent()) {
            throw new DuplicateResourceException("Le patient est déjà dans la liste d'attente");
        }

        ListeAttente entity = mapper.toEntity(request);
        
        // Calculer la position (dernière position + 1)
        Integer maxPosition = repository.findMaxPositionByCabinetIdAndDateAttente(
                request.getCabinetId(), 
                dateAttente
        );
        entity.setPosition(maxPosition + 1);
        
        // Définir date et heure si non fournies
        if (entity.getDateAttente() == null) {
            entity.setDateAttente(LocalDate.now());
        }
        if (entity.getHeureArrivee() == null) {
            entity.setHeureArrivee(LocalTime.now());
        }
        
        ListeAttente saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public void retirerPatient(Long id) {
        ListeAttente entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrée liste d'attente non trouvée avec l'id : " + id));
        
        // Marquer comme terminé et réorganiser les positions
        entity.setStatut(StatutListeAttente.TERMINE);
        repository.save(entity);
        
        // Réorganiser les positions des autres patients
        reorganiserPositions(entity.getCabinetId(), entity.getDateAttente(), entity.getPosition());
    }
    
    private void reorganiserPositions(Long cabinetId, LocalDate dateAttente, Integer positionSupprimee) {
        List<ListeAttente> listes = repository.findByCabinetIdAndDateAttente(cabinetId, dateAttente);
        listes.stream()
                .filter(l -> l.getStatut() == StatutListeAttente.EN_ATTENTE 
                        || l.getStatut() == StatutListeAttente.EN_CONSULTATION)
                .filter(l -> l.getPosition() > positionSupprimee)
                .forEach(l -> {
                    l.setPosition(l.getPosition() - 1);
                    repository.save(l);
                });
    }

    @Override
    public ListeAttenteDTO envoyerAuMedecin(Long id, Long medecinId) {
        ListeAttente entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Entrée liste d'attente non trouvée avec l'id : " + id));
        
        // Le patient est envoyé au médecin (en consultation)
        entity.setStatut(StatutListeAttente.EN_CONSULTATION);
        
        ListeAttente saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public ListeAttenteDTO obtenirSuivant(Long cabinetId) {
        // Logique FIFO: obtenir le premier patient en attente selon la position pour un cabinet
        Optional<ListeAttente> suivant = repository.findFirstByCabinetIdAndStatutOrderByPositionAsc(
                cabinetId, 
                StatutListeAttente.EN_ATTENTE
        );
        
        return suivant.map(mapper::toDTO).orElse(null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListeAttenteDTO> getAllEnAttente() {
        return repository.findByStatutOrderByPositionAsc(StatutListeAttente.EN_ATTENTE).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListeAttenteDTO> getAllEnAttenteByCabinet(Long cabinetId) {
        return repository.findByStatutAndCabinetIdOrderByPositionAsc(StatutListeAttente.EN_ATTENTE, cabinetId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListeAttenteDTO> getByMedecin(Long medecinId) {
        // Note: Cette méthode est dépréciée car medecinId n'existe plus dans ListeAttente
        // On retourne une liste vide
        return List.of();
    }
}
