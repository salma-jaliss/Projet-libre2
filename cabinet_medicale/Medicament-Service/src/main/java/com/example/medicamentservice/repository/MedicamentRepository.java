package com.example.medicamentservice.repository;

import com.example.medicamentservice.entity.Medicament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicamentRepository extends JpaRepository<Medicament, Long> {

    // Recherche par nom commercial (insensible à la casse + partielle)
    List<Medicament> findByNomCommercialContainingIgnoreCase(String nom);

    // Recherche par DCI (insensible à la casse + partielle)
    List<Medicament> findByDciContainingIgnoreCase(String dci);

    // Autocomplétion combinée
    List<Medicament> findByNomCommercialContainingIgnoreCaseOrDciContainingIgnoreCase(String nom, String dci);
}