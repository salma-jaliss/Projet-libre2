package ma.cabinet.patient.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "dossiers_medicaux")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DossierMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDossier;

    @Column(columnDefinition = "TEXT")
    private String antMedicaux;

    @Column(name = "ant_chirurgicaux", columnDefinition = "TEXT")
    private String antChirurgicaux;

    @Column(columnDefinition = "TEXT")
    private String allergies;

    @Column(columnDefinition = "TEXT")
    private String traitement;

    @Column(columnDefinition = "TEXT")
    private String habitudes;

    @Column(name = "groupe_sanguin", length = 5)
    private String groupeSanguin;

    @Column(columnDefinition = "TEXT")
    private String risque;

    @Column(name = "date_creation")
    private LocalDateTime dateCreation;
    @Column(name = "date_modification")
    private LocalDateTime dateModification;

    @OneToOne
    @JoinColumn(name = "patient_id", unique = true)
    private Patient patient;

    @OneToMany(mappedBy = "dossierMedical", cascade = CascadeType.ALL)
    private List<DocumentMedical> documentsMedicaux;
}
