package com.medical.chatbot.service;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.time.LocalTime;
import static org.junit.jupiter.api.Assertions.*;

class NLPUtilsTest {

    private final NLPUtils nlpUtils = new NLPUtils();

    @Test
    void testDateFormats() {
        // 1. "25/12/2025"
        LocalDate d1 = nlpUtils.extractDate("25/12/2025");
        assertEquals(LocalDate.of(2025, 12, 25), d1, "Failed to parse 25/12/2025");

        // 2. "25-12-2025"
        LocalDate d2 = nlpUtils.extractDate("25-12-2025");
        assertEquals(LocalDate.of(2025, 12, 25), d2, "Failed to parse 25-12-2025");

        // 3. "25 décembre 2025"
        LocalDate d3 = nlpUtils.extractDate("25 décembre 2025");
        assertEquals(LocalDate.of(2025, 12, 25), d3, "Failed to parse '25 décembre 2025'");

        // 4. "1 janvier" (without year, should be next occurrence)
        LocalDate d4 = nlpUtils.extractDate("1 janvier");
        assertNotNull(d4);
        assertTrue(d4.isAfter(LocalDate.now().minusDays(1)));

        // 5. "01.01.2025"
        LocalDate d5 = nlpUtils.extractDate("01.01.2025");
        assertEquals(LocalDate.of(2025, 1, 1), d5, "Failed to parse '01.01.2025'");
        
        // 6. "25 12 2025"
        LocalDate d6 = nlpUtils.extractDate("25 12 2025");
        assertEquals(LocalDate.of(2025, 12, 25), d6, "Failed to parse '25 12 2025'");
    }

    @Test
    void testTimeFormats() {
        // 1. "14h30"
        LocalTime t1 = nlpUtils.extractTime("14h30");
        assertEquals(LocalTime.of(14, 30), t1);

        // 2. "14:30"
        LocalTime t2 = nlpUtils.extractTime("14:30");
        assertEquals(LocalTime.of(14, 30), t2);

        // 3. "14.30"
        LocalTime t3 = nlpUtils.extractTime("14.30");
        assertEquals(LocalTime.of(14, 30), t3);
        
        // 4. "9h"
        LocalTime t4 = nlpUtils.extractTime("9h");
        assertEquals(LocalTime.of(9, 0), t4);
        
        // 5. "15 heures"
        LocalTime t5 = nlpUtils.extractTime("15 heures");
        assertEquals(LocalTime.of(15, 0), t5);
    }
    
    @Test
    void testExtractId() {
        assertEquals(1L, nlpUtils.extractId("Je veux annuler le rendez-vous 1"));
        assertEquals(123L, nlpUtils.extractId("rdv #123"));
        assertEquals(456L, nlpUtils.extractId("456"));
    }
}
