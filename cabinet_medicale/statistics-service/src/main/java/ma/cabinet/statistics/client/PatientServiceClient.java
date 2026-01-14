package ma.cabinet.statistics.client;

import ma.cabinet.statistics.model.Patient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "patient-service")
public interface PatientServiceClient {
    @GetMapping("/api/patients")
    List<Patient> getAllPatients();
}
