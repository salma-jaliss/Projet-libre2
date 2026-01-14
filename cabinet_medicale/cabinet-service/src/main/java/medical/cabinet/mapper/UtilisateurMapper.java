package medical.cabinet.mapper;

import medical.cabinet.dto.CreateUtilisateurRequest;
import medical.cabinet.dto.UtilisateurDTO;
import medical.cabinet.entity.Utilisateur;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

/**
 * Mapper pour l'entité Utilisateur
 * Utilise MapStruct pour la conversion Entity <-> DTO
 */
@Mapper(componentModel = "spring")
public interface UtilisateurMapper {

    /**
     * Convertit une entité Utilisateur en DTO
     * Exclut le mot de passe pour des raisons de sécurité (le DTO n'a pas de champ
     * pwd)
     * Les champs cabinetId et cabinetNom sont mappés via une méthode par défaut
     */
    @Mapping(target = "cabinetId", ignore = true)
    @Mapping(target = "cabinetNom", ignore = true)
    UtilisateurDTO toDTO(Utilisateur utilisateur);

    /**
     * Méthode par défaut pour compléter le mapping avec les informations du cabinet
     */
    default UtilisateurDTO toDTOWithCabinet(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }
        UtilisateurDTO dto = toDTO(utilisateur);
        if (utilisateur.getCabinet() != null) {
            dto.setCabinetId(utilisateur.getCabinet().getId());
            dto.setCabinetNom(utilisateur.getCabinet().getNom());
        }
        return dto;
    }

    /**
     * Convertit un CreateUtilisateurRequest en entité Utilisateur
     * Le cabinet et le mot de passe seront assignés dans le service
     * Note: CreateUtilisateurRequest a un champ cabinetId mais pas cabinet
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "cabinet", ignore = true)
    @Mapping(target = "pwd", ignore = true)
    @Mapping(target = "actif", ignore = true)
    Utilisateur toEntity(CreateUtilisateurRequest request);

    /**
     * Convertit une liste d'entités en liste de DTOs
     * Utilise toDTOWithCabinet pour inclure les informations du cabinet
     */
    default List<UtilisateurDTO> toDTOList(List<Utilisateur> utilisateurs) {
        if (utilisateurs == null) {
            return null;
        }
        return utilisateurs.stream()
                .map(this::toDTOWithCabinet)
                .collect(java.util.stream.Collectors.toList());
    }
}