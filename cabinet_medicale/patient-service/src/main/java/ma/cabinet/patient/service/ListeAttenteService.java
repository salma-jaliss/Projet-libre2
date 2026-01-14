package ma.cabinet.patient.service;

import ma.cabinet.patient.entity.EtatAttente;
import ma.cabinet.patient.entity.ListeAttente;
import ma.cabinet.patient.repository.ListeAttenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class ListeAttenteService {

    @Autowired
    private ListeAttenteRepository repository;

    public ListeAttente addToWaitList(Long cabinetId, Long patientId, Integer position) {
        ListeAttente entry = ListeAttente.builder()
                .cabinetId(cabinetId)
                .patientId(patientId)
                .position(position)
                .date(LocalDate.now())
                .etat(EtatAttente.EN_ATTENTE)
                .build();
        return repository.save(entry);
    }

    public List<ListeAttente> listForToday(Long cabinetId) {
        return repository.findByCabinetIdAndDateOrderByPositionAsc(cabinetId, LocalDate.now());
    }

    public ListeAttente startConsultation(Long entryId) {
        return repository.findById(entryId).map(e -> {
            e.setEtat(EtatAttente.EN_CONSULTATION);
            return repository.save(e);
        }).orElseThrow(() -> new RuntimeException("Waitlist entry not found"));
    }

    public ListeAttente finishConsultation(Long entryId) {
        return repository.findById(entryId).map(e -> {
            e.setEtat(EtatAttente.TERMINE);
            return repository.save(e);
        }).orElseThrow(() -> new RuntimeException("Waitlist entry not found"));
    }
}
