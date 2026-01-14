package com.example.consultationservice.controller;

import com.example.consultationservice.dto.ConsultationDto;
import com.example.consultationservice.dto.OrdonnanceDto;
import com.example.consultationservice.service.ConsultationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/consultations")
@CrossOrigin(origins = "*") // À sécuriser en prod
public class ConsultationController {

    @Autowired
    private ConsultationService consultationService;

    // ==================== CONSULTATIONS ====================

    @GetMapping
    public ResponseEntity<List<ConsultationDto>> getAllConsultations() {
        return ResponseEntity.ok(consultationService.getAllConsultations());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConsultationDto> getConsultation(@PathVariable Long id) {
        return ResponseEntity.ok(consultationService.getConsultationById(id));
    }

    @PostMapping
    public ResponseEntity<ConsultationDto> createConsultation(@RequestBody ConsultationDto dto) {
        return new ResponseEntity<>(consultationService.createConsultation(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConsultationDto> updateConsultation(@PathVariable Long id, @RequestBody ConsultationDto dto) {
        return ResponseEntity.ok(consultationService.updateConsultation(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConsultation(@PathVariable Long id) {
        consultationService.deleteConsultation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<ConsultationDto>> getConsultationsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(consultationService.getConsultationsByPatientId(patientId));
    }

    // ==================== ORDONNANCES ====================

    @PostMapping("/{consultationId}/ordonnance")
    public ResponseEntity<OrdonnanceDto> createOrdonnance(
            @PathVariable Long consultationId,
            @RequestBody OrdonnanceDto ordonnanceDto) {

        OrdonnanceDto created = consultationService.createOrdonnanceForConsultation(consultationId, ordonnanceDto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping("/{consultationId}/ordonnances")
    public ResponseEntity<List<OrdonnanceDto>> getOrdonnancesByConsultation(@PathVariable Long consultationId) {
        ConsultationDto consultation = consultationService.getConsultationById(consultationId);
        return ResponseEntity.ok(consultation.getOrdonnances());
    }
}