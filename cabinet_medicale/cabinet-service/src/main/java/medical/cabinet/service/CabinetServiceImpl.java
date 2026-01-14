package medical.cabinet.service;

import medical.cabinet.dto.CabinetCreateRequest;
import medical.cabinet.dto.CabinetDTO;
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
 * Implémentation du service Cabinet
 * Contient la logique métier pour la gestion des cabinets
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CabinetServiceImpl implements CabinetService {

    private final CabinetRepository cabinetRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public CabinetDTO creerCabinet(CabinetCreateRequest request) {
        log.info("Création d'un nouveau cabinet: {}", request.getNom());

        // Vérifier si le cabinet existe déjà
        if (cabinetRepository.existsByNom(request.getNom())) {
            throw new DuplicateResourceException("Cabinet", "nom", request.getNom());
        }

        // Créer l'entité Cabinet
        Cabinet cabinet = new Cabinet();
        cabinet.setLogo(request.getLogo());
        cabinet.setNom(request.getNom());
        cabinet.setSpecialite(request.getSpecialite());
        cabinet.setAdresse(request.getAdresse());
        cabinet.setTel(request.getTel());
        cabinet.setEmail(request.getEmail());
        cabinet.creerCabinet(); // Méthode du diagramme de classes

        // Sauvegarder le cabinet
        Cabinet savedCabinet = cabinetRepository.save(cabinet);

        // Créer les utilisateurs associés (si fournis)
        if (request.getUtilisateurs() != null) {
            for (CabinetCreateRequest.UtilisateurCreateRequest userReq : request.getUtilisateurs()) {
                // Vérifier si le login existe déjà
                if (utilisateurRepository.existsByLogin(userReq.getLogin())) {
                    throw new DuplicateResourceException("Utilisateur", "login", userReq.getLogin());
                }

                Utilisateur utilisateur = new Utilisateur();
                utilisateur.setLogin(userReq.getLogin());
                utilisateur.setPwd(passwordEncoder.encode(userReq.getPwd())); // Hash du mot de passe
                utilisateur.setNom(userReq.getNom());
                utilisateur.setPrenom(userReq.getPrenom());
                utilisateur.setNumTel(userReq.getNumTel());
                utilisateur.setSignature(userReq.getSignature());
                utilisateur.setRole(Role.valueOf(userReq.getRole()));
                utilisateur.setActif(true);
                utilisateur.setCabinet(savedCabinet);

                utilisateurRepository.save(utilisateur);
            }
        }

        log.info("Cabinet créé avec succès: ID={}", savedCabinet.getId());
        return convertToDTO(savedCabinet);
    }

    @Override
    @Transactional
    public CabinetDTO modifierCabinet(Long id, CabinetDTO cabinetDTO) {
        log.info("Modification du cabinet ID={}", id);

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cabinet", "id", id));

        // Vérifier si le nouveau nom existe déjà (si changé)
        if (!cabinet.getNom().equals(cabinetDTO.getNom()) &&
                cabinetRepository.existsByNom(cabinetDTO.getNom())) {
            throw new DuplicateResourceException("Cabinet", "nom", cabinetDTO.getNom());
        }

        cabinet.setLogo(cabinetDTO.getLogo());
        cabinet.setNom(cabinetDTO.getNom());
        cabinet.setSpecialite(cabinetDTO.getSpecialite());
        cabinet.setAdresse(cabinetDTO.getAdresse());
        cabinet.setTel(cabinetDTO.getTel());
        cabinet.setEmail(cabinetDTO.getEmail());
        // appeler la méthode métier avec les paramètres correspondants
        cabinet.modifierCabinet(cabinetDTO.getNom(), cabinetDTO.getSpecialite(), cabinetDTO.getAdresse(), cabinetDTO.getTel(), cabinetDTO.getEmail(), cabinetDTO.getLogo());

        Cabinet updatedCabinet = cabinetRepository.save(cabinet);
        log.info("Cabinet modifié avec succès: ID={}", id);

        return convertToDTO(updatedCabinet);
    }

    @Override
    @Transactional
    public CabinetDTO activerCabinet(Long id) {
        log.info("Activation du cabinet ID={}", id);

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cabinet", "id", id));

        cabinet.activerCabinet(); // Méthode du diagramme de classes
        Cabinet activatedCabinet = cabinetRepository.save(cabinet);

        log.info("Cabinet activé avec succès: ID={}", id);
        return convertToDTO(activatedCabinet);
    }

    @Override
    @Transactional
    public CabinetDTO desactiverCabinet(Long id) {
        log.info("Désactivation du cabinet ID={}", id);

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cabinet", "id", id));

        cabinet.desactiverCabinet(); // Méthode du diagramme de classes
        Cabinet deactivatedCabinet = cabinetRepository.save(cabinet);

        log.info("Cabinet désactivé avec succès: ID={}", id);
        return convertToDTO(deactivatedCabinet);
    }

    @Override
    @Transactional(readOnly = true)
    public CabinetDTO obtenirCabinetParId(Long id) {
        log.info("Récupération du cabinet ID={}", id);

        Cabinet cabinet = cabinetRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cabinet", "id", id));

        return convertToDTO(cabinet);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CabinetDTO> obtenirTousLesCabinets() {
        log.info("Récupération de tous les cabinets");

        return cabinetRepository.findAll().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CabinetDTO> obtenirCabinetsActifs() {
        log.info("Récupération des cabinets actifs");

        return cabinetRepository.findByActifTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CabinetDTO> obtenirCabinetsParSpecialite(String specialite) {
        log.info("Récupération des cabinets par spécialité: {}", specialite);

        return cabinetRepository.findBySpecialite(specialite).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void supprimerCabinet(Long id) {
        log.info("Suppression du cabinet ID={}", id);

        if (!cabinetRepository.existsById(id)) {
            throw new ResourceNotFoundException("Cabinet", "id", id);
        }

        cabinetRepository.deleteById(id);
        log.info("Cabinet supprimé avec succès: ID={}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean cabinetExiste(String nom) {
        return cabinetRepository.existsByNom(nom);
    }

    /**
     * Convertit une entité Cabinet en DTO
     */
    private CabinetDTO convertToDTO(Cabinet cabinet) {
        CabinetDTO dto = new CabinetDTO();
        dto.setId(cabinet.getId());
        dto.setLogo(cabinet.getLogo());
        dto.setNom(cabinet.getNom());
        dto.setSpecialite(cabinet.getSpecialite());
        dto.setAdresse(cabinet.getAdresse());
        dto.setTel(cabinet.getTel());
        dto.setEmail(cabinet.getEmail());
        dto.setDateCreation(cabinet.getDateCreation());
        dto.setActif(cabinet.getActif());

        // Statistiques
        dto.setNombreUtilisateurs(utilisateurRepository.countByCabinetId(cabinet.getId()));
        dto.setNombreMedecins(utilisateurRepository.countMedecinsByCabinetId(cabinet.getId()));

        return dto;
    }
}

