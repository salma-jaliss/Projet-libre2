package ma.cabinet.patient.repository;

import ma.cabinet.patient.entity.DocumentMedical;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentMedicalRepository extends JpaRepository<DocumentMedical, Long> {
}
