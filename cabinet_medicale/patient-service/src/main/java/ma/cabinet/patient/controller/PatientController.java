package ma.cabinet.patient.controller;

import ma.cabinet.patient.entity.DocumentMedical;
import ma.cabinet.patient.entity.DossierMedical;
import ma.cabinet.patient.entity.ListeAttente;
import ma.cabinet.patient.entity.Patient;
import ma.cabinet.patient.service.DossierMedicalService;
import ma.cabinet.patient.service.FileStorageService;
import ma.cabinet.patient.service.ListeAttenteService;
import ma.cabinet.patient.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    @Autowired
    private PatientService patientService;
    
    @Autowired
    private DossierMedicalService dossierMedicalService;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    @Autowired
    private ListeAttenteService listeAttenteService;

    @GetMapping
    public List<Patient> getAllPatients() {
        return patientService.getAllPatients();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(@PathVariable Long id) {
        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Patient>> searchPatients(@RequestParam(required = false) String query, 
                                                         @RequestParam(required = false) String cin) {
        if (cin != null && !cin.isEmpty()) {
            return patientService.getPatientByCin(cin)
                    .map(patient -> ResponseEntity.ok(List.of(patient)))
                    .orElse(ResponseEntity.ok(List.of()));
        }
        if (query != null && !query.isEmpty()) {
            return ResponseEntity.ok(patientService.searchPatientsByName(query));
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping
    public Patient createPatient(@RequestBody Patient patient) {
        return patientService.createPatient(patient);
    }

    @PutMapping("/{id}")
    public Patient updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        return patientService.updatePatient(id, patient);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok().build();
    }

    // Dossier Medical Endpoints

    @PostMapping("/{patientId}/dossier")
    public DossierMedical createDossier(@PathVariable Long patientId, @RequestBody DossierMedical dossier) {
        return dossierMedicalService.createDossier(patientId, dossier);
    }

    @GetMapping("/{patientId}/dossier")
    public ResponseEntity<DossierMedical> getDossier(@PathVariable Long patientId) {
        return dossierMedicalService.getDossierByPatientId(patientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/{patientId}/dossier-complet")
    public ResponseEntity<ma.cabinet.patient.dto.DossierCompletDTO> getDossierComplet(@PathVariable Long patientId) {
        return ResponseEntity.ok(dossierMedicalService.getDossierComplet(patientId));
    }
    
    @PutMapping("/dossier/{id}")
    public DossierMedical updateDossier(@PathVariable Long id, @RequestBody DossierMedical dossier) {
        return dossierMedicalService.updateDossier(id, dossier);
    }

    @PostMapping("/dossier/{dossierId}/documents")
    public DocumentMedical uploadDocument(@PathVariable Long dossierId, @RequestParam("file") MultipartFile file) {
        return dossierMedicalService.uploadDocument(dossierId, file);
    }
    
    @GetMapping("/documents/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable String filename) {
        Resource resource = fileStorageService.loadFileAsResource(filename);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
    
    // Liste d'attente endpoints
    @PostMapping("/waitlist")
    public ListeAttente addToWaitList(@RequestParam Long cabinetId, @RequestParam Long patientId, @RequestParam Integer position) {
        return listeAttenteService.addToWaitList(cabinetId, patientId, position);
    }
    
    @GetMapping("/waitlist")
    public List<ListeAttente> getTodayWaitList(@RequestParam Long cabinetId) {
        return listeAttenteService.listForToday(cabinetId);
    }
    
    @PostMapping("/waitlist/{id}/start")
    public ListeAttente startConsultation(@PathVariable Long id) {
        return listeAttenteService.startConsultation(id);
    }
    
    @PostMapping("/waitlist/{id}/finish")
    public ListeAttente finishConsultation(@PathVariable Long id) {
        return listeAttenteService.finishConsultation(id);
    }
}
