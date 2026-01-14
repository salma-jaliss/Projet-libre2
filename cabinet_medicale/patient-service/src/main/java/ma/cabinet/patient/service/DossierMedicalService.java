package ma.cabinet.patient.service;

import ma.cabinet.patient.entity.DocumentMedical;
import ma.cabinet.patient.entity.DossierMedical;
import ma.cabinet.patient.entity.Patient;
import ma.cabinet.patient.repository.DocumentMedicalRepository;
import ma.cabinet.patient.repository.DossierMedicalRepository;
import ma.cabinet.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DossierMedicalService {

    @Autowired
    private DossierMedicalRepository dossierMedicalRepository;
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Autowired
    private DocumentMedicalRepository documentMedicalRepository;
    
    @Autowired
    private FileStorageService fileStorageService;

    public DossierMedical createDossier(Long patientId, DossierMedical dossier) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        if (patient.getDossierMedical() != null) {
            throw new RuntimeException("Patient already has a dossier");
        }
        
        dossier.setPatient(patient);
        dossier.setDateCreation(LocalDateTime.now());
        return dossierMedicalRepository.save(dossier);
    }

    public Optional<DossierMedical> getDossierByPatientId(Long patientId) {
        return dossierMedicalRepository.findByPatientId(patientId);
    }
    
    public DossierMedical updateDossier(Long id, DossierMedical details) {
        return dossierMedicalRepository.findById(id).map(dossier -> {
            dossier.setAntMedicaux(details.getAntMedicaux());
            dossier.setAntChirurgicaux(details.getAntChirurgicaux());
            dossier.setAllergies(details.getAllergies());
            dossier.setTraitement(details.getTraitement());
            dossier.setHabitudes(details.getHabitudes());
            dossier.setGroupeSanguin(details.getGroupeSanguin());
            dossier.setRisque(details.getRisque());
            dossier.setDateModification(LocalDateTime.now());
            return dossierMedicalRepository.save(dossier);
        }).orElseThrow(() -> new RuntimeException("Dossier not found"));
    }

    public DocumentMedical uploadDocument(Long dossierId, MultipartFile file) {
        DossierMedical dossier = dossierMedicalRepository.findById(dossierId)
                .orElseThrow(() -> new RuntimeException("Dossier not found"));
        
        String fileName = fileStorageService.storeFile(file);
        
        DocumentMedical doc = DocumentMedical.builder()
                .nomDocument(file.getOriginalFilename())
                .typeDocument(file.getContentType())
                .cheminFichier(fileName)
                .tailleFichier(file.getSize())
                .dateUpload(LocalDateTime.now())
                .dossierMedical(dossier)
                .build();

        return documentMedicalRepository.save(doc);
    }
    
    public ma.cabinet.patient.dto.DossierCompletDTO getDossierComplet(Long patientId) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found"));
        
        DossierMedical dossier = dossierMedicalRepository.findByPatientId(patientId)
                .orElse(null);
        
        // Consultation history will be fetched from Consultation Service via Feign when available
        // For now, return empty list
        List<Object> historiqueConsultations = new ArrayList<>();
        
        return ma.cabinet.patient.dto.DossierCompletDTO.builder()
                .patient(patient)
                .dossierMedical(dossier)
                .historiqueConsultations(historiqueConsultations)
                .build();
    }
}
