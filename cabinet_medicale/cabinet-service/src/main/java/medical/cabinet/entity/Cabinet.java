package medical.cabinet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Entité Cabinet - Représente un cabinet médical
 * Conforme au diagramme de classes UML
 *
     * Attributs du diagramme:
     * - id: Long
     * - logo: String
     * - nom: String
     * - specialite: String
     * - adresse: String
     * - tel: String
     * - email: String (conforme à l'ERD)
     * - dateCreation: Date
     * - actif: boolean
 *
 * Méthodes du diagramme:
 * - creerCabinet()
 * - modifierCabinet()
 * - activerCabinet()
 * - desactiverCabinet()
 */
@Entity
@Table(name = "cabinet")
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "utilisateurs") // Évite les boucles infinies lors du toString
public class Cabinet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_cabinet")
    private Long id;

    @Column(name = "logo", length = 1000)
    @Lob
    private String logo;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "specialite", nullable = false, length = 100)
    private String specialite;

    @Column(name = "adresse", nullable = false, length = 255)
    private String adresse;

    @Column(name = "tel", nullable = false, length = 20)
    private String tel;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "date_creation", nullable = false)
    private LocalDate dateCreation;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    /**
     * Relation OneToMany avec Utilisateur
     * Un cabinet emploie plusieurs utilisateurs
     * mappedBy = "cabinet" indique que c'est Utilisateur qui possède la clé étrangère
     */
    @OneToMany(mappedBy = "cabinet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Utilisateur> utilisateurs = new ArrayList<>();

    // ========== MÉTHODES MÉTIER DU DIAGRAMME UML ==========

    /**
     * Méthode métier: creerCabinet()
     * Initialise un nouveau cabinet avec les valeurs par défaut
     */
    public void creerCabinet() {
        this.dateCreation = LocalDate.now();
        this.actif = true;
    }

    /**
     * Méthode métier: modifierCabinet()
     * Modifie les informations d'un cabinet
     *
     * @param nom Nouveau nom du cabinet
     * @param specialite Nouvelle spécialité
     * @param adresse Nouvelle adresse
     * @param tel Nouveau téléphone
     * @param email Nouvel email (peut être null)
     * @param logo Nouveau logo (peut être null)
     */
    public void modifierCabinet(String nom, String specialite, String adresse, String tel, String email, String logo) {
        if (nom != null && !nom.trim().isEmpty()) {
            this.nom = nom;
        }
        if (specialite != null && !specialite.trim().isEmpty()) {
            this.specialite = specialite;
        }
        if (adresse != null && !adresse.trim().isEmpty()) {
            this.adresse = adresse;
        }
        if (tel != null && !tel.trim().isEmpty()) {
            this.tel = tel;
        }
        if (email != null && !email.trim().isEmpty()) {
            this.email = email;
        }
        if (logo != null && !logo.trim().isEmpty()) {
            this.logo = logo;
        }
    }

    /**
     * Méthode métier: activerCabinet()
     * Active un cabinet médical
     */
    public void activerCabinet() {
        this.actif = true;
    }

    /**
     * Méthode métier: desactiverCabinet()
     * Désactive un cabinet médical
     */
    public void desactiverCabinet() {
        this.actif = false;
    }

    // ========== CALLBACKS JPA ==========



    // ========== MÉTHODES UTILITAIRES ==========

    /**
     * Ajoute un utilisateur à ce cabinet
     * Gère la relation bidirectionnelle
     *
     * @param utilisateur L'utilisateur à ajouter
     */
    public void ajouterUtilisateur(Utilisateur utilisateur) {
        utilisateurs.add(utilisateur);
        utilisateur.setCabinet(this);
    }

    /**
     * Retire un utilisateur de ce cabinet
     * Gère la relation bidirectionnelle
     *
     * @param utilisateur L'utilisateur à retirer
     */
    public void retirerUtilisateur(Utilisateur utilisateur) {
        utilisateurs.remove(utilisateur);
        utilisateur.setCabinet(null);
    }
}

//