package com.medical.appointment.controller;

import com.medical.appointment.dto.CreateRendezVousRequest;
import com.medical.appointment.dto.RendezVousDTO;
import com.medical.appointment.enums.StatutRendezVous;
import com.medical.appointment.service.RendezVousService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Controller pour la gestion des rendez-vous
 * Endpoints selon le diagramme de séquence et le cahier des charges
 */
@RestController
@RequestMapping("/api/rendez-vous")
@RequiredArgsConstructor
public class RendezVousController {

    private final RendezVousService rendezVousService;

    @PostMapping
    public ResponseEntity<RendezVousDTO> prendreRendezVous(@Valid @RequestBody CreateRendezVousRequest request) {
        return new ResponseEntity<>(rendezVousService.prendreRendezVous(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RendezVousDTO> getRendezVousById(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.getRendezVousById(id));
    }

    @GetMapping
    public ResponseEntity<List<RendezVousDTO>> getAllRendezVous() {
        return ResponseEntity.ok(rendezVousService.getAllRendezVous());
    }

    @PutMapping("/{id}")
    public ResponseEntity<RendezVousDTO> modifierRendezvous(@PathVariable Long id, @Valid @RequestBody CreateRendezVousRequest request) {
        return ResponseEntity.ok(rendezVousService.modifierRendezvous(id, request));
    }

    @PatchMapping("/{id}/annuler")
    public ResponseEntity<Void> annulerRendezVous(@PathVariable Long id) {
        rendezVousService.annulerRendezVous(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/confirmer")
    public ResponseEntity<RendezVousDTO> confirmerRendezvous(@PathVariable Long id) {
        return ResponseEntity.ok(rendezVousService.confirmerRendezvous(id));
    }

    @PatchMapping("/{id}/statut")
    public ResponseEntity<RendezVousDTO> updateStatut(@PathVariable Long id, @RequestParam StatutRendezVous statut) {
        return ResponseEntity.ok(rendezVousService.updateStatut(id, statut));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByPatient(patientId));
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<RendezVousDTO>> getRendezVousByUtilisateur(@PathVariable Long utilisateurId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByUtilisateur(utilisateurId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<RendezVousDTO>> searchRendezVous(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long utilisateurId) {
        return ResponseEntity.ok(rendezVousService.getRendezVousByDateAndUtilisateur(date, utilisateurId));
    }

    /**
     * Obtenir les rendez-vous du jour selon le diagramme de classe UML
     * Endpoint: GET /api/rendez-vous/du-jour?date=2025-12-20&cabinetId=1
     */
    @GetMapping("/du-jour")
    public ResponseEntity<List<RendezVousDTO>> obtenirRendezVousDujour(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long cabinetId) {
        return ResponseEntity.ok(rendezVousService.obtenirRendezVousDujour(date, cabinetId));
    }

    /**
     * Vérifier la disponibilité d'un créneau
     * Endpoint: GET /api/rendez-vous/disponibilite?date=2025-12-20&heure=14:00&cabinetId=1
     */
    @GetMapping("/disponibilite")
    public ResponseEntity<Boolean> verifierDisponibilite(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) java.time.LocalTime heure,
            @RequestParam Long cabinetId) {
        return ResponseEntity.ok(rendezVousService.verifierDisponibilite(date, heure, cabinetId));
    }
}
