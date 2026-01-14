package com.example.consultationservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "medicament-service", url = "${medicament.service.url:http://localhost:8086}")
public interface MedicamentFeignClient {

    @GetMapping("/api/medicaments/{id}")
    Object getMedicamentById(@PathVariable Long id); // Retourne un DTO medicament
}