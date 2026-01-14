package medical.cabinet.entity;
import medical.cabinet.enums.Role;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Entité Utilisateur - Représente un utilisateur du système
 * (Médecin, Secrétaire, Administrateur)
 * Conforme au diagramme de classes UML
 *
 * Attributs du diagramme:
 * - id: Long
 * - login: String
 * - pwd: String
 * - nom: String
 * - prenom: String
 * - numTel: String
 * - signature: String
 * - role: Role (enum)
 *
 * Méthodes du diagramme:
 * - seConnecter()
 * - seDeconnecter()
 * - modifierProfil()
 * - changerMotDePasse()
 */
@Entity
@Table(name = "utilisateur",
        uniqueConstraints = @UniqueConstraint(columnNames = "login"))
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"cabinet", "pwd"}) // Évite d'afficher le mot de passe et les boucles
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_utilisateur")
    private Long id;

    @Column(name = "login", nullable = false, unique = true, length = 50)
    private String login;

    @Column(name = "pwd", nullable = false, length = 255)
    private String pwd;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "num_tel", length = 20)
    private String numTel;

    @Column(name = "signature", length = 1000)
    @Lob
    private String signature;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "actif", nullable = false)
    private Boolean actif = true;

    public Boolean getActif() {
        return this.actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    /**
     * Relation ManyToOne avec Cabinet
     * Plusieurs utilisateurs appartiennent à un cabinet
     * Cette relation est obligatoire (nullable = false)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cabinet_id", nullable = false)
    private Cabinet cabinet;

    // ========== MÉTHODES MÉTIER DU DIAGRAMME UML ==========

    /**
     * Méthode métier: seConnecter()
     * Note: L'authentification réelle est gérée par Auth-Service
     * Cette méthode peut servir pour la validation côté métier
     */
    public void seConnecter() {
        // La logique de connexion sera gérée par Auth-Service
        // Cette méthode peut être utilisée pour des validations métier
        // Par exemple: vérifier si l'utilisateur appartient à un cabinet actif
        if (cabinet != null && !cabinet.getActif()) {
            throw new IllegalStateException("Le cabinet de cet utilisateur est désactivé");
        }
    }

    /**
     * Méthode métier: modifierProfil()
     * Modifie les informations du profil utilisateur
     *
     * @param nom Nouveau nom
     * @param prenom Nouveau prénom
     * @param numTel Nouveau numéro de téléphone
     * @param signature Nouvelle signature (pour les médecins)
     */
    public void modifierProfil(String nom, String prenom, String numTel, String signature) {
        if (nom != null && !nom.trim().isEmpty()) {
            this.nom = nom;
        }
        if (prenom != null && !prenom.trim().isEmpty()) {
            this.prenom = prenom;
        }
        if (numTel != null && !numTel.trim().isEmpty()) {
            this.numTel = numTel;
        }
        // La signature est réservée aux médecins
        if (signature != null && this.role == Role.MEDECIN) {
            this.signature = signature;
        }
    }

    /**
     * Méthode métier: changerMotDePasse()
     * Change le mot de passe de l'utilisateur
     * Note: Le mot de passe doit être hashé avant d'appeler cette méthode
     *
     * @param newPwd Le nouveau mot de passe (déjà hashé)
     */
    public void changerMotDePasse(String newPwd) {
        if (newPwd != null && !newPwd.trim().isEmpty()) {
            this.pwd = newPwd;
        } else {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide");
        }
    }

    /**
     * Méthode métier: seDeconnecter()
     * Note: La déconnexion réelle est gérée par Auth-Service
     * Cette méthode peut servir pour la validation côté métier
     */
    public void seDeconnecter() {
        // La logique de déconnexion sera gérée par Auth-Service
        // Cette méthode peut être utilisée pour des validations métier
    }
}