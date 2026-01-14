package ma.cabinet.patient.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "liste_attente")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ListeAttente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_attente")
    private Long id;

    private Integer position;
    @Column(name = "heure_arrivee")
    private LocalTime heureArrivee;
    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private EtatAttente etat;

    @Column(name = "id_patient")
    private Long patientId;

    @Column(name = "id_cabinet")
    private Long cabinetId;
}
