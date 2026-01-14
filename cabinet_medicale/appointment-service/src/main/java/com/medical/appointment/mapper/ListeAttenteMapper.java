package com.medical.appointment.mapper;

import com.medical.appointment.dto.CreateListeAttenteRequest;
import com.medical.appointment.dto.ListeAttenteDTO;
import com.medical.appointment.entity.ListeAttente;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ListeAttenteMapper {

    ListeAttenteDTO toDTO(ListeAttente entity);

    @Mapping(target = "idAttente", ignore = true)
    @Mapping(target = "position", ignore = true) // Sera calculé dans le service
    @Mapping(target = "heureArrivee", expression = "java(request.getHeureArrivee() != null ? request.getHeureArrivee() : java.time.LocalTime.now())")
    @Mapping(target = "dateAttente", expression = "java(request.getDateAttente() != null ? request.getDateAttente() : java.time.LocalDate.now())")
    @Mapping(target = "statut", constant = "EN_ATTENTE")
    ListeAttente toEntity(CreateListeAttenteRequest request);
}
