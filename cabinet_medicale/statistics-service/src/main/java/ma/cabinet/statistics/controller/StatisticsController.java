package ma.cabinet.statistics.controller;

import ma.cabinet.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/global")
    public ResponseEntity<Map<String, Object>> getGlobalStats() {
        return ResponseEntity.ok(statisticsService.getGlobalStats());
    }
    
    @GetMapping("/dashboard/doctor")
    public ResponseEntity<Map<String, Object>> getDoctorDashboard() {
        return ResponseEntity.ok(statisticsService.getDoctorDashboard());
    }
    
    @GetMapping("/dashboard/admin")
    public ResponseEntity<Map<String, Object>> getAdminDashboard() {
        return ResponseEntity.ok(statisticsService.getAdminDashboard());
    }
}
