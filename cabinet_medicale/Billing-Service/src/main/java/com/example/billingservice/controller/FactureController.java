package com.example.billingservice.controller;

import com.example.billingservice.dto.FactureDto;
import com.example.billingservice.service.FactureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/factures")
@CrossOrigin(origins = "*")
public class FactureController {

    @Autowired
    private FactureService factureService;

    @GetMapping
    public ResponseEntity<List<FactureDto>> getAllFactures() {
        return ResponseEntity.ok(factureService.getAllFactures());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureDto> getFacture(@PathVariable Long id) {
        return ResponseEntity.ok(factureService.getFactureById(id));
    }

    @PostMapping
    public ResponseEntity<FactureDto> createFacture(@RequestBody FactureDto dto) {
        return new ResponseEntity<>(factureService.createFacture(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FactureDto> updateFacture(@PathVariable Long id, @RequestBody FactureDto dto) {
        return ResponseEntity.ok(factureService.updateFacture(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFacture(@PathVariable Long id) {
        factureService.deleteFacture(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<FactureDto>> getFacturesByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(factureService.getFacturesByPatient(patientId));
    }
}
