package com.medical.appointment.entity;

import com.medical.appointment.enums.StatutListeAttente;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entité ListeAttente selon le diagramme ERD
 * Table: liste_attente
 * Gère la file d'attente des patients dans la salle d'attente
 */
@Entity
@Table(name = "liste_attente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ListeAttente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_attente")
    private Long idAttente;

    @Column(name = "position", nullable = false)
    private Integer position;

    @Column(name = "heure_arrivee", nullable = false)
    private LocalTime heureArrivee;

    @Column(name = "date_attente", nullable = false)
    private LocalDate dateAttente;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutListeAttente statut;

    // Relations avec les autres microservices (stockage des IDs)
    // Selon ERD: #id_patient, #id_cabinet
    @Column(name = "id_patient", nullable = false)
    private Long patientId;

    @Column(name = "id_cabinet", nullable = false)
    private Long cabinetId;
}
