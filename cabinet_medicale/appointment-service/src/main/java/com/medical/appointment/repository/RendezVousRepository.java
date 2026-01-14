package com.medical.appointment.repository;

import com.medical.appointment.entity.RendezVous;
import com.medical.appointment.enums.StatutRendezVous;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RendezVousRepository extends JpaRepository<RendezVous, Long> {
    
    List<RendezVous> findByPatientId(Long patientId);
    
    List<RendezVous> findByUtilisateurId(Long utilisateurId);
    
    List<RendezVous> findByDateRdv(LocalDate dateRdv);
    
    List<RendezVous> findByDateRdvAndUtilisateurId(LocalDate dateRdv, Long utilisateurId);
    
    List<RendezVous> findByStatut(StatutRendezVous statut);
    
    List<RendezVous> findByCabinetId(Long cabinetId);
    
    // Méthode pour obtenir les rendez-vous du jour selon le diagramme de classe UML
    @Query("SELECT r FROM RendezVous r WHERE r.dateRdv = :date AND r.cabinetId = :cabinetId ORDER BY r.heureRdv ASC")
    List<RendezVous> obtenirRendezVousDujour(@Param("date") LocalDate date, @Param("cabinetId") Long cabinetId);
    
    // Vérifier disponibilité (éviter les doublons)
    boolean existsByDateRdvAndHeureRdvAndCabinetId(LocalDate dateRdv, java.time.LocalTime heureRdv, Long cabinetId);
}
