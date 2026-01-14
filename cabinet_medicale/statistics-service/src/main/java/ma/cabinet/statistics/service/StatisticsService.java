package ma.cabinet.statistics.service;

import ma.cabinet.statistics.client.AppointmentServiceClient;
import ma.cabinet.statistics.client.BillingServiceClient;
import ma.cabinet.statistics.client.ConsultationServiceClient;
import ma.cabinet.statistics.client.PatientServiceClient;
import ma.cabinet.statistics.model.Appointment;
import ma.cabinet.statistics.model.Billing;
import ma.cabinet.statistics.model.Consultation;
import ma.cabinet.statistics.model.Patient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatisticsService {

    @Autowired
    private PatientServiceClient patientServiceClient;
    
    @Autowired(required = false)
    private ConsultationServiceClient consultationServiceClient;
    
    @Autowired(required = false)
    private AppointmentServiceClient appointmentServiceClient;
    
    @Autowired(required = false)
    private BillingServiceClient billingServiceClient;

    public Map<String, Object> getGlobalStats() {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            List<Patient> patients = patientServiceClient.getAllPatients();
            stats.put("totalPatients", patients.size());
        } catch (Exception e) {
            stats.put("totalPatients", 0);
            stats.put("patientError", e.getMessage());
        }
        
        try {
            if (consultationServiceClient != null) {
                List<Consultation> consultations = consultationServiceClient.getAllConsultations();
                stats.put("totalConsultations", consultations.size());
            } else {
                stats.put("totalConsultations", 0);
            }
        } catch (Exception e) {
            stats.put("totalConsultations", 0);
        }
        
        try {
            if (appointmentServiceClient != null) {
                List<Appointment> appointments = appointmentServiceClient.getAllAppointments();
                stats.put("totalAppointments", appointments.size());
                long confirmed = appointments.stream().filter(a -> "CONFIRMED".equalsIgnoreCase(a.getStatus())).count();
                stats.put("confirmedAppointments", confirmed);
            } else {
                stats.put("totalAppointments", 0);
            }
        } catch (Exception e) {
            stats.put("totalAppointments", 0);
        }
        
        try {
            if (billingServiceClient != null) {
                List<Billing> billings = billingServiceClient.getAllBillings();
                double totalRevenue = billings.stream().mapToDouble(b -> b.getMontant() != null ? b.getMontant() : 0.0).sum();
                stats.put("totalRevenue", totalRevenue);
            } else {
                stats.put("totalRevenue", 0.0);
            }
        } catch (Exception e) {
            stats.put("totalRevenue", 0.0);
        }
        
        return stats;
    }
    
    public Map<String, Object> getDoctorDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        try {
            if (appointmentServiceClient != null) {
                List<Appointment> appointments = appointmentServiceClient.getAllAppointments();
                java.time.LocalDate today = java.time.LocalDate.now();
                long todayAppointments = appointments.stream()
                    .filter(a -> a.getDateRdv() != null && 
                        a.getDateRdv().toLocalDate().equals(today))
                    .count();
                dashboard.put("todayAppointments", todayAppointments);
                dashboard.put("totalAppointments", appointments.size());
            } else {
                dashboard.put("todayAppointments", 0);
                dashboard.put("totalAppointments", 0);
            }
        } catch (Exception e) {
            dashboard.put("todayAppointments", 0);
            dashboard.put("totalAppointments", 0);
        }
        
        try {
            if (consultationServiceClient != null) {
                List<Consultation> consultations = consultationServiceClient.getAllConsultations();
                dashboard.put("totalConsultations", consultations.size());
                java.time.LocalDate today = java.time.LocalDate.now();
                long todayConsultations = consultations.stream()
                    .filter(c -> c.getDateConsultation() != null && 
                        c.getDateConsultation().toLocalDate().equals(today))
                    .count();
                dashboard.put("todayConsultations", todayConsultations);
            } else {
                dashboard.put("totalConsultations", 0);
                dashboard.put("todayConsultations", 0);
            }
        } catch (Exception e) {
            dashboard.put("totalConsultations", 0);
            dashboard.put("todayConsultations", 0);
        }
        
        try {
            List<Patient> patients = patientServiceClient.getAllPatients();
            dashboard.put("totalPatients", patients.size());
        } catch (Exception e) {
            dashboard.put("totalPatients", 0);
        }
        
        return dashboard;
    }
    
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();
        
        // Global statistics
        dashboard.putAll(getGlobalStats());
        
        // Additional admin-specific metrics
        try {
            if (billingServiceClient != null) {
                List<Billing> billings = billingServiceClient.getAllBillings();
                double totalRevenue = billings.stream()
                    .mapToDouble(b -> b.getMontant() != null ? b.getMontant() : 0.0)
                    .sum();
                dashboard.put("totalRevenue", totalRevenue);
                
                long paidInvoices = billings.stream()
                    .filter(b -> b.getStatutPaiement() != null && 
                        ("PAYE".equalsIgnoreCase(b.getStatutPaiement()) || "PAID".equalsIgnoreCase(b.getStatutPaiement())))
                    .count();
                dashboard.put("paidInvoices", paidInvoices);
                dashboard.put("totalInvoices", billings.size());
            } else {
                dashboard.put("totalRevenue", 0.0);
                dashboard.put("paidInvoices", 0);
                dashboard.put("totalInvoices", 0);
            }
        } catch (Exception e) {
            dashboard.put("totalRevenue", 0.0);
            dashboard.put("paidInvoices", 0);
            dashboard.put("totalInvoices", 0);
        }
        
        return dashboard;
    }
}
