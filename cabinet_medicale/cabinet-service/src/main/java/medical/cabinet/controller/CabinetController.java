package medical.cabinet.controller;

import medical.cabinet.dto.CabinetCreateRequest;
import medical.cabinet.dto.CabinetDTO;
import medical.cabinet.service.CabinetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Contrôleur REST pour la gestion des cabinets
 * Endpoints conformes aux cas d'utilisation du cahier des charges
 */
@RestController
@RequestMapping("/api/cabinets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class CabinetController {

    private final CabinetService cabinetService;

    /**
     * Crée un nouveau cabinet avec ses utilisateurs
     * Réservé à l'administrateur
     */
    @PostMapping
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<CabinetDTO> creerCabinet(@Valid @RequestBody CabinetCreateRequest request) {
        log.info("POST /api/cabinets - Création d'un cabinet: {}", request.getNom());
        CabinetDTO createdCabinet = cabinetService.creerCabinet(request);
        return new ResponseEntity<>(createdCabinet, HttpStatus.CREATED);
    }

    /**
     * Modifie un cabinet existant
     * Réservé à l'administrateur
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<CabinetDTO> modifierCabinet(
            @PathVariable Long id,
            @Valid @RequestBody CabinetDTO cabinetDTO) {
        log.info("PUT /api/cabinets/{} - Modification du cabinet", id);
        CabinetDTO updatedCabinet = cabinetService.modifierCabinet(id, cabinetDTO);
        return ResponseEntity.ok(updatedCabinet);
    }

    /**
     * Active un cabinet
     * Réservé à l'administrateur
     * Conforme au diagramme de séquence "4. Création Cabinet et Utilisateurs"
     */
    @PutMapping("/{id}/activate")
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<CabinetDTO> activerCabinet(@PathVariable Long id) {
        log.info("PUT /api/cabinets/{}/activate - Activation du cabinet", id);
        CabinetDTO activatedCabinet = cabinetService.activerCabinet(id);
        return ResponseEntity.ok(activatedCabinet);
    }

    /**
     * Désactive un cabinet
     * Réservé à l'administrateur
     */
    @PutMapping("/{id}/desactiver")
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<CabinetDTO> desactiverCabinet(@PathVariable Long id) {
        log.info("PUT /api/cabinets/{}/desactiver - Désactivation du cabinet", id);
        CabinetDTO deactivatedCabinet = cabinetService.desactiverCabinet(id);
        return ResponseEntity.ok(deactivatedCabinet);
    }

    /**
     * Récupère un cabinet par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CabinetDTO> obtenirCabinetParId(@PathVariable Long id) {
        log.info("GET /api/cabinets/{} - Récupération du cabinet", id);
        CabinetDTO cabinet = cabinetService.obtenirCabinetParId(id);
        return ResponseEntity.ok(cabinet);
    }

    /**
     * Récupère tous les cabinets
     * Réservé à l'administrateur
     */
    @GetMapping
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<List<CabinetDTO>> obtenirTousLesCabinets() {
        log.info("GET /api/cabinets - Récupération de tous les cabinets");
        List<CabinetDTO> cabinets = cabinetService.obtenirTousLesCabinets();
        return ResponseEntity.ok(cabinets);
    }

    /**
     * Récupère les cabinets actifs
     */
    @GetMapping("/actifs")
    public ResponseEntity<List<CabinetDTO>> obtenirCabinetsActifs() {
        log.info("GET /api/cabinets/actifs - Récupération des cabinets actifs");
        List<CabinetDTO> cabinets = cabinetService.obtenirCabinetsActifs();
        return ResponseEntity.ok(cabinets);
    }

    /**
     * Récupère les cabinets par spécialité
     */
    @GetMapping("/specialite/{specialite}")
    public ResponseEntity<List<CabinetDTO>> obtenirCabinetsParSpecialite(@PathVariable String specialite) {
        log.info("GET /api/cabinets/specialite/{} - Récupération par spécialité", specialite);
        List<CabinetDTO> cabinets = cabinetService.obtenirCabinetsParSpecialite(specialite);
        return ResponseEntity.ok(cabinets);
    }

    /**
     * Supprime un cabinet
     * Réservé à l'administrateur
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerCabinet(@PathVariable Long id) {
        log.info("DELETE /api/cabinets/{} - Suppression du cabinet", id);
        cabinetService.supprimerCabinet(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Vérifie si un cabinet existe par son nom
     */
    @GetMapping("/existe")
    public ResponseEntity<Boolean> cabinetExiste(@RequestParam String nom) {
        log.info("GET /api/cabinets/existe?nom={} - Vérification existence", nom);
        boolean existe = cabinetService.cabinetExiste(nom);
        return ResponseEntity.ok(existe);
    }
}