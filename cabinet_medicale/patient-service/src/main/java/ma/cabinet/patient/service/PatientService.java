package ma.cabinet.patient.service;

import ma.cabinet.patient.entity.Patient;
import ma.cabinet.patient.repository.PatientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    public Optional<Patient> getPatientByCin(String cin) {
        return patientRepository.findByCin(cin);
    }
    
    public List<Patient> searchPatientsByName(String query) {
        return patientRepository.findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(query, query);
    }

    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    public Patient updatePatient(Long id, Patient patientDetails) {
        return patientRepository.findById(id).map(patient -> {
            patient.setCin(patientDetails.getCin());
            patient.setNom(patientDetails.getNom());
            patient.setPrenom(patientDetails.getPrenom());
            patient.setDateNaissance(patientDetails.getDateNaissance());
            patient.setSexe(patientDetails.getSexe());
            patient.setNumTel(patientDetails.getNumTel());
            patient.setTypeMutuelle(patientDetails.getTypeMutuelle());
            patient.setEmail(patientDetails.getEmail());
            patient.setAdresse(patientDetails.getAdresse());
            patient.setGroupeSanguin(patientDetails.getGroupeSanguin());
            patient.setProfession(patientDetails.getProfession());
            patient.setCabinetId(patientDetails.getCabinetId());
            return patientRepository.save(patient);
        }).orElseThrow(() -> new RuntimeException("Patient not found"));
    }

    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
}
