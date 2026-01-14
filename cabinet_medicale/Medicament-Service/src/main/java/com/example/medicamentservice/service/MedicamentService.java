package com.example.medicamentservice.service;

import com.example.medicamentservice.dto.MedicamentDto;
import com.example.medicamentservice.entity.Medicament;
import com.example.medicamentservice.repository.MedicamentRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MedicamentService {

    @Autowired
    private MedicamentRepository medicamentRepository;

    public List<MedicamentDto> getAll() {
        return medicamentRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public MedicamentDto getById(Long id) {
        return medicamentRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));
    }

    public MedicamentDto create(MedicamentDto dto) {
        Medicament entity = toEntity(dto);
        return toDto(medicamentRepository.save(entity));
    }

    public MedicamentDto update(Long id, MedicamentDto dto) {
        Medicament existing = medicamentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Médicament non trouvé"));
        existing.setNomCommercial(dto.getNomCommercial());
        existing.setDci(dto.getDci());
        existing.setDosage(dto.getDosage());
        existing.setForme(dto.getForme());
        existing.setClasseTherapeutique(dto.getClasseTherapeutique());
        existing.setPrix(dto.getPrix());
        existing.setRemboursable(dto.getRemboursable());
        return toDto(medicamentRepository.save(existing));
    }

    public void delete(Long id) {
        medicamentRepository.deleteById(id);
    }

    // Recherche autocomplétion
    public List<MedicamentDto> search(String query) {
        return medicamentRepository.findByNomCommercialContainingIgnoreCaseOrDciContainingIgnoreCase(query, query)
                .stream().map(this::toDto).collect(Collectors.toList());
    }

    // Import CSV
    public String importCsv(MultipartFile file) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            reader.readNext(); // Skip header
            String[] line;
            int count = 0;
            while ((line = reader.readNext()) != null) {
                Medicament med = Medicament.builder()
                        .nomCommercial(line[0])
                        .dci(line[1])
                        .dosage(line[2])
                        .forme(line[3])
                        .classeTherapeutique(line[4])
                        .prix(Double.parseDouble(line[5]))
                        .remboursable(Boolean.parseBoolean(line[6]))
                        .build();
                medicamentRepository.save(med);
                count++;
            }
            return count + " médicaments importés avec succès";
        }
    }

    private MedicamentDto toDto(Medicament entity) {
        MedicamentDto dto = new MedicamentDto();
        dto.setId(entity.getId());
        dto.setNomCommercial(entity.getNomCommercial());
        dto.setDci(entity.getDci());
        dto.setDosage(entity.getDosage());
        dto.setForme(entity.getForme());
        dto.setClasseTherapeutique(entity.getClasseTherapeutique());
        dto.setPrix(entity.getPrix());
        dto.setRemboursable(entity.getRemboursable());
        return dto;
    }

    private Medicament toEntity(MedicamentDto dto) {
        return Medicament.builder()
                .nomCommercial(dto.getNomCommercial())
                .dci(dto.getDci())
                .dosage(dto.getDosage())
                .forme(dto.getForme())
                .classeTherapeutique(dto.getClasseTherapeutique())
                .prix(dto.getPrix())
                .remboursable(dto.getRemboursable())
                .build();
    }
}