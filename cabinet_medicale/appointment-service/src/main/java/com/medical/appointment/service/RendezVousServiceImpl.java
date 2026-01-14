package com.medical.appointment.service;

import com.medical.appointment.client.PatientClient;
import com.medical.appointment.client.UserClient;
import com.medical.appointment.dto.CreateRendezVousRequest;
import com.medical.appointment.dto.RendezVousDTO;
import com.medical.appointment.entity.RendezVous;
import com.medical.appointment.enums.StatutRendezVous;
import com.medical.appointment.exception.DuplicateResourceException;
import com.medical.appointment.exception.ResourceNotFoundException;
import com.medical.appointment.mapper.RendezVousMapper;
import com.medical.appointment.repository.RendezVousRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class RendezVousServiceImpl implements RendezVousService {

    private final RendezVousRepository repository;
    private final RendezVousMapper mapper;
    private final PatientClient patientClient;
    private final UserClient userClient;

    @Override
    public RendezVousDTO prendreRendezVous(CreateRendezVousRequest request) {
        // Vérifier disponibilité
        if (!verifierDisponibilite(request.getDateRdv(), request.getHeureRdv(), request.getCabinetId())) {
            throw new DuplicateResourceException("Le créneau est déjà pris pour cette date et heure");
        }

        // Vérifier existence du patient
        try {
            patientClient.getPatientById(request.getPatientId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Patient non trouvé avec l'id : " + request.getPatientId());
        }

        // Vérifier existence de l'utilisateur
        try {
            userClient.getUserById(request.getUtilisateurId());
        } catch (FeignException.NotFound e) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'id : " + request.getUtilisateurId());
        }

        RendezVous entity = mapper.toEntity(request);
        entity.setStatut(StatutRendezVous.EN_ATTENTE);
        RendezVous saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RendezVousDTO getRendezVousById(Long id) {
        RendezVous entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'id : " + id));
        return mapper.toDTO(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousDTO> getAllRendezVous() {
        return repository.findAll().stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public RendezVousDTO modifierRendezvous(Long id, CreateRendezVousRequest request) {
        RendezVous entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'id : " + id));

        if ((request.getDateRdv() != null && !request.getDateRdv().equals(entity.getDateRdv())) ||
            (request.getHeureRdv() != null && !request.getHeureRdv().equals(entity.getHeureRdv()))) {
            if (!verifierDisponibilite(
                    request.getDateRdv() != null ? request.getDateRdv() : entity.getDateRdv(),
                    request.getHeureRdv() != null ? request.getHeureRdv() : entity.getHeureRdv(),
                    request.getCabinetId() != null ? request.getCabinetId() : entity.getCabinetId())) {
                throw new DuplicateResourceException("Le créneau est déjà pris pour cette date et heure");
            }
        }

        if(request.getDateRdv() != null) entity.setDateRdv(request.getDateRdv());
        if(request.getHeureRdv() != null) entity.setHeureRdv(request.getHeureRdv());
        if(request.getMotif() != null) entity.setMotif(request.getMotif());
        if(request.getNotes() != null) entity.setNotes(request.getNotes());
        if(request.getUtilisateurId() != null) entity.setUtilisateurId(request.getUtilisateurId());
        if(request.getCabinetId() != null) entity.setCabinetId(request.getCabinetId());

        RendezVous updated = repository.save(entity);
        return mapper.toDTO(updated);
    }

    @Override
    public void annulerRendezVous(Long id) {
        RendezVous entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'id : " + id));
        entity.setStatut(StatutRendezVous.ANNULE);
        repository.save(entity);
    }

    @Override
    public RendezVousDTO confirmerRendezvous(Long id) {
        RendezVous entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'id : " + id));
        entity.setStatut(StatutRendezVous.CONFIRME);
        RendezVous saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    public RendezVousDTO updateStatut(Long id, StatutRendezVous statut) {
        RendezVous entity = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rendez-vous non trouvé avec l'id : " + id));
        entity.setStatut(statut);
        RendezVous saved = repository.save(entity);
        return mapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousDTO> getRendezVousByPatient(Long patientId) {
        return repository.findByPatientId(patientId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousDTO> getRendezVousByUtilisateur(Long utilisateurId) {
        return repository.findByUtilisateurId(utilisateurId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousDTO> getRendezVousByDateAndUtilisateur(LocalDate date, Long utilisateurId) {
        return repository.findByDateRdvAndUtilisateurId(date, utilisateurId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RendezVousDTO> obtenirRendezVousDujour(LocalDate date, Long cabinetId) {
        LocalDate dateToUse = date != null ? date : LocalDate.now();
        return repository.obtenirRendezVousDujour(dateToUse, cabinetId).stream()
                .map(mapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean verifierDisponibilite(LocalDate date, java.time.LocalTime heure, Long cabinetId) {
        return !repository.existsByDateRdvAndHeureRdvAndCabinetId(date, heure, cabinetId);
    }
}
