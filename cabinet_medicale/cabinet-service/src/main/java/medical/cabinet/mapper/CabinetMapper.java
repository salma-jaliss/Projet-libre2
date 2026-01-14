package medical.cabinet.mapper;

import medical.cabinet.dto.CabinetDTO;
import medical.cabinet.dto.CreateCabinetRequest;
import medical.cabinet.entity.Cabinet;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

/**
 * Mapper pour l'entité Cabinet
 * Utilise MapStruct pour la conversion Entity <-> DTO
 */
@Mapper(componentModel = "spring")
public interface CabinetMapper {

    /**
     * Convertit une entité Cabinet en DTO
     * Les champs nombreUtilisateurs et nombreMedecins sont calculés dans le service
     */
    @Mapping(target = "nombreUtilisateurs", ignore = true)
    @Mapping(target = "nombreMedecins", ignore = true)
    CabinetDTO toDTO(Cabinet cabinet);

    /**
     * Convertit un CreateCabinetRequest en entité Cabinet
     * Les champs id, dateCreation, utilisateurs et actif sont gérés dans le service
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "utilisateurs", ignore = true)
    @Mapping(target = "actif", ignore = true)
    Cabinet toEntity(CreateCabinetRequest request);

    /**
     * Convertit une liste d'entités en liste de DTOs
     */
    List<CabinetDTO> toDTOList(List<Cabinet> cabinets);

    /**
     * Met à jour une entité Cabinet à partir d'un CreateCabinetRequest
     * Ignore l'ID, la date de création et les utilisateurs
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "dateCreation", ignore = true)
    @Mapping(target = "utilisateurs", ignore = true)
    void updateCabinetFromRequest(CreateCabinetRequest request, @MappingTarget Cabinet cabinet);
}