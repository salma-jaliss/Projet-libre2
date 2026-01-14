package medical.cabinet.repository;

import medical.cabinet.entity.Utilisateur;
import medical.cabinet.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Utilisateur
 * Fournit les opérations CRUD et des requêtes personnalisées
 */
@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Long> {

    /**
     * Recherche un utilisateur par login
     */
    Optional<Utilisateur> findByLogin(String login);

    /**
     * Recherche des utilisateurs par cabinet
     */
    List<Utilisateur> findByCabinetId(Long cabinetId);

    /**
     * Recherche des utilisateurs par rôle
     */
    List<Utilisateur> findByRole(Role role);

    /**
     * Recherche des utilisateurs par cabinet et rôle
     */
    List<Utilisateur> findByCabinetIdAndRole(Long cabinetId, Role role);

    /**
     * Recherche des utilisateurs actifs d'un cabinet
     */
    @Query("SELECT u FROM Utilisateur u WHERE u.cabinet.id = :cabinetId AND u.actif = true")
    List<Utilisateur> findActiveByCabinetId(@Param("cabinetId") Long cabinetId);

    /**
     * Vérifie si un login existe déjà
     */
    boolean existsByLogin(String login);

    /**
     * Compte le nombre d'utilisateurs par cabinet
     */
    long countByCabinetId(Long cabinetId);

    /**
     * Compte le nombre de médecins d'un cabinet
     */
    @Query("SELECT COUNT(u) FROM Utilisateur u WHERE u.cabinet.id = :cabinetId AND u.role = 'MEDECIN'")
    long countMedecinsByCabinetId(@Param("cabinetId") Long cabinetId);
}