package com.medical.appointment.mapper;

import com.medical.appointment.dto.CreateRendezVousRequest;
import com.medical.appointment.dto.RendezVousDTO;
import com.medical.appointment.entity.RendezVous;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RendezVousMapper {

    RendezVousDTO toDTO(RendezVous rendezVous);

    @Mapping(target = "idRendezVous", ignore = true)
    @Mapping(target = "statut", ignore = true) // Géré dans le service
    RendezVous toEntity(CreateRendezVousRequest request);

    @Mapping(target = "idRendezVous", ignore = true)
    @Mapping(target = "statut", ignore = true) // Ne pas modifier le statut lors de la mise à jour
    void updateEntityFromRequest(CreateRendezVousRequest request, @MappingTarget RendezVous entity);
}
