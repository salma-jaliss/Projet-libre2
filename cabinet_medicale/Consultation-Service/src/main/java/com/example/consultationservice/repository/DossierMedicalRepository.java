package com.example.consultationservice.repository;

import com.example.consultationservice.entity.DossierMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface DossierMedicalRepository extends JpaRepository<DossierMedical, Long> {
    Optional<DossierMedical> findByPatientId(Long patientId);
}
