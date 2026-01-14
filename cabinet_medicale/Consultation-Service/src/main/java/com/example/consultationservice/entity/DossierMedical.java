package com.example.consultationservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dossiers_medicaux")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DossierMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private Long patientId;

    private String antMedicaux;

    private String antChirurgicaux;

    private String allergies;

    private String traitementEnCours;

    private String habitudes; // Tabac, alimentation...

    private LocalDateTime dateCreation;

    @ElementCollection
    private List<String> documentsMedicaux = new ArrayList<>();

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Consultation> historiqueConsultations = new ArrayList<>();
}
