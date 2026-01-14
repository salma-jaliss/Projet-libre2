package com.example.consultationservice.controller;

import com.example.consultationservice.entity.DossierMedical;
import com.example.consultationservice.service.DossierMedicalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/dossiers")
@CrossOrigin(origins = "*")
public class DossierMedicalController {

    @Autowired
    private DossierMedicalService dossierMedicalService;

    @PostMapping
    public ResponseEntity<DossierMedical> createDossier(@RequestBody DossierMedical dossier) {
        return ResponseEntity.ok(dossierMedicalService.createDossier(dossier));
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<DossierMedical> getDossierByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(dossierMedicalService.getDossierByPatientId(patientId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DossierMedical> updateDossier(@PathVariable Long id, @RequestBody DossierMedical dossier) {
        return ResponseEntity.ok(dossierMedicalService.updateDossier(id, dossier));
    }
}
