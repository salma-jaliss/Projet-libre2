package ma.cabinet.statistics.client;

import ma.cabinet.statistics.model.Consultation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "consultation-service", url = "${application.config.consultation-url:}")
public interface ConsultationServiceClient {
    @GetMapping("/api/consultations")
    List<Consultation> getAllConsultations();
}
