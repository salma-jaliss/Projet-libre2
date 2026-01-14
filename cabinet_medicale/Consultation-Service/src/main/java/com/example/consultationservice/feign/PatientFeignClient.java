package com.example.consultationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "patient-service", url = "${patient.service.url:http://localhost:8082}") // Adapte le port de patient-service
public interface PatientFeignClient {

    @GetMapping("/api/patients/{id}")
    Object getPatientById(@PathVariable Long id); // Retourne un DTO patient, adapte à ton PatientDto
}