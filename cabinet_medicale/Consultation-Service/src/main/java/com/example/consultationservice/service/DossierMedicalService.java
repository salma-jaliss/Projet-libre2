package com.example.consultationservice.service;

import com.example.consultationservice.entity.DossierMedical;
import com.example.consultationservice.repository.DossierMedicalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class DossierMedicalService {

    @Autowired
    private DossierMedicalRepository dossierMedicalRepository;

    public DossierMedical createDossier(DossierMedical dossier) {
        if (dossier.getDateCreation() == null) {
            dossier.setDateCreation(LocalDateTime.now());
        }
        return dossierMedicalRepository.save(dossier);
    }

    public DossierMedical getDossierByPatientId(Long patientId) {
        return dossierMedicalRepository.findByPatientId(patientId)
                .orElseGet(() -> {
                    // Création automatique si inexistant (optionnel, selon règles métier)
                    DossierMedical newDossier = new DossierMedical();
                    newDossier.setPatientId(patientId);
                    newDossier.setDateCreation(LocalDateTime.now());
                    return dossierMedicalRepository.save(newDossier);
                });
    }

    public DossierMedical updateDossier(Long id, DossierMedical details) {
        DossierMedical existing = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dossier non trouvé"));
        
        existing.setAntMedicaux(details.getAntMedicaux());
        existing.setAntChirurgicaux(details.getAntChirurgicaux());
        existing.setAllergies(details.getAllergies());
        existing.setTraitementEnCours(details.getTraitementEnCours());
        existing.setHabitudes(details.getHabitudes());
        
        return dossierMedicalRepository.save(existing);
    }

    public void addDocument(Long id, String documentPath) {
        DossierMedical dossier = dossierMedicalRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Dossier non trouvé"));
        dossier.getDocumentsMedicaux().add(documentPath);
        dossierMedicalRepository.save(dossier);
    }
}
