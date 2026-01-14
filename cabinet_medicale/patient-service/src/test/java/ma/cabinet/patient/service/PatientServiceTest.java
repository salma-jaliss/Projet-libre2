package ma.cabinet.patient.service;

import ma.cabinet.patient.entity.Patient;
import ma.cabinet.patient.repository.PatientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientService patientService;

    @Test
    void getAllPatients_ShouldReturnList() {
        Patient p1 = new Patient();
        p1.setNom("Doe");
        Patient p2 = new Patient();
        p2.setNom("Smith");
        
        when(patientRepository.findAll()).thenReturn(Arrays.asList(p1, p2));

        List<Patient> result = patientService.getAllPatients();

        assertEquals(2, result.size());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void createPatient_ShouldReturnSavedPatient() {
        Patient p = new Patient();
        p.setNom("Doe");
        
        when(patientRepository.save(any(Patient.class))).thenReturn(p);

        Patient result = patientService.createPatient(p);

        assertNotNull(result);
        assertEquals("Doe", result.getNom());
    }
}
