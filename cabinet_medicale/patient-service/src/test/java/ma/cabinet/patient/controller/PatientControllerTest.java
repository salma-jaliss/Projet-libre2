package ma.cabinet.patient.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ma.cabinet.patient.entity.Patient;
import ma.cabinet.patient.service.DossierMedicalService;
import ma.cabinet.patient.service.FileStorageService;
import ma.cabinet.patient.service.PatientService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.flyway.FlywayAutoConfiguration"
})
@AutoConfigureMockMvc
class PatientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PatientService patientService;
    
    @MockBean
    private DossierMedicalService dossierMedicalService;
    
    @MockBean
    private FileStorageService fileStorageService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAllPatients_ShouldReturnList() throws Exception {
        Patient p = new Patient();
        p.setId(1L);
        p.setNom("Doe");
        
        when(patientService.getAllPatients()).thenReturn(Collections.singletonList(p));

        mockMvc.perform(get("/api/patients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Doe"));
    }

    @Test
    void createPatient_ShouldReturnCreated() throws Exception {
        Patient p = new Patient();
        p.setNom("Doe");
        p.setCin("AB123456");
        p.setPrenom("John");

        when(patientService.createPatient(any(Patient.class))).thenReturn(p);

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(p)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Doe"));
    }
}
