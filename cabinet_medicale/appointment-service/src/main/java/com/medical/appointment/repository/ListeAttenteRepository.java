package com.medical.appointment.repository;

import com.medical.appointment.entity.ListeAttente;
import com.medical.appointment.enums.StatutListeAttente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListeAttenteRepository extends JpaRepository<ListeAttente, Long> {
    
    List<ListeAttente> findByStatutOrderByPositionAsc(StatutListeAttente statut);
    
    List<ListeAttente> findByStatutAndCabinetIdOrderByPositionAsc(StatutListeAttente statut, Long cabinetId);

    Optional<ListeAttente> findByPatientIdAndStatut(Long patientId, StatutListeAttente statut);
    
    List<ListeAttente> findByCabinetId(Long cabinetId);
    
    List<ListeAttente> findByCabinetIdAndDateAttente(Long cabinetId, LocalDate dateAttente);
    
    // Obtenir le prochain patient selon la position
    @Query("SELECT l FROM ListeAttente l WHERE l.cabinetId = :cabinetId AND l.statut = :statut ORDER BY l.position ASC")
    Optional<ListeAttente> findFirstByCabinetIdAndStatutOrderByPositionAsc(@Param("cabinetId") Long cabinetId, @Param("statut") StatutListeAttente statut);
    
    // Obtenir la position maximale pour un cabinet
    @Query("SELECT COALESCE(MAX(l.position), 0) FROM ListeAttente l WHERE l.cabinetId = :cabinetId AND l.dateAttente = :dateAttente")
    Integer findMaxPositionByCabinetIdAndDateAttente(@Param("cabinetId") Long cabinetId, @Param("dateAttente") LocalDate dateAttente);
}
