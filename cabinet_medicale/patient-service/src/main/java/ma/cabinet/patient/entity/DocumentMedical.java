package ma.cabinet.patient.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "documents_medicaux")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DocumentMedical {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_document")
    private Long idDocument;

    @Column(name = "nom_document")
    private String nomDocument;
    @Column(name = "type_document")
    private String typeDocument;
    @Column(name = "chemin_fichier", length = 500)
    private String cheminFichier;
    @Column(name = "taille_fichier")
    private Long tailleFichier;
    @Column(name = "date_upload")
    private LocalDateTime dateUpload;
    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "dossier_id")
    @JsonIgnore
    private DossierMedical dossierMedical;
}
