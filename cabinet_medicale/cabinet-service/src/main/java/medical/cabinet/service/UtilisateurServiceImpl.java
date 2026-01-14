package medical.cabinet.service;

import medical.cabinet.dto.UtilisateurDTO;
import medical.cabinet.entity.Cabinet;
import medical.cabinet.entity.Utilisateur;
import medical.cabinet.enums.Role;
import medical.cabinet.exception.DuplicateResourceException;
import medical.cabinet.exception.ResourceNotFoundException;
import medical.cabinet.repository.CabinetRepository;
import medical.cabinet.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service Utilisateur
 * Contient la logique métier pour la gestion des utilisateurs
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UtilisateurServiceImpl implements UtilisateurService {

    private final UtilisateurRepository utilisateurRepository;
    private final CabinetRepository cabinetRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UtilisateurDTO creerUtilisateur(UtilisateurDTO utilisateurDTO, String password) {
        log.info("Création d'un nouvel utilisateur: {}", utilisateurDTO.getLogin());

        // Vérifier si le login existe déjà
        if (utilisateurRepository.existsByLogin(utilisateurDTO.getLogin())) {
            throw new DuplicateResourceException("Utilisateur", "login", utilisateurDTO.getLogin());
        }

        // Vérifier que le cabinet existe
        Cabinet cabinet = cabinetRepository.findById(utilisateurDTO.getCabinetId())
                .orElseThrow(() -> new ResourceNotFoundException("Cabinet", "id", utilisateurDTO.getCabinetId()));

        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setLogin(utilisateurDTO.getLogin());
        utilisateur.setPwd(passwordEncoder.encode(password));
        utilisateur.setNom(utilisateurDTO.getNom());
        utilisateur.setPrenom(utilisateurDTO.getPrenom());
        utilisateur.setNumTel(utilisateurDTO.getNumTel());
        utilisateur.setSignature(utilisateurDTO.getSignature());
        utilisateur.setRole(utilisateurDTO.getRole());
        utilisateur.setActif(true);
        utilisateur.setCabinet(cabinet);

        Utilisateur savedUtilisateur = utilisateurRepository.save(utilisateur);
        log.info("Utilisateur créé avec succès: ID={}", savedUtilisateur.getId());

        return convertToDTO(savedUtilisateur);
    }

    @Override
    @Transactional
    public UtilisateurDTO modifierUtilisateur(Long id, UtilisateurDTO utilisateurDTO) {
        log.info("Modification de l'utilisateur ID={}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        // Vérifier si le nouveau login existe déjà (si changé)
        if (!utilisateur.getLogin().equals(utilisateurDTO.getLogin()) &&
                utilisateurRepository.existsByLogin(utilisateurDTO.getLogin())) {
            throw new DuplicateResourceException("Utilisateur", "login", utilisateurDTO.getLogin());
        }

        utilisateur.setLogin(utilisateurDTO.getLogin());
        // on appelle modifierProfil avec les paramètres correspondants
        utilisateur.modifierProfil(utilisateurDTO.getNom(), utilisateurDTO.getPrenom(), utilisateurDTO.getNumTel(), utilisateurDTO.getSignature());
        utilisateur.setRole(utilisateurDTO.getRole());

        Utilisateur updatedUtilisateur = utilisateurRepository.save(utilisateur);
        log.info("Utilisateur modifié avec succès: ID={}", id);

        return convertToDTO(updatedUtilisateur);
    }

    @Override
    @Transactional
    public void supprimerUtilisateur(Long id) {
        log.info("Suppression de l'utilisateur ID={}", id);

        if (!utilisateurRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur", "id", id);
        }

        utilisateurRepository.deleteById(id);
        log.info("Utilisateur supprimé avec succès: ID={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurDTO obtenirUtilisateurParId(Long id) {
        log.info("Récupération de l'utilisateur ID={}", id);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        return convertToDTO(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public UtilisateurDTO obtenirUtilisateurParLogin(String login) {
        log.info("Récupération de l'utilisateur par login: {}", login);

        Utilisateur utilisateur = utilisateurRepository.findByLogin(login)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "login", login));

        return convertToDTO(utilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> obtenirUtilisateursParCabinet(Long cabinetId) {
        log.info("Récupération des utilisateurs du cabinet ID={}", cabinetId);

        return utilisateurRepository.findByCabinetId(cabinetId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> obtenirUtilisateursParRole(Role role) {
        log.info("Récupération des utilisateurs par rôle: {}", role);

        return utilisateurRepository.findByRole(role).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<UtilisateurDTO> obtenirUtilisateursParCabinetEtRole(Long cabinetId, Role role) {
        log.info("Récupération des utilisateurs du cabinet ID={} avec rôle {}", cabinetId, role);

        return utilisateurRepository.findByCabinetIdAndRole(cabinetId, role).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UtilisateurDTO changerStatutUtilisateur(Long id, boolean actif) {
        log.info("Changement du statut de l'utilisateur ID={} à {}", id, actif);

        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur", "id", id));

        utilisateur.setActif(actif);
        Utilisateur updatedUtilisateur = utilisateurRepository.save(utilisateur);

        log.info("Statut de l'utilisateur modifié avec succès: ID={}", id);
        return convertToDTO(updatedUtilisateur);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean loginExiste(String login) {
        return utilisateurRepository.existsByLogin(login);
    }

    /**
     * Convertit une entité Utilisateur en DTO
     * NOTE: Le mot de passe n'est jamais exposé dans le DTO
     */
    private UtilisateurDTO convertToDTO(Utilisateur utilisateur) {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(utilisateur.getId());
        dto.setLogin(utilisateur.getLogin());
        dto.setNom(utilisateur.getNom());
        dto.setPrenom(utilisateur.getPrenom());
        dto.setNumTel(utilisateur.getNumTel());
        dto.setSignature(utilisateur.getSignature());
        dto.setRole(utilisateur.getRole());
        dto.setActif(utilisateur.getActif());
        dto.setCabinetId(utilisateur.getCabinet().getId());
        dto.setCabinetNom(utilisateur.getCabinet().getNom());

        return dto;
    }
}

