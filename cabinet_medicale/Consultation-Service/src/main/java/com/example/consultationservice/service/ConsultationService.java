package com.example.consultationservice.service;

import com.example.consultationservice.dto.ConsultationDto;
import com.example.consultationservice.dto.LigneMedicamentDto;
import com.example.consultationservice.dto.OrdonnanceDto;
import com.example.consultationservice.entity.Consultation;
import com.example.consultationservice.entity.LigneMedicament;
import com.example.consultationservice.entity.Ordonnance;
import com.example.consultationservice.exception.ResourceNotFoundException;
import com.example.consultationservice.feign.MedicamentFeignClient;
import com.example.consultationservice.feign.PatientFeignClient;
import com.example.consultationservice.repository.ConsultationRepository;
import com.example.consultationservice.repository.LigneMedicamentRepository;
import com.example.consultationservice.repository.OrdonnanceRepository;
import com.example.consultationservice.util.PdfGeneratorUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ConsultationService {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private OrdonnanceRepository ordonnanceRepository;

    @Autowired
    private LigneMedicamentRepository ligneMedicamentRepository;

    @Autowired
    private PatientFeignClient patientFeignClient;

    @Autowired
    private MedicamentFeignClient medicamentFeignClient;

    @Autowired
    private PdfGeneratorUtil pdfGeneratorUtil;

    @Autowired
    private DossierMedicalService dossierMedicalService;

    // ==================== CONSULTATION ====================

    public List<ConsultationDto> getAllConsultations() {
        return consultationRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public ConsultationDto getConsultationById(Long id) {
        Consultation consultation = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'id : " + id));
        return convertToDto(consultation);
    }

    public ConsultationDto createConsultation(ConsultationDto dto) {
        // Verification Patient via Feign (décommenté si le service Patient tourne)
        try {
            patientFeignClient.getPatientById(dto.getPatientId());
        } catch (Exception e) {
            // Log warning or ignore if service not available during dev
            System.err.println("Patient Service unreachable: " + e.getMessage());
        }

        Consultation consultation = convertToEntity(dto);
        consultation.setDateConsultation(LocalDateTime.now());

        // Lier au dossier médical
        if (dto.getPatientId() != null) {
            com.example.consultationservice.entity.DossierMedical dossier = dossierMedicalService.getDossierByPatientId(dto.getPatientId());
            consultation.setDossierMedical(dossier);
        }

        Consultation saved = consultationRepository.save(consultation);
        return convertToDto(saved);
    }
    public ConsultationDto updateConsultation(Long id, ConsultationDto dto) {
        Consultation existing = consultationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée avec l'id : " + id));

        existing.setType(dto.getType());
        existing.setExamenClinique(dto.getExamenClinique());
        existing.setExamenSupplementaire(dto.getExamenSupplementaire());
        existing.setDiagnostic(dto.getDiagnostic());
        existing.setTraitement(dto.getTraitement());
        existing.setObservations(dto.getObservations());

        Consultation updated = consultationRepository.save(existing);
        return convertToDto(updated);
    }

    public void deleteConsultation(Long id) {
        if (!consultationRepository.existsById(id)) {
            throw new ResourceNotFoundException("Consultation non trouvée avec l'id : " + id);
        }
        consultationRepository.deleteById(id);
    }

    public List<ConsultationDto> getConsultationsByPatientId(Long patientId) {
        return consultationRepository.findByPatientId(patientId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // ==================== ORDONNANCE ====================

    public OrdonnanceDto createOrdonnanceForConsultation(Long consultationId, OrdonnanceDto ordonnanceDto) {
        Consultation consultation = consultationRepository.findById(consultationId)
                .orElseThrow(() -> new ResourceNotFoundException("Consultation non trouvée"));

        Ordonnance ordonnance = new Ordonnance();
        ordonnance.setType(ordonnanceDto.getType());
        ordonnance.setDateOrdonnance(LocalDateTime.now());
        ordonnance.setConsultation(consultation);

        List<LigneMedicament> lignes = ordonnanceDto.getLignesMedicaments().stream().map(ligneDto -> {
            // Ligne commentée temporairement : le Medicament Service n'est pas encore lancé
            // medicamentFeignClient.getMedicamentById(ligneDto.getMedicamentId());

            LigneMedicament ligne = new LigneMedicament();
            ligne.setMedicamentId(ligneDto.getMedicamentId());
            ligne.setPosologie(ligneDto.getPosologie());
            ligne.setQuantite(ligneDto.getQuantite());
            ligne.setInstructions(ligneDto.getInstructions());
            ligne.setOrdonnance(ordonnance);

            return ligne;
        }).collect(Collectors.toList());

        ordonnance.setLignesMedicaments(lignes);

        Ordonnance saved = ordonnanceRepository.save(ordonnance);

        String pdfPath = pdfGeneratorUtil.generateOrdonnancePdf(saved);
        saved.setSignaturePath(pdfPath);
        ordonnanceRepository.save(saved);

        return convertOrdonnanceToDto(saved);
    }
    // ==================== MAPPERS ====================

    private ConsultationDto convertToDto(Consultation entity) {
        ConsultationDto dto = new ConsultationDto();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setDateConsultation(entity.getDateConsultation());
        dto.setExamenClinique(entity.getExamenClinique());
        dto.setExamenSupplementaire(entity.getExamenSupplementaire());
        dto.setDiagnostic(entity.getDiagnostic());
        dto.setTraitement(entity.getTraitement());
        dto.setObservations(entity.getObservations());
        dto.setPatientId(entity.getPatientId());

        if (entity.getOrdonnances() != null) {
            dto.setOrdonnances(entity.getOrdonnances().stream()
                    .map(this::convertOrdonnanceToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private Consultation convertToEntity(ConsultationDto dto) {
        Consultation entity = new Consultation();
        entity.setType(dto.getType());
        entity.setExamenClinique(dto.getExamenClinique());
        entity.setExamenSupplementaire(dto.getExamenSupplementaire());
        entity.setDiagnostic(dto.getDiagnostic());
        entity.setTraitement(dto.getTraitement());
        entity.setObservations(dto.getObservations());
        entity.setPatientId(dto.getPatientId());
        return entity;
    }

    private OrdonnanceDto convertOrdonnanceToDto(Ordonnance entity) {
        OrdonnanceDto dto = new OrdonnanceDto();
        dto.setId(entity.getId());
        dto.setType(entity.getType());
        dto.setDateOrdonnance(entity.getDateOrdonnance());
        dto.setConsultationId(entity.getConsultation().getId());
        dto.setSignaturePath(entity.getSignaturePath());

        if (entity.getLignesMedicaments() != null) {
            dto.setLignesMedicaments(entity.getLignesMedicaments().stream()
                    .map(this::convertLigneToDto)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    private LigneMedicamentDto convertLigneToDto(LigneMedicament entity) {
        LigneMedicamentDto dto = new LigneMedicamentDto();
        dto.setId(entity.getId());
        dto.setMedicamentId(entity.getMedicamentId());
        dto.setPosologie(entity.getPosologie());
        dto.setQuantite(entity.getQuantite());
        dto.setInstructions(entity.getInstructions());
        return dto;
    }
}