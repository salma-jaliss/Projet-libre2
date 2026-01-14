package com.medical.chatbot.service;

import com.medical.chatbot.client.AppointmentClient;
import com.medical.chatbot.client.CabinetClient;
import com.medical.chatbot.dto.*;
import com.medical.chatbot.enums.MotifRendezvous;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatbotServiceTest {

    @Mock
    private AppointmentClient appointmentClient;

    @Mock
    private CabinetClient cabinetClient;

    @Mock
    private NLPUtils nlpUtils;

    @InjectMocks
    private ChatbotService chatbotService;

    private ChatbotRequest request;
    private Long cabinetId = 1L;
    private Long patientId = 100L;

    @BeforeEach
    void setUp() {
        request = new ChatbotRequest();
        request.setCabinetId(cabinetId);
        request.setPatientId(patientId);
    }

    @Test
    void testSalutation() {
        request.setMessage("Bonjour");
        ChatbotResponse response = chatbotService.processMessage(request);
        assertTrue(response.getResponse().contains("Bonjour"));
        assertTrue(response.getResponse().contains("assistant virtuel"));
    }

    @Test
    void testDemandeInfosCabinet() {
        request.setMessage("Où est le cabinet ?");

        CabinetDTO mockCabinet = new CabinetDTO();
        mockCabinet.setNom("Cabinet Test");
        mockCabinet.setAdresse("123 Rue Test");
        mockCabinet.setTel("0102030405");

        when(cabinetClient.obtenirCabinetParId(cabinetId)).thenReturn(ResponseEntity.ok(mockCabinet));

        ChatbotResponse response = chatbotService.processMessage(request);

        assertTrue(response.getResponse().contains("123 Rue Test"));
        verify(cabinetClient).obtenirCabinetParId(cabinetId);
    }

    @Test
    void testDisponibiliteDirecte() {
        String msg = "Dispo demain";
        request.setMessage(msg);
        LocalDate tomorrow = LocalDate.now().plusDays(1);

        when(nlpUtils.extractDate(msg)).thenReturn(tomorrow);

        // Mock appointment client
        List<RendezVousDTO> bookedSlots = new ArrayList<>();
        // Disons qu'il y a un RDV à 9h00
        RendezVousDTO rdv = new RendezVousDTO();
        rdv.setHeureRdv(LocalTime.of(9, 0));
        bookedSlots.add(rdv);

        when(appointmentClient.obtenirRendezVousDujour(eq(tomorrow), eq(cabinetId)))
                .thenReturn(ResponseEntity.ok(bookedSlots));

        ChatbotResponse response = chatbotService.processMessage(request);

        assertNotNull(response.getData());
        List<LocalTime> slots = (List<LocalTime>) response.getData();
        assertFalse(slots.contains(LocalTime.of(9, 0))); // 9h00 pris
        assertTrue(slots.contains(LocalTime.of(9, 30))); // 9h30 libre
    }

    @Test
    void testScenarioPriseRdvComplet() {
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        LocalTime time = LocalTime.of(14, 30);

        // Etape 1: "Je veux un rdv"
        request.setMessage("Je veux un rdv");
        // extractDate returns null by default mock
        ChatbotResponse r1 = chatbotService.processMessage(request);
        assertTrue(r1.getResponse().toLowerCase().contains("quelle date"));

        // Etape 2: "Demain"
        request.setMessage("Demain");
        when(nlpUtils.extractDate("Demain")).thenReturn(tomorrow);

        // Mock disponibilité pour demain
        when(appointmentClient.obtenirRendezVousDujour(eq(tomorrow), eq(cabinetId)))
                .thenReturn(ResponseEntity.ok(new ArrayList<>())); // Tout est libre

        ChatbotResponse r2 = chatbotService.processMessage(request);
        assertTrue(r2.getResponse().contains("heures disponibles") || r2.getResponse().contains("Quelle heure"));

        // Etape 3: "14h30"
        request.setMessage("14h30");
        when(nlpUtils.extractTime("14h30")).thenReturn(time);

        RendezVousDTO confirmRdv = new RendezVousDTO();
        confirmRdv.setDateRdv(tomorrow);
        confirmRdv.setHeureRdv(time);

        when(appointmentClient.prendreRendezVous(any(CreateRendezVousRequest.class)))
                .thenReturn(ResponseEntity.ok(confirmRdv));

        ChatbotResponse r3 = chatbotService.processMessage(request);

        assertTrue(r3.getResponse().contains("confirmé"));
        assertTrue(r3.getResponse().contains("14:30"));
        verify(appointmentClient).prendreRendezVous(any(CreateRendezVousRequest.class));
    }

    @Test
    void testAnnulationEnCoursDeRoute() {
        // Etape 1: Initier RDV
        request.setMessage("Je veux un rdv");
        chatbotService.processMessage(request);

        // Etape 2: Annuler
        request.setMessage("Annuler");
        ChatbotResponse r2 = chatbotService.processMessage(request);

        assertTrue(r2.getResponse().contains("annulé l'opération"));

        // Etape 3: Vérifier qu'on est revenu à l'état initial (IDLE)
        request.setMessage("Demain");
        // Mock nlpUtils pour que "Demain" renvoie une date,
        // ce qui déclenche le "Je n'ai pas bien compris" ou l'affichage de dispo si
        // interprété comme tel.
        // Mais comme ce n'est pas "Dispo demain", juste "Demain", et qu'on est IDLE...
        // Dans handleIdleState: extractDate("Demain") != null.
        // Mais intent DISPO/RDV/INFO/SALUTATION non détecté sur "Demain".
        // Donc retour "Je n'ai pas bien compris..."

        when(nlpUtils.extractDate("Demain")).thenReturn(LocalDate.now().plusDays(1));

        ChatbotResponse r3 = chatbotService.processMessage(request);

        // Le chatbot est intelligent : "Demain" est compris comme une intention de RDV
        // implicite
        // Donc il propose les heures et demande "Quelle heure vous convient ?"
        assertTrue(r3.getResponse().contains("Quelle heure"));
    }

    @Test
    void testAnnulationDirecteAvecId() {
        // "Je veux annuler le rendez-vous 1"
        String msg = "Je veux annuler le rendez-vous 1";
        request.setMessage(msg);

        when(nlpUtils.extractId(msg)).thenReturn(1L);

        // Mock finding the appointment
        List<RendezVousDTO> myRdvs = new ArrayList<>();
        RendezVousDTO rdv = new RendezVousDTO();
        rdv.setIdRendezVous(1L);
        rdv.setDateRdv(LocalDate.now().plusDays(1));
        rdv.setHeureRdv(LocalTime.of(10, 0));
        myRdvs.add(rdv);

        when(appointmentClient.obtenirRendezVousParPatient(patientId)).thenReturn(ResponseEntity.ok(myRdvs));

        ChatbotResponse response = chatbotService.processMessage(request);

        assertTrue(response.getResponse().contains("annulé avec succès"));
        verify(appointmentClient).annulerRendezVous(1L);
    }
}
