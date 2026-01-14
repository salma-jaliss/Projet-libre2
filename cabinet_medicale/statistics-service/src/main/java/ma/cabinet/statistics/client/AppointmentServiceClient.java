package ma.cabinet.statistics.client;

import ma.cabinet.statistics.model.Appointment;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(name = "appointment-service", url = "${application.config.appointment-url:}")
public interface AppointmentServiceClient {
    @GetMapping("/api/appointments")
    List<Appointment> getAllAppointments();
}
