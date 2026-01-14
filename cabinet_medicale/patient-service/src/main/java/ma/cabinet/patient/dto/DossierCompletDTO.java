package ma.cabinet.patient.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.cabinet.patient.entity.DossierMedical;
import ma.cabinet.patient.entity.Patient;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DossierCompletDTO {
    private Patient patient;
    private DossierMedical dossierMedical;
    private List<Object> historiqueConsultations; // Will be populated from Consultation Service when available
}


