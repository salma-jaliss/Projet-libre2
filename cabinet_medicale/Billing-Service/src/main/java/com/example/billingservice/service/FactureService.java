package com.example.billingservice.service;

import com.example.billingservice.dto.FactureDto;
import com.example.billingservice.entity.Facture;
import com.example.billingservice.repository.FactureRepository;
import com.example.billingservice.util.PdfInvoiceGenerator;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class FactureService {

    @Autowired
    private FactureRepository factureRepository;

    @Autowired
    private PdfInvoiceGenerator pdfInvoiceGenerator;

    public List<FactureDto> getAllFactures() {
        return factureRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public FactureDto getFactureById(Long id) {
        Facture facture = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée avec l'id : " + id));
        return convertToDto(facture);
    }

    public FactureDto createFacture(FactureDto dto) {
        Facture facture = new Facture();
        BeanUtils.copyProperties(dto, facture);

        // Generate Invoice Number if not present
        if (facture.getNumeroFacture() == null) {
            facture.setNumeroFacture("FACT-" + System.currentTimeMillis());
        }

        facture.setDateFacture(LocalDate.now());
        facture.setStatut("IMPAYEE");
        
        // Calcul automatique si montants fournis
        if (dto.getLignesMontant() != null && !dto.getLignesMontant().isEmpty()) {
            double total = dto.getLignesMontant().stream().mapToDouble(Double::doubleValue).sum();
            facture.setMontantTotal(total);
            facture.setMontantRestant(total);
            facture.setMontantPaye(0.0);
        }

        Facture saved = factureRepository.save(facture);
        
        // Generate PDF
        try {
            String pdfPath = pdfInvoiceGenerator.generateInvoicePdf(saved);
            saved.setPdfPath(pdfPath);
            saved = factureRepository.save(saved);
        } catch (Exception e) {
            e.printStackTrace(); // Log error but don't fail transaction
        }

        return convertToDto(saved);
    }

    public FactureDto updateFacture(Long id, FactureDto dto) {
        Facture existing = factureRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Facture non trouvée"));

        // Update payment info
        if (dto.getMontantPaye() != null) {
            existing.setMontantPaye(existing.getMontantPaye() + dto.getMontantPaye());
            existing.setMontantRestant(existing.getMontantTotal() - existing.getMontantPaye());
            
            if (existing.getMontantRestant() <= 0) {
                existing.setStatut("PAYEE");
                existing.setMontantRestant(0.0);
                existing.setDatePaiement(LocalDateTime.now());
            } else {
                existing.setStatut("PARTIELLE");
            }
        }
        
        if (dto.getModePaiement() != null) {
            existing.setModePaiement(dto.getModePaiement());
        }

        Facture updated = factureRepository.save(existing);
        return convertToDto(updated);
    }

    public void deleteFacture(Long id) {
        factureRepository.deleteById(id);
    }

    public List<FactureDto> getFacturesByPatient(Long patientId) {
        return factureRepository.findByPatientId(patientId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    private FactureDto convertToDto(Facture entity) {
        FactureDto dto = new FactureDto();
        BeanUtils.copyProperties(entity, dto);
        return dto;
    }
}
