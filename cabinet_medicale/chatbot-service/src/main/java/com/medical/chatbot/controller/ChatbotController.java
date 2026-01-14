package com.medical.chatbot.controller;

import com.medical.chatbot.dto.CabinetDTO;
import com.medical.chatbot.dto.ChatbotRequest;
import com.medical.chatbot.dto.ChatbotResponse;
import com.medical.chatbot.dto.CreateRendezVousRequest;
import com.medical.chatbot.dto.RendezVousDTO;
import com.medical.chatbot.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/message")
    public ResponseEntity<ChatbotResponse> chat(@RequestBody ChatbotRequest request) {
        return ResponseEntity.ok(chatbotService.processMessage(request));
    }

    @GetMapping("/disponibilites")
    public ResponseEntity<List<LocalTime>> getDisponibilites(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long cabinetId) {
        return ResponseEntity.ok(chatbotService.getAvailableSlots(date, cabinetId));
    }

    @PostMapping("/rendez-vous")
    public ResponseEntity<RendezVousDTO> bookAppointment(@RequestBody CreateRendezVousRequest request) {
        return ResponseEntity.ok(chatbotService.bookAppointment(request));
    }

    @PatchMapping("/rendez-vous/{id}/annuler")
    public ResponseEntity<Void> cancelAppointment(@PathVariable Long id) {
        chatbotService.cancelAppointment(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/cabinet/{id}")
    public ResponseEntity<CabinetDTO> getCabinetInfo(@PathVariable Long id) {
        return ResponseEntity.ok(chatbotService.getCabinetInfo(id));
    }
}
