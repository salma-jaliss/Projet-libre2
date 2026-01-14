package ma.cabinet.patient.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.Period;

@Entity
@Table(name = "patients")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Patient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cin;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    private LocalDate dateNaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "sexe")
    private Sexe sexe;

    @Column(length = 255)
    private String adresse;

    @Column(length = 255)
    private String email;

    @Column(length = 100)
    private String profession;

    private String numTel;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_mutuelle")
    private TypeMutuelle typeMutuelle;

    @Column(name = "groupe_sanguin", length = 5)
    private String groupeSanguin;

    @Column(name = "id_cabinet")
    private Long cabinetId;

    @OneToOne(mappedBy = "patient", cascade = CascadeType.ALL)
    private DossierMedical dossierMedical;

    @Transient
    public Integer getAge() {
        if (dateNaissance == null) return null;
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
}
