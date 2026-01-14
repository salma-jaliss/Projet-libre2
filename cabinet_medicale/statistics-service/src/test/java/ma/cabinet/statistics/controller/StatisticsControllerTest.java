package ma.cabinet.statistics.controller;

import ma.cabinet.statistics.service.StatisticsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StatisticsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatisticsService statisticsService;

    @Test
    void getGlobalStats_ShouldReturnStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalPatients", 10);
        
        when(statisticsService.getGlobalStats()).thenReturn(stats);

        mockMvc.perform(get("/api/statistics/global"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalPatients").value(10));
    }
}
