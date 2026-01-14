package com.medical.chatbot.client;

import com.medical.chatbot.dto.CreateRendezVousRequest;
import com.medical.chatbot.dto.RendezVousDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@FeignClient(name = "appointment-service")
public interface AppointmentClient {

        @PostMapping("/api/rendez-vous")
        ResponseEntity<RendezVousDTO> prendreRendezVous(@RequestBody CreateRendezVousRequest request);

        @GetMapping("/api/rendez-vous/du-jour")
        ResponseEntity<List<RendezVousDTO>> obtenirRendezVousDujour(
                        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        @RequestParam("cabinetId") Long cabinetId);

        @GetMapping("/api/rendez-vous/disponibilite")
        ResponseEntity<Boolean> verifierDisponibilite(
                        @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                        @RequestParam("heure") @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime heure,
                        @RequestParam("cabinetId") Long cabinetId);

        @PatchMapping("/api/rendez-vous/{id}/annuler")
        ResponseEntity<Void> annulerRendezVous(@PathVariable("id") Long id);

        @GetMapping("/api/rendez-vous/patient/{patientId}")
        ResponseEntity<List<RendezVousDTO>> obtenirRendezVousParPatient(@PathVariable("patientId") Long patientId);
}
