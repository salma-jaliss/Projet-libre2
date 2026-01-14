package ma.cabinet.patient.repository;

import ma.cabinet.patient.entity.EtatAttente;
import ma.cabinet.patient.entity.ListeAttente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ListeAttenteRepository extends JpaRepository<ListeAttente, Long> {
    List<ListeAttente> findByCabinetIdAndDateOrderByPositionAsc(Long cabinetId, LocalDate date);
    Optional<ListeAttente> findFirstByCabinetIdAndDateAndEtatOrderByPositionAsc(Long cabinetId, LocalDate date, EtatAttente etat);
}
