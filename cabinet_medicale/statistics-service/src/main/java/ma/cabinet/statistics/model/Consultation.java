package ma.cabinet.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Consultation {
    private Long id;
    private Long patientId;
    private LocalDateTime dateConsultation;
    private String motif;
}
