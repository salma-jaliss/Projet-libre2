package com.example.billingservice.repository;

import com.example.billingservice.entity.Facture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface FactureRepository extends JpaRepository<Facture, Long> {

    List<Facture> findByPatientId(Long patientId);

    // Statistiques
    @Query("SELECT SUM(f.montantPaye) FROM Facture f WHERE YEAR(f.datePaiement) = :year")
    Double getTotalRecettesByYear(int year);

    @Query("SELECT SUM(f.montantPaye) FROM Facture f WHERE MONTH(f.datePaiement) = :month AND YEAR(f.datePaiement) = :year")
    Double getTotalRecettesByMonth(int year, int month);
}