package com.example.medicamentservice.controller;

import com.example.medicamentservice.dto.MedicamentDto;
import com.example.medicamentservice.service.MedicamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/medicaments")
@CrossOrigin("*")
public class MedicamentController {

    @Autowired
    private MedicamentService medicamentService;

    @GetMapping
    public List<MedicamentDto> getAll() {
        return medicamentService.getAll();
    }

    @GetMapping("/{id}")
    public MedicamentDto getById(@PathVariable Long id) {
        return medicamentService.getById(id);
    }

    @PostMapping
    public MedicamentDto create(@RequestBody MedicamentDto dto) {
        return medicamentService.create(dto);
    }

    @PutMapping("/{id}")
    public MedicamentDto update(@PathVariable Long id, @RequestBody MedicamentDto dto) {
        return medicamentService.update(id, dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        medicamentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<MedicamentDto> search(@RequestParam String q) {
        return medicamentService.search(q);
    }

    @PostMapping("/import-csv")
    public ResponseEntity<String> importCsv(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok(medicamentService.importCsv(file));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur import : " + e.getMessage());
        }
    }
}