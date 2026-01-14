package ma.cabinet.statistics.service;

import ma.cabinet.statistics.client.AppointmentServiceClient;
import ma.cabinet.statistics.client.BillingServiceClient;
import ma.cabinet.statistics.client.ConsultationServiceClient;
import ma.cabinet.statistics.client.PatientServiceClient;
import ma.cabinet.statistics.model.Billing;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticsServiceTest {

    @Mock
    private PatientServiceClient patientServiceClient;

    @Mock
    private BillingServiceClient billingServiceClient;

    @Mock
    private AppointmentServiceClient appointmentServiceClient;

    @Mock
    private ConsultationServiceClient consultationServiceClient;

    @InjectMocks
    private StatisticsService statisticsService;

    @Test
    void getGlobalStats_ShouldReturnStats() {
        // Patient Mock
        when(patientServiceClient.getAllPatients()).thenReturn(Collections.emptyList());

        // Billing Mock
        Billing b1 = Billing.builder().montant(100.0).build();
        Billing b2 = Billing.builder().montant(200.0).build();
        when(billingServiceClient.getAllBillings()).thenReturn(Arrays.asList(b1, b2));

        // Other mocks to avoid NPE if called (though code checks for null, @Mock creates a non-null mock)
        // If the service checks (client != null), the mock is not null.
        // But if we don't stub the methods, they return empty lists/nulls by default.
        // The service calls getAllConsultations() etc. so we should stub them or rely on empty list default for List return types?
        // Mockito default answer for List is empty list, so it should be fine.
        
        // Wait, for appointmentServiceClient.getAllAppointments(), it returns List. Default mock returns empty list.
        // Same for consultationServiceClient.

        Map<String, Object> stats = statisticsService.getGlobalStats();

        assertNotNull(stats);
        assertEquals(0, stats.get("totalPatients"));
        assertEquals(300.0, stats.get("totalRevenue"));
    }
}
