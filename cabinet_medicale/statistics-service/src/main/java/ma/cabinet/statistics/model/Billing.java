package ma.cabinet.statistics.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Billing {
    private Long id;
    private Long consultationId;
    private Double montant;
    private LocalDate dateFacture;
    private String statutPaiement;
}
