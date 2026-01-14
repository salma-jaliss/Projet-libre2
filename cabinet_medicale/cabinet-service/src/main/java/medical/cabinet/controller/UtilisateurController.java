package medical.cabinet.controller;

import medical.cabinet.dto.UtilisateurDTO;
import medical.cabinet.enums.Role;
import medical.cabinet.service.UtilisateurService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des utilisateurs
 * Endpoints conformes aux cas d'utilisation du cahier des charges
 * Conforme aux diagrammes de séquence: utilise /api/users
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class UtilisateurController {

    private final UtilisateurService utilisateurService;

    /**
     * Crée un nouvel utilisateur
     * Réservé à l'administrateur
     * Conforme au diagramme de séquence "4. Création Cabinet et Utilisateurs"
     */
    @PostMapping
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UtilisateurDTO> creerUtilisateur(
            @Valid @RequestBody Map<String, Object> request) {
        log.info("POST /api/users - Création d'un utilisateur");

        UtilisateurDTO utilisateurDTO = mapToDTO(request);
        String password = (String) request.get("password");

        UtilisateurDTO createdUtilisateur = utilisateurService.creerUtilisateur(utilisateurDTO, password);
        return new ResponseEntity<>(createdUtilisateur, HttpStatus.CREATED);
    }

    /**
     * Modifie un utilisateur existant
     * Réservé à l'administrateur
     */
    @PutMapping("/{id}")
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UtilisateurDTO> modifierUtilisateur(
            @PathVariable Long id,
            @Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        log.info("PUT /api/users/{} - Modification de l'utilisateur", id);
        UtilisateurDTO updatedUtilisateur = utilisateurService.modifierUtilisateur(id, utilisateurDTO);
        return ResponseEntity.ok(updatedUtilisateur);
    }

    /**
     * Supprime un utilisateur
     * Réservé à l'administrateur
     */
    @DeleteMapping("/{id}")
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<Void> supprimerUtilisateur(@PathVariable Long id) {
        log.info("DELETE /api/users/{} - Suppression de l'utilisateur", id);
        utilisateurService.supprimerUtilisateur(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Récupère un utilisateur par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> obtenirUtilisateurParId(@PathVariable Long id) {
        log.info("GET /api/users/{} - Récupération de l'utilisateur", id);
        UtilisateurDTO utilisateur = utilisateurService.obtenirUtilisateurParId(id);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Récupère un utilisateur par son login
     * Conforme au diagramme de séquence "1. Authentification et Login"
     * Endpoint utilisé par Auth Service: GET /api/users/by-login/{login}
     */
    @GetMapping("/by-login/{login}")
    public ResponseEntity<UtilisateurDTO> obtenirUtilisateurParLogin(@PathVariable String login) {
        log.info("GET /api/users/by-login/{} - Récupération par login", login);
        UtilisateurDTO utilisateur = utilisateurService.obtenirUtilisateurParLogin(login);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Récupère tous les utilisateurs d'un cabinet
     */
    @GetMapping("/cabinet/{cabinetId}")
    public ResponseEntity<List<UtilisateurDTO>> obtenirUtilisateursParCabinet(@PathVariable Long cabinetId) {
        log.info("GET /api/users/cabinet/{} - Récupération par cabinet", cabinetId);
        List<UtilisateurDTO> utilisateurs = utilisateurService.obtenirUtilisateursParCabinet(cabinetId);
        return ResponseEntity.ok(utilisateurs);
    }

    /**
     * Récupère les utilisateurs par rôle
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<UtilisateurDTO>> obtenirUtilisateursParRole(@PathVariable Role role) {
        log.info("GET /api/users/role/{} - Récupération par rôle", role);
        List<UtilisateurDTO> utilisateurs = utilisateurService.obtenirUtilisateursParRole(role);
        return ResponseEntity.ok(utilisateurs);
    }

    /**
     * Récupère les utilisateurs d'un cabinet par rôle
     */
    @GetMapping("/cabinet/{cabinetId}/role/{role}")
    public ResponseEntity<List<UtilisateurDTO>> obtenirUtilisateursParCabinetEtRole(
            @PathVariable Long cabinetId,
            @PathVariable Role role) {
        log.info("GET /api/users/cabinet/{}/role/{} - Récupération par cabinet et rôle", cabinetId, role);
        List<UtilisateurDTO> utilisateurs = utilisateurService.obtenirUtilisateursParCabinetEtRole(cabinetId, role);
        return ResponseEntity.ok(utilisateurs);
    }

    /**
     * Active ou désactive un utilisateur
     * Réservé à l'administrateur
     */
    @PutMapping("/{id}/statut")
    // @PreAuthorize("hasRole('ADMINISTRATEUR')")
    public ResponseEntity<UtilisateurDTO> changerStatutUtilisateur(
            @PathVariable Long id,
            @RequestParam boolean actif) {
        log.info("PUT /api/users/{}/statut - Changement de statut à {}", id, actif);
        UtilisateurDTO utilisateur = utilisateurService.changerStatutUtilisateur(id, actif);
        return ResponseEntity.ok(utilisateur);
    }

    /**
     * Vérifie si un login existe
     */
    @GetMapping("/existe")
    public ResponseEntity<Boolean> loginExiste(@RequestParam String login) {
        log.info("GET /api/users/existe?login={} - Vérification existence", login);
        boolean existe = utilisateurService.loginExiste(login);
        return ResponseEntity.ok(existe);
    }

    /**
     * Convertit une Map en UtilisateurDTO
     */
    private UtilisateurDTO mapToDTO(Map<String, Object> request) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setLogin((String) request.get("login"));
        dto.setNom((String) request.get("nom"));
        dto.setPrenom((String) request.get("prenom"));
        dto.setNumTel((String) request.get("numTel"));
        dto.setSignature((String) request.get("signature"));
        dto.setRole(Role.valueOf((String) request.get("role")));
        dto.setCabinetId(Long.valueOf(request.get("cabinetId").toString()));
        return dto;
    }
}