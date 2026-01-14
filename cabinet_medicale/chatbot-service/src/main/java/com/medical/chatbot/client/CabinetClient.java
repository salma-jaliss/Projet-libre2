package com.medical.chatbot.client;

import com.medical.chatbot.dto.CabinetDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "cabinet-service")
public interface CabinetClient {

    @GetMapping("/api/cabinets/{id}")
    ResponseEntity<CabinetDTO> obtenirCabinetParId(@PathVariable("id") Long id);
}
