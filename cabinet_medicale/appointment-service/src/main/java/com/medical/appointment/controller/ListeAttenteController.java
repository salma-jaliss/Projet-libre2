package com.medical.appointment.controller;

import com.medical.appointment.dto.CreateListeAttenteRequest;
import com.medical.appointment.dto.ListeAttenteDTO;
import com.medical.appointment.service.ListeAttenteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller pour la gestion de la liste d'attente
 * Endpoints selon le diagramme de classe UML et le cahier des charges
 */
@RestController
@RequestMapping("/api/liste-attente")
@RequiredArgsConstructor
public class ListeAttenteController {

    private final ListeAttenteService listeAttenteService;

    /**
     * Ajouter un patient à la liste d'attente
     * Selon le diagramme de classe UML: ajouterPatient()
     */
    @PostMapping
    public ResponseEntity<ListeAttenteDTO> ajouterPatient(@Valid @RequestBody CreateListeAttenteRequest request) {
        return new ResponseEntity<>(listeAttenteService.ajouterPatient(request), HttpStatus.CREATED);
    }

    /**
     * Retirer un patient de la liste d'attente
     * Selon le diagramme de classe UML: retirerPatient()
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> retirerPatient(@PathVariable Long id) {
        listeAttenteService.retirerPatient(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Envoyer le dossier du patient au médecin
     * Selon le diagramme de classe UML: envoyerAuMedecin()
     */
    @PatchMapping("/{id}/envoyer/{medecinId}")
    public ResponseEntity<ListeAttenteDTO> envoyerAuMedecin(@PathVariable Long id, @PathVariable Long medecinId) {
        return ResponseEntity.ok(listeAttenteService.envoyerAuMedecin(id, medecinId));
    }

    /**
     * Obtenir le prochain patient en attente
     * Selon le diagramme de classe UML: obtenirSuivant()
     */
    @GetMapping("/suivant")
    public ResponseEntity<ListeAttenteDTO> obtenirSuivant(@RequestParam Long cabinetId) {
        ListeAttenteDTO next = listeAttenteService.obtenirSuivant(cabinetId);
        if (next == null) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(next);
    }
    
    /**
     * Obtenir tous les patients en attente pour un cabinet
     */
    @GetMapping("/cabinet/{cabinetId}")
    public ResponseEntity<List<ListeAttenteDTO>> getAllEnAttenteByCabinet(@PathVariable Long cabinetId) {
        return ResponseEntity.ok(listeAttenteService.getAllEnAttenteByCabinet(cabinetId));
    }

    /**
     * Obtenir tous les patients en attente
     */
    @GetMapping
    public ResponseEntity<List<ListeAttenteDTO>> getAllEnAttente() {
        return ResponseEntity.ok(listeAttenteService.getAllEnAttente());
    }

    /**
     * Obtenir la liste d'attente par médecin (si nécessaire)
     */
    @GetMapping("/medecin/{medecinId}")
    public ResponseEntity<List<ListeAttenteDTO>> getByMedecin(@PathVariable Long medecinId) {
        return ResponseEntity.ok(listeAttenteService.getByMedecin(medecinId));
    }
}
