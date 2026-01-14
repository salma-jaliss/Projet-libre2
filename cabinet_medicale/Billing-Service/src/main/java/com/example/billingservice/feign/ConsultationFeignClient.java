package com.example.billingservice.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "consultation-service")
public interface ConsultationFeignClient {
    @GetMapping("/api/consultations/{id}")
    Object getConsultationById(@PathVariable Long id);
}