package com.medical.appointment.entity;

import com.medical.appointment.enums.MotifRendezvous;
import com.medical.appointment.enums.StatutRendezVous;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Entité RendezVous selon le diagramme ERD
 * Table: rendez_vous
 */
@Entity
@Table(name = "rendez_vous")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RendezVous {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_rendez_vous")
    private Long idRendezVous;

    @Column(name = "date_rdv", nullable = false)
    private LocalDate dateRdv;

    @Column(name = "heure_rdv", nullable = false)
    private LocalTime heureRdv;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif", nullable = false)
    private MotifRendezvous motif;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false)
    private StatutRendezVous statut;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // Relations avec les autres microservices (stockage des IDs)
    // Selon ERD: #id_patient, #id_utilisateur, #id_cabinet
    @Column(name = "id_patient", nullable = false)
    private Long patientId;

    @Column(name = "id_utilisateur", nullable = false)
    private Long utilisateurId; // ID de l'utilisateur (médecin ou secrétaire)

    @Column(name = "id_cabinet", nullable = false)
    private Long cabinetId;
}
