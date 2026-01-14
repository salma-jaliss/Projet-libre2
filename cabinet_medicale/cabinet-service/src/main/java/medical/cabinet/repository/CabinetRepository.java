package medical.cabinet.repository;

import medical.cabinet.entity.Cabinet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Cabinet
 * Fournit les opérations CRUD et des requêtes personnalisées
 */
@Repository
public interface CabinetRepository extends JpaRepository<Cabinet, Long> {

    /**
     * Recherche un cabinet par son nom
     */
    Optional<Cabinet> findByNom(String nom);

    /**
     * Recherche des cabinets par spécialité
     */
    List<Cabinet> findBySpecialite(String specialite);

    /**
     * Recherche des cabinets actifs
     */
    List<Cabinet> findByActifTrue();

    /**
     * Recherche des cabinets inactifs
     */
    List<Cabinet> findByActifFalse();

    /**
     * Vérifie si un cabinet existe par son nom
     */
    boolean existsByNom(String nom);

    /**
     * Compte le nombre total de cabinets actifs
     */
    @Query("SELECT COUNT(c) FROM Cabinet c WHERE c.actif = true")
    long countActiveCabinets();
}