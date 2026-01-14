package com.medical.chatbot.service;

import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class NLPUtils {

    private static final Pattern DATE_PATTERN_DDMM = Pattern.compile("(\\d{1,2})[/-](\\d{1,2})(?:[/-](\\d{2,4}))?");
    private static final Pattern DATE_PATTERN_YYYYMMDD = Pattern.compile("(\\d{4})[-/](\\d{1,2})[-/](\\d{1,2})");
    private static final Pattern DATE_PATTERN_FLEXIBLE = Pattern.compile("(\\d{1,2})[\\.\\s](\\d{1,2})(?:[\\.\\s](\\d{2,4}))?");
    private static final Pattern DATE_PATTERN_TEXT = Pattern.compile("(\\d{1,2})\\s+(janvier|fevrier|février|mars|avril|mai|juin|juillet|aout|août|septembre|octobre|novembre|decembre|décembre)(?:\\s+(\\d{2,4}))?");

    private static final Pattern TIME_PATTERN_HOURS = Pattern.compile("(\\d{1,2})\\s+heures?(?:\\s+(\\d{2}))?");
    private static final Pattern TIME_PATTERN_STRICT = Pattern.compile("(\\d{1,2})(?:[h:](\\d{2})|[h])");
    private static final Pattern TIME_WORDS = Pattern.compile("(matin|midi|soir|après-midi|apres midi|apres-midi)");

    // Pattern pour détecter une tentative d'heure invalide (ex: 25h, 99:99)
    private static final Pattern INVALID_TIME_PATTERN = Pattern.compile("(\\d{2,})[h:](\\d{2})?");

    public LocalDate extractDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        
        text = text.toLowerCase().trim();

        if (containsWord(text, "aujourd'hui", "ce jour", "maintenant")) {
            return LocalDate.now();
        }
        if (text.matches(".*après[-\\s]demain.*") || text.matches(".*apres[-\\s]demain.*")) {
            return LocalDate.now().plusDays(2);
        }
        if (containsWord(text, "demain")) {
            return LocalDate.now().plusDays(1);
        }
        if (containsWord(text, "hier")) {
            return LocalDate.now().minusDays(1);
        }
        
        Pattern daysPattern = Pattern.compile("dans\\s+(\\d+)\\s+jour");
        Matcher daysMatcher = daysPattern.matcher(text);
        if (daysMatcher.find()) {
            int days = Integer.parseInt(daysMatcher.group(1));
            return LocalDate.now().plusDays(days);
        }

        boolean isNextWeek = text.contains("prochain") || text.contains("semaine prochaine");
        
        if (text.contains("lundi")) return isNextWeek ? nextWeekDay(DayOfWeek.MONDAY) : nextDay(DayOfWeek.MONDAY);
        if (text.contains("mardi")) return isNextWeek ? nextWeekDay(DayOfWeek.TUESDAY) : nextDay(DayOfWeek.TUESDAY);
        if (text.contains("mercredi")) return isNextWeek ? nextWeekDay(DayOfWeek.WEDNESDAY) : nextDay(DayOfWeek.WEDNESDAY);
        if (text.contains("jeudi")) return isNextWeek ? nextWeekDay(DayOfWeek.THURSDAY) : nextDay(DayOfWeek.THURSDAY);
        if (text.contains("vendredi")) return isNextWeek ? nextWeekDay(DayOfWeek.FRIDAY) : nextDay(DayOfWeek.FRIDAY);
        if (text.contains("samedi")) return isNextWeek ? nextWeekDay(DayOfWeek.SATURDAY) : nextDay(DayOfWeek.SATURDAY);
        if (text.contains("dimanche")) return isNextWeek ? nextWeekDay(DayOfWeek.SUNDAY) : nextDay(DayOfWeek.SUNDAY);

        Matcher ymdMatcher = DATE_PATTERN_YYYYMMDD.matcher(text);
        if (ymdMatcher.find()) {
            try {
                return LocalDate.of(Integer.parseInt(ymdMatcher.group(1)), Integer.parseInt(ymdMatcher.group(2)), Integer.parseInt(ymdMatcher.group(3)));
            } catch (Exception e) {}
        }

        Matcher dmyMatcher = DATE_PATTERN_DDMM.matcher(text);
        if (dmyMatcher.find()) return parseDateGroups(dmyMatcher.group(1), dmyMatcher.group(2), dmyMatcher.group(3));

        Matcher flexibleMatcher = DATE_PATTERN_FLEXIBLE.matcher(text);
        if (flexibleMatcher.find()) return parseDateGroups(flexibleMatcher.group(1), flexibleMatcher.group(2), flexibleMatcher.group(3));

        Matcher textMatcher = DATE_PATTERN_TEXT.matcher(text);
        if (textMatcher.find()) {
            try {
                int day = Integer.parseInt(textMatcher.group(1));
                int month = convertMonth(textMatcher.group(2));
                int currentYear = LocalDate.now().getYear();
                boolean yearProvided = false;
                if (textMatcher.group(3) != null && !textMatcher.group(3).isEmpty()) {
                    int year = Integer.parseInt(textMatcher.group(3));
                    if (year < 100) year += 2000;
                    currentYear = year;
                    yearProvided = true;
                }
                if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                    LocalDate date = LocalDate.of(currentYear, month, day);
                    if (!yearProvided && date.isBefore(LocalDate.now())) date = date.plusYears(1);
                    return date;
                }
            } catch (Exception e) {}
        }
        return null;
    }
    
    private LocalDate parseDateGroups(String d, String m, String y) {
        try {
            int day = Integer.parseInt(d);
            int month = Integer.parseInt(m);
            int currentYear = LocalDate.now().getYear();
            boolean yearProvided = false;
            if (y != null && !y.isEmpty()) {
                int year = Integer.parseInt(y);
                if (year < 100) year += 2000;
                currentYear = year;
                yearProvided = true;
            }
            if (month >= 1 && month <= 12 && day >= 1 && day <= 31) {
                LocalDate date = LocalDate.of(currentYear, month, day);
                if (!yearProvided && date.isBefore(LocalDate.now())) date = date.plusYears(1);
                return date;
            }
        } catch (Exception e) {}
        return null;
    }

    private int convertMonth(String monthStr) {
        monthStr = monthStr.toLowerCase().replaceAll("[éèê]", "e");
        switch (monthStr) {
            case "janvier": return 1;
            case "fevrier": return 2;
            case "mars": return 3;
            case "avril": return 4;
            case "mai": return 5;
            case "juin": return 6;
            case "juillet": return 7;
            case "aout": return 8;
            case "septembre": return 9;
            case "octobre": return 10;
            case "novembre": return 11;
            case "decembre": return 12;
            default: return 1;
        }
    }

    public LocalTime extractTime(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        text = text.toLowerCase().trim();
        
        Matcher timeWordsMatcher = TIME_WORDS.matcher(text);
        if (timeWordsMatcher.find()) {
            String timeWord = timeWordsMatcher.group(1);
            if (timeWord.contains("matin")) return LocalTime.of(9, 0);
            if (timeWord.contains("midi")) return LocalTime.of(12, 0);
            if (timeWord.contains("après-midi") || timeWord.contains("apres")) return LocalTime.of(14, 0);
            if (timeWord.contains("soir")) return LocalTime.of(16, 0);
        }
        
        Matcher hmMatcher = TIME_PATTERN_HOURS.matcher(text);
        if (hmMatcher.find()) {
            try {
                int hour = Integer.parseInt(hmMatcher.group(1));
                int minute = (hmMatcher.group(2) != null) ? Integer.parseInt(hmMatcher.group(2)) : 0;
                if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60) return LocalTime.of(hour, minute);
            } catch (Exception e) {}
        }
        
        Matcher matcher = TIME_PATTERN_STRICT.matcher(text);
        if (matcher.find()) {
            try {
                int hour = Integer.parseInt(matcher.group(1));
                int minute = (matcher.group(2) != null && !matcher.group(2).isEmpty()) ? Integer.parseInt(matcher.group(2)) : 0;
                if (hour >= 0 && hour < 24 && minute >= 0 && minute < 60) return LocalTime.of(hour, minute);
            } catch (Exception e) {}
        }
        return null;
    }

    /**
     * Vérifie si le texte contient une tentative d'heure invalide (ex: 25h, 99:00)
     */
    public boolean containsInvalidTime(String text) {
        if (text == null) return false;
        Matcher matcher = INVALID_TIME_PATTERN.matcher(text.toLowerCase());
        if (matcher.find()) {
            try {
                int hour = Integer.parseInt(matcher.group(1));
                int minute = (matcher.group(2) != null) ? Integer.parseInt(matcher.group(2)) : 0;
                return hour >= 24 || minute >= 60;
            } catch (NumberFormatException e) {
                return true;
            }
        }
        return false;
    }

    public Long extractId(String text) {
        if (text == null || text.trim().isEmpty()) return null;
        String trimmedText = text.trim();
        
        // 1. Essayer de trouver un ID avec contexte explicite (rdv 123, #123)
        Pattern idPattern = Pattern.compile("(?:rdv|rendez-vous|appointment|id|numéro|numero|n°|no)\\s*(?:n°|no|numero|#)?\\s*(\\d+)", Pattern.CASE_INSENSITIVE);
        Matcher matcher = idPattern.matcher(text);
        if (matcher.find()) {
            try { return Long.parseLong(matcher.group(1)); } catch (Exception e) {}
        }
        
        // 2. Si le message est court (ex: "123" ou "annuler 123"), chercher n'importe quel nombre
        if (trimmedText.length() < 20) {
            Pattern simpleNumber = Pattern.compile("(\\d+)");
            Matcher simpleMatcher = simpleNumber.matcher(trimmedText);
            if (simpleMatcher.find()) {
                try { return Long.parseLong(simpleMatcher.group(1)); } catch (Exception e) {}
            }
        }
        
        return null;
    }

    private boolean containsWord(String text, String... words) {
        for (String word : words) {
            if (text.contains(word)) return true;
        }
        return false;
    }

    private LocalDate nextDay(DayOfWeek dayOfWeek) {
        LocalDate today = LocalDate.now();
        LocalDate next = today.with(TemporalAdjusters.nextOrSame(dayOfWeek));
        if (next.equals(today)) next = today.with(TemporalAdjusters.next(dayOfWeek));
        return next;
    }

    private LocalDate nextWeekDay(DayOfWeek dayOfWeek) {
        return LocalDate.now().with(TemporalAdjusters.next(dayOfWeek));
    }
}
