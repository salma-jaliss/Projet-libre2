package com.medical.chatbot.service;

import com.medical.chatbot.client.AppointmentClient;
import com.medical.chatbot.client.CabinetClient;
import com.medical.chatbot.dto.CabinetDTO;
import com.medical.chatbot.dto.ChatbotRequest;
import com.medical.chatbot.dto.ChatbotResponse;
import com.medical.chatbot.dto.CreateRendezVousRequest;
import com.medical.chatbot.enums.MotifRendezvous;
import com.medical.chatbot.dto.RendezVousDTO;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final AppointmentClient appointmentClient;
    private final CabinetClient cabinetClient;
    private final NLPUtils nlpUtils;
    private final LevenshteinDistance levenshtein = new LevenshteinDistance();
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    // Gestion de la session en mémoire
    private final Map<String, SessionContext> sessions = new ConcurrentHashMap<>();

    // Intentions avec mots-clés étendus
    private final List<String> INTENT_DISPO = Arrays.asList("disponibilité", "disponible", "disponibilités", "créneau",
            "créneaux", "libre", "libres", "horaires", "quand êtes-vous", "quand est-ce", "quelles heures",
            "quelles sont les disponibilités", "quels créneaux");

    private final List<String> INTENT_RDV = Arrays.asList("rendez-vous", "rdv", "rendez vous", "prendre rdv",
            "prendre rendez-vous", "réserver", "reserver", "je veux prendre", "j'aimerais prendre",
            "souhaiterais prendre",
            "je souhaite", "je veux un", "j'aimerais un", "besoin d'un rdv", "fixer", "programmer", "planifier");

    private final List<String> INTENT_INFO = Arrays.asList("cabinet", "information", "infos", "adresse", "téléphone",
            "telephone", "tel", "contact", "où est", "ou est", "localisation", "où se trouve", "ou se trouve", "situé",
            "situe",
            "comment contacter", "coordonnées", "coordonnees");

    private final List<String> INTENT_ANNULATION = Arrays.asList("annuler", "supprimer", "retirer", "cancel", "annule",
            "supprime", "retire", "annulation", "je veux annuler", "je souhaite annuler", "j'aimerais annuler");

    private final List<String> INTENT_SALUTATION = Arrays.asList("bonjour", "salut", "hello", "hi", "coucou",
            "bonsoir", "bonne journée", "salutations", "hey");

    private final List<String> INTENT_AU_REVOIR = Arrays.asList("au revoir", "aurevoir", "au-revoir", "bye", "bye bye",
            "goodbye", "à bientôt", "a bientot", "à plus", "a plus", "salut", "ciao", "adieu", "à la prochaine",
            "a la prochaine", "bonne journée", "bonne soirée", "bonne soiree");

    private final List<String> INTENT_REMERCIEMENT = Arrays.asList("merci", "merci beaucoup", "remercier", "thanks",
            "thank you", "merci bien", "je vous remercie", "c'est gentil");

    private final List<String> INTENT_MES_RDV = Arrays.asList("mes rendez-vous", "mes rdv", "mon rendez-vous",
            "mes rendez vous", "liste de mes", "mes consultations", "quels sont mes", "voir mes rendez-vous",
            "consulter mes rendez-vous", "mes appointments");

    private final List<String> INTENT_AIDE = Arrays.asList("aide", "help", "comment faire", "que puis-je",
            "qu'est-ce que", "quoi", "aide-moi", "aidez-moi", "je ne comprends pas", "explique", "expliquer");

    public ChatbotResponse processMessage(ChatbotRequest request) {
        String sessionKey;
        if (request.getPatientId() != null) {
            sessionKey = "P:" + request.getPatientId();
        } else if (request.getSessionId() != null && !request.getSessionId().trim().isEmpty()) {
            sessionKey = "S:" + request.getSessionId().trim();
        } else {
            sessionKey = "ANON:" + java.util.UUID.randomUUID().toString();
            log.info("Aucune identification fournie pour le message; création d'une session anonyme : {}", sessionKey);
        }

        SessionContext session = sessions.computeIfAbsent(sessionKey, k -> new SessionContext());
        session.setCabinetId(request.getCabinetId());
        session.addToHistory(request.getMessage());
        Long patientId = request.getPatientId();

        String rawMessage = request.getMessage();
        String userMessage = normalizeText(rawMessage);

        try {
            // Gestion de l'annulation globale
            if (detectIntent(userMessage, INTENT_ANNULATION) && session.getState() != SessionContext.ChatState.IDLE) {
                session.reset();
                return ChatbotResponse.builder()
                        .response("D'accord, j'ai annulé l'opération en cours. Que puis-je faire pour vous ? " +
                                "Vous pouvez consulter les disponibilités, prendre un rendez-vous, voir vos rendez-vous, "
                                +
                                "annuler un rendez-vous ou obtenir des informations sur le cabinet.")
                        .build();
            }

            // Machine à états
            switch (session.getState()) {
                case AWAITING_DATE_FOR_AVAILABILITY:
                    return handleDateForAvailability(session, rawMessage);
                case AWAITING_DATE_FOR_BOOKING:
                    return handleDateForBooking(session, rawMessage);
                case AWAITING_TIME_FOR_BOOKING:
                    return handleTimeForBooking(session, rawMessage, patientId);
                case AWAITING_RDV_ID_FOR_CANCELLATION:
                    return handleRdvIdForCancellation(session, rawMessage, patientId);
                case AWAITING_CONFIRMATION:
                    return handleConfirmation(session, userMessage, rawMessage, patientId);
                case IDLE:
                default:
                    return handleIdleState(session, userMessage, rawMessage, request.getCabinetId(), patientId);
            }
        } catch (Exception e) {
            log.error("Erreur lors du traitement du message", e);
            session.setErrorCount(session.getErrorCount() + 1);
            String errorMessage = "Je suis désolé, une erreur s'est produite. ";
            if (session.getErrorCount() >= 3) {
                errorMessage += "Voulez-vous recommencer ou avez-vous besoin d'aide ?";
                session.reset();
            } else {
                errorMessage += "Pouvez-vous reformuler votre demande ?";
            }
            return ChatbotResponse.builder().response(errorMessage).build();
        }
    }

    // --- Gestionnaires d'états ---

    private ChatbotResponse handleIdleState(SessionContext session, String normalizedMsg, String rawMsg, Long cabinetId,
            Long patientId) {
        LocalDate extractedDate = nlpUtils.extractDate(rawMsg);
        LocalTime extractedTime = nlpUtils.extractTime(rawMsg);
        boolean containsInvalidTime = nlpUtils.containsInvalidTime(rawMsg);

        // Salutation
        if (detectIntent(normalizedMsg, INTENT_SALUTATION)) {
            session.setLastIntent("SALUTATION");
            session.setErrorCount(0);
            return ChatbotResponse.builder()
                    .response("Bonjour ! 👋 Je suis l'assistant virtuel du cabinet médical. " +
                            "Je peux vous aider à :\n" +
                            "• Consulter les disponibilités\n" +
                            "• Prendre un rendez-vous\n" +
                            "• Voir vos rendez-vous\n" +
                            "• Annuler un rendez-vous\n" +
                            "• Obtenir des informations sur le cabinet\n\n" +
                            "Comment puis-je vous aider aujourd'hui ?")
                    .build();
        }

        // Au revoir
        if (detectIntent(normalizedMsg, INTENT_AU_REVOIR)) {
            session.setLastIntent("AU_REVOIR");
            session.setErrorCount(0);
            session.reset();
            return ChatbotResponse.builder()
                    .response(
                            "Au revoir ! 😊 N'hésitez pas à revenir si vous avez besoin d'aide. Prenez soin de vous !")
                    .build();
        }

        // Aide
        if (detectIntent(normalizedMsg, INTENT_AIDE)) {
            session.setLastIntent("AIDE");
            session.setErrorCount(0);
            return ChatbotResponse.builder()
                    .response("Bien sûr ! Je peux vous aider avec :\n\n" +
                            "📅 **Prendre un rendez-vous** : Dites \"Je veux prendre rendez-vous\" ou \"Je souhaite un rdv\"\n"
                            +
                            "🔍 **Voir les disponibilités** : Dites \"Quelles sont les disponibilités ?\" ou \"Disponibilités pour demain\"\n"
                            +
                            "📋 **Mes rendez-vous** : Dites \"Mes rendez-vous\" ou \"Liste de mes rdv\"\n" +
                            "❌ **Annuler un rendez-vous** : Dites \"Je veux annuler\" suivi du numéro de rendez-vous\n"
                            +
                            "ℹ️ **Informations cabinet** : Dites \"Informations\" ou \"Adresse du cabinet\"\n\n" +
                            "Que souhaitez-vous faire ?")
                    .build();
        }

        // Consulter mes rendez-vous
        if (detectIntent(normalizedMsg, INTENT_MES_RDV)) {
            session.setLastIntent("MES_RDV");
            session.setErrorCount(0);
            return handleViewMyAppointments(patientId);
        }

        // Annulation de rendez-vous
        if (detectIntent(normalizedMsg, INTENT_ANNULATION)) {
            session.setLastIntent("ANNULATION");
            session.setErrorCount(0);
            Long extractedId = nlpUtils.extractId(rawMsg);
            if (extractedId != null) {
                return handleCancelAppointment(extractedId, patientId, session);
            } else {
                List<RendezVousDTO> appointments = getMyAppointments(patientId);
                if (appointments == null) {
                    return ChatbotResponse.builder()
                            .response("Désolé, je ne peux pas accéder à vos rendez-vous pour le moment. Veuillez réessayer plus tard.")
                            .build();
                }
                if (appointments.isEmpty()) {
                    return ChatbotResponse.builder()
                            .response("Vous n'avez actuellement aucun rendez-vous à annuler.")
                            .build();
                }
                session.setState(SessionContext.ChatState.AWAITING_RDV_ID_FOR_CANCELLATION);
                String appointmentsList = formatAppointmentsList(appointments);
                return ChatbotResponse.builder()
                        .response("Voici vos rendez-vous :\n\n" + appointmentsList +
                                "\nQuel rendez-vous souhaitez-vous annuler ? Indiquez le numéro.")
                        .data(appointments)
                        .build();
            }
        }

        // Prendre rendez-vous
        boolean isRdvIntent = detectIntent(normalizedMsg, INTENT_RDV);
        if (isRdvIntent) {
            session.setLastIntent("RDV");
            session.setErrorCount(0);
            
            if (extractedDate != null && isDateInPast(extractedDate)) {
                 return ChatbotResponse.builder()
                        .response("La date indiquée (" + extractedDate.format(DATE_FORMATTER) + ") est passée. Veuillez choisir une date future.")
                        .build();
            }
            
            if (containsInvalidTime) {
                return ChatbotResponse.builder()
                        .response("L'heure indiquée n'est pas valide. Veuillez indiquer une heure correcte (ex: 14h30).")
                        .build();
            }

            if (extractedDate != null && extractedTime != null) {
                if (!isTimeWithinWorkingHours(extractedTime)) {
                    return ChatbotResponse.builder()
                            .response("Le cabinet est fermé à " + extractedTime.format(TIME_FORMATTER) + ". Les horaires sont de 09h00 à 17h00.")
                            .build();
                }
                return attemptDirectBooking(session, extractedDate, extractedTime, cabinetId, patientId);
            } else if (extractedDate != null) {
                session.setTempDate(extractedDate);
                session.setState(SessionContext.ChatState.AWAITING_TIME_FOR_BOOKING);
                List<LocalTime> slots = getAvailableSlots(extractedDate, cabinetId);
                if (slots == null) {
                    session.setState(SessionContext.ChatState.IDLE);
                    return ChatbotResponse.builder()
                            .response("Désolé, je ne peux pas vérifier les disponibilités pour le moment. Veuillez réessayer plus tard.")
                            .build();
                }
                String dateStr = extractedDate.format(DATE_FORMATTER);
                if (slots.isEmpty()) {
                    return ChatbotResponse.builder()
                            .response("Désolé, il n'y a plus de créneaux disponibles pour le " + dateStr + ". " +
                                    "Souhaitez-vous choisir une autre date ?")
                            .build();
                }
                String slotsStr = formatTimeSlots(slots);
                return ChatbotResponse.builder()
                        .response("Parfait ! Pour le " + dateStr + ", voici les heures disponibles :\n" + slotsStr +
                                "\n\nQuelle heure vous convient ?")
                        .data(slots)
                        .build();
            } else {
                session.setState(SessionContext.ChatState.AWAITING_DATE_FOR_BOOKING);
                return ChatbotResponse.builder()
                        .response("Parfait ! Je vais vous aider à prendre un rendez-vous. 📅\n" +
                                "Pour quelle date souhaitez-vous venir ? (ex: \"demain\", \"lundi\", \"25/12\", \"dans 3 jours\")")
                        .build();
            }
        }

        // Disponibilités
        boolean isDispoIntent = detectIntent(normalizedMsg, INTENT_DISPO);
        if (isDispoIntent) {
            session.setLastIntent("DISPO");
            session.setErrorCount(0);
            LocalDate dispoDate = nlpUtils.extractDate(rawMsg);
            
            if (dispoDate != null && isDateInPast(dispoDate)) {
                 return ChatbotResponse.builder()
                        .response("La date indiquée (" + dispoDate.format(DATE_FORMATTER) + ") est passée. Veuillez choisir une date future.")
                        .build();
            }

            if (dispoDate != null) {
                List<LocalTime> slots = getAvailableSlots(dispoDate, cabinetId);
                if (slots == null) {
                    return ChatbotResponse.builder()
                            .response("Désolé, je ne peux pas vérifier les disponibilités pour le moment. Veuillez réessayer plus tard.")
                            .build();
                }
                String dateStr = dispoDate.format(DATE_FORMATTER);
                if (slots.isEmpty()) {
                    return ChatbotResponse.builder()
                            .response("Désolé, il n'y a plus de créneaux disponibles pour le " + dateStr + ". " +
                                    "Souhaitez-vous choisir une autre date ?")
                            .data(slots)
                            .build();
                }
                String slotsStr = formatTimeSlots(slots);
                return ChatbotResponse.builder()
                        .response("✅ Voici les créneaux disponibles pour le " + dateStr + " :\n" + slotsStr +
                                "\n\nSouhaitez-vous réserver l'un de ces créneaux ?")
                        .data(slots)
                        .build();
            } else {
                session.setState(SessionContext.ChatState.AWAITING_DATE_FOR_AVAILABILITY);
                return ChatbotResponse.builder()
                        .response("Pour quelle date souhaitez-vous connaître les disponibilités ?\n" +
                                "Vous pouvez indiquer : \"demain\", \"lundi\", \"25/12\", \"dans 3 jours\", \"après-demain\", etc.")
                        .build();
            }
        }

        // RDV Implicite (Date détectée sans intention explicite)
        if (extractedDate != null && !isRdvIntent && !isDispoIntent) {
            if (isDateInPast(extractedDate)) {
                // Ignore past dates for implicit intent
            } else {
                session.setLastIntent("RDV_IMPLICITE");
                session.setTempDate(extractedDate);
                session.setState(SessionContext.ChatState.AWAITING_TIME_FOR_BOOKING);
                List<LocalTime> slots = getAvailableSlots(extractedDate, cabinetId);
                if (slots == null) {
                    session.setState(SessionContext.ChatState.IDLE);
                    return ChatbotResponse.builder()
                            .response("Je comprends que vous parlez du " + extractedDate.format(DATE_FORMATTER) + 
                                    ", mais je ne peux pas vérifier les disponibilités pour le moment.")
                            .build();
                }
                String dateStr = extractedDate.format(DATE_FORMATTER);
                if (slots.isEmpty()) {
                    return ChatbotResponse.builder()
                            .response("Je comprends que vous voulez prendre rendez-vous pour le " + dateStr + ". " +
                                    "Malheureusement, il n'y a plus de créneaux disponibles pour cette date. " +
                                    "Souhaitez-vous choisir une autre date ?")
                            .build();
                }
                String slotsStr = formatTimeSlots(slots);
                return ChatbotResponse.builder()
                        .response("Je comprends que vous souhaitez prendre rendez-vous pour le " + dateStr + ". " +
                                "Voici les heures disponibles :\n" + slotsStr +
                                "\n\nQuelle heure vous convient ?")
                        .data(slots)
                        .build();
            }
        }

        // Heure Implicite
        if (extractedTime != null && !isRdvIntent && !isDispoIntent && extractedDate == null) {
            boolean hasValidWords = normalizedMsg.length() > 2
                    && (normalizedMsg.contains("heure") || normalizedMsg.contains("h") ||
                            normalizedMsg.matches(".*\\d+.*"));

            if (hasValidWords) {
                if (!isTimeWithinWorkingHours(extractedTime)) {
                    return ChatbotResponse.builder()
                            .response("Le cabinet est fermé à " + extractedTime.format(TIME_FORMATTER) + ". Les horaires sont de 09h00 à 17h00.")
                            .build();
                }
                session.setLastIntent("HEURE_IMPLICITE");
                String timeStr = extractedTime.format(TIME_FORMATTER);
                return ChatbotResponse.builder()
                        .response("Je comprends que vous mentionnez l'heure " + timeStr + ". " +
                                "Pour prendre rendez-vous à cette heure, j'ai besoin de connaître la date. " +
                                "Pour quelle date souhaitez-vous ce rendez-vous ? (ex: \"demain\", \"lundi\", \"25/12\")")
                        .build();
            }
        }
        
        // Détection spécifique d'heure invalide sans autre contexte
        if (containsInvalidTime) {
            return ChatbotResponse.builder()
                    .response("L'heure indiquée n'est pas valide. Veuillez indiquer une heure correcte (ex: 14h30).")
                    .build();
        }

        // Informations cabinet
        if (detectIntent(normalizedMsg, INTENT_INFO)) {
            session.setLastIntent("INFO");
            session.setErrorCount(0);
            try {
                CabinetDTO cabinet = getCabinetInfo(cabinetId);
                if (cabinet != null) {
                    String response = "📋 **Informations du cabinet**\n\n";
                    response += "🏥 Nom : " + (cabinet.getNom() != null ? cabinet.getNom() : "Non renseigné") + "\n";
                    if (cabinet.getSpecialite() != null && !cabinet.getSpecialite().isEmpty()) {
                        response += "👨‍⚕️ Spécialité : " + cabinet.getSpecialite() + "\n";
                    }
                    response += "📍 Adresse : "
                            + (cabinet.getAdresse() != null ? cabinet.getAdresse() : "Non renseignée") + "\n";
                    response += "📞 Téléphone : " + (cabinet.getTel() != null ? cabinet.getTel() : "Non renseigné")
                            + "\n";
                    if (cabinet.getDateCreation() != null) {
                        response += "📅 Créé le : " + cabinet.getDateCreation().format(DATE_FORMATTER) + "\n";
                    }
                    return ChatbotResponse.builder().response(response).data(cabinet).build();
                } else {
                    return ChatbotResponse.builder()
                            .response(
                                    "Désolé, je n'ai pas pu récupérer les informations du cabinet. Le cabinet demandé n'existe peut-être pas.")
                            .build();
                }
            } catch (Exception e) {
                log.error("Erreur lors de la récupération des informations du cabinet", e);
                return ChatbotResponse.builder()
                        .response("Désolé, je n'ai pas pu récupérer les informations du cabinet pour le moment. " +
                                "Veuillez réessayer plus tard.")
                        .build();
            }
        }

        // Remerciements
        if (detectIntent(normalizedMsg, INTENT_REMERCIEMENT)) {
            session.setLastIntent("REMERCIEMENT");
            session.setErrorCount(0);
            return ChatbotResponse.builder()
                    .response("De rien ! 😊 N'hésitez pas si vous avez besoin d'autre chose. Bonne journée !")
                    .build();
        }

        // Message non compris
        session.setErrorCount(session.getErrorCount() + 1);
        String fallbackMessage;
        if (session.getErrorCount() >= 1 || normalizedMsg.length() < 4) {
            fallbackMessage = "Je n'ai pas bien compris votre demande. " +
                    "Voici ce que je peux faire pour vous :\n" +
                    "• Consulter les disponibilités\n" +
                    "• Prendre un rendez-vous\n" +
                    "• Voir vos rendez-vous\n" +
                    "• Annuler un rendez-vous\n" +
                    "• Obtenir des informations sur le cabinet\n\n" +
                    "Dites \"aide\" pour plus d'informations.";
        } else {
            fallbackMessage = "Je n'ai pas bien compris votre demande. " +
                    "Pouvez-vous reformuler ? Vous pouvez dire \"aide\" pour voir ce que je peux faire.";
        }
        return ChatbotResponse.builder().response(fallbackMessage).build();
    }

    private ChatbotResponse handleDateForAvailability(SessionContext session, String rawMsg) {
        LocalDate date = nlpUtils.extractDate(rawMsg);
        if (date != null) {
            if (isDateInPast(date)) {
                 return ChatbotResponse.builder()
                        .response("La date indiquée (" + date.format(DATE_FORMATTER) + ") est passée. Veuillez choisir une date future.")
                        .build();
            }
            
            session.setState(SessionContext.ChatState.IDLE);
            session.setErrorCount(0);
            List<LocalTime> slots = getAvailableSlots(date, session.getCabinetId());
            if (slots == null) {
                return ChatbotResponse.builder()
                        .response("Désolé, je ne peux pas vérifier les disponibilités pour le moment. Veuillez réessayer plus tard.")
                        .build();
            }
            String dateStr = date.format(DATE_FORMATTER);
            if (slots.isEmpty()) {
                return ChatbotResponse.builder()
                        .response("Désolé, il n'y a plus de créneaux disponibles pour le " + dateStr + ". " +
                                "Souhaitez-vous choisir une autre date ?")
                        .data(slots)
                        .build();
            }
            String slotsStr = formatTimeSlots(slots);
            return ChatbotResponse.builder()
                    .response("✅ Voici les créneaux disponibles pour le " + dateStr + " :\n" + slotsStr +
                            "\n\nSouhaitez-vous réserver l'un de ces créneaux ?")
                    .data(slots)
                    .build();
        }
        session.setErrorCount(session.getErrorCount() + 1);
        if (session.getErrorCount() >= 2) {
            session.setState(SessionContext.ChatState.IDLE);
            return ChatbotResponse.builder()
                    .response("Je n'ai pas pu comprendre la date que vous avez indiquée. " +
                            "Vous pouvez essayer avec : \"demain\", \"lundi\", \"25/12\", \"dans 3 jours\", etc. " +
                            "Ou dites \"aide\" pour plus d'informations.")
                    .build();
        }
        return ChatbotResponse.builder()
                .response("Je n'ai pas compris la date. Pouvez-vous la reformuler ?\n" +
                        "Exemples valides : \"demain\", \"lundi\", \"25/12\", \"dans 3 jours\", \"après-demain\"")
                .build();
    }

    private ChatbotResponse handleDateForBooking(SessionContext session, String rawMsg) {
        LocalDate date = nlpUtils.extractDate(rawMsg);
        if (date != null) {
            if (isDateInPast(date)) {
                 return ChatbotResponse.builder()
                        .response("La date indiquée (" + date.format(DATE_FORMATTER) + ") est passée. Veuillez choisir une date future.")
                        .build();
            }
            
            session.setTempDate(date);
            session.setState(SessionContext.ChatState.AWAITING_TIME_FOR_BOOKING);
            session.setErrorCount(0);
            List<LocalTime> slots = getAvailableSlots(date, session.getCabinetId());
            if (slots == null) {
                session.setState(SessionContext.ChatState.IDLE);
                return ChatbotResponse.builder()
                        .response("Désolé, je ne peux pas vérifier les disponibilités pour le moment. Veuillez réessayer plus tard.")
                        .build();
            }
            String dateStr = date.format(DATE_FORMATTER);
            if (slots.isEmpty()) {
                session.setState(SessionContext.ChatState.AWAITING_DATE_FOR_BOOKING);
                return ChatbotResponse.builder()
                        .response("Désolé, il n'y a plus de créneaux disponibles pour le " + dateStr + ". " +
                                "Souhaitez-vous choisir une autre date ?")
                        .build();
            }
            String slotsStr = formatTimeSlots(slots);
            return ChatbotResponse.builder()
                    .response("Parfait ! Pour le " + dateStr + ", voici les heures disponibles :\n" + slotsStr +
                            "\n\nQuelle heure vous convient ?")
                    .data(slots)
                    .build();
        }
        session.setErrorCount(session.getErrorCount() + 1);
        if (session.getErrorCount() >= 2) {
            session.setState(SessionContext.ChatState.IDLE);
            return ChatbotResponse.builder()
                    .response("Je n'ai pas pu comprendre la date. Voulez-vous recommencer ou avez-vous besoin d'aide ?")
                    .build();
        }
        return ChatbotResponse.builder()
                .response("Je n'ai pas compris la date. Pouvez-vous la reformuler ?\n" +
                        "Exemples : \"demain\", \"lundi\", \"25/12\", \"dans 3 jours\"")
                .build();
    }

    private ChatbotResponse handleTimeForBooking(SessionContext session, String rawMsg, Long patientId) {
        LocalTime time = nlpUtils.extractTime(rawMsg);
        boolean containsInvalidTime = nlpUtils.containsInvalidTime(rawMsg);
        
        if (containsInvalidTime) {
            return ChatbotResponse.builder()
                    .response("L'heure indiquée n'est pas valide. Veuillez indiquer une heure correcte (ex: 14h30).")
                    .build();
        }
        
        if (time != null) {
            if (!isTimeWithinWorkingHours(time)) {
                return ChatbotResponse.builder()
                        .response("Le cabinet est fermé à " + time.format(TIME_FORMATTER) + ". Les horaires sont de 09h00 à 17h00. Veuillez choisir une autre heure.")
                        .build();
            }
            
            try {
                org.springframework.http.ResponseEntity<Boolean> availResp = appointmentClient
                        .verifierDisponibilite(session.getTempDate(), time, session.getCabinetId());
                Boolean isAvailable = (availResp != null && availResp.getStatusCode().is2xxSuccessful())
                        ? availResp.getBody()
                        : null;

                if (isAvailable != null && !isAvailable) {
                    List<LocalTime> slots = getAvailableSlots(session.getTempDate(), session.getCabinetId());
                    if (slots == null) {
                        return ChatbotResponse.builder()
                                .response("Désolé, ce créneau n'est plus disponible et je ne peux pas récupérer les autres créneaux pour le moment.")
                                .build();
                    }
                    if (slots.isEmpty()) {
                        session.setState(SessionContext.ChatState.AWAITING_DATE_FOR_BOOKING);
                        return ChatbotResponse.builder()
                                .response(
                                        "Désolé, ce créneau n'est plus disponible et il n'y a plus d'autres créneaux pour cette date. "
                                                +
                                                "Souhaitez-vous choisir une autre date ?")
                                .build();
                    }
                    String slotsStr = formatTimeSlots(slots);
                    return ChatbotResponse.builder()
                            .response(
                                    "Désolé, ce créneau n'est plus disponible. Voici les créneaux encore disponibles :\n"
                                            +
                                            slotsStr + "\n\nQuelle heure vous convient ?")
                            .data(slots)
                            .build();
                }

                return attemptBooking(session, session.getTempDate(), time, session.getCabinetId(), patientId);
            } catch (Exception e) {
                log.error("Erreur lors de la vérification de disponibilité", e);
                // Try to book anyway if verification fails, let the booking endpoint decide
                return attemptBooking(session, session.getTempDate(), time, session.getCabinetId(), patientId);
            }
        }
        session.setErrorCount(session.getErrorCount() + 1);
        if (session.getErrorCount() >= 2) {
            session.setState(SessionContext.ChatState.AWAITING_DATE_FOR_BOOKING);
            String dateStr = session.getTempDate() != null ? session.getTempDate().format(DATE_FORMATTER)
                    : "cette date";
            return ChatbotResponse.builder()
                    .response("Je n'ai pas pu comprendre l'heure que vous avez indiquée. " +
                            "Pour le " + dateStr
                            + ", veuillez indiquer une heure valide comme \"14h30\", \"10:00\", \"9h\", \"matin\", \"midi\" ou \"soir\". "
                            +
                            "Souhaitez-vous choisir une autre date ?")
                    .build();
        }
        return ChatbotResponse.builder()
                .response("Je n'ai pas compris l'heure. Pouvez-vous la reformuler ?\n" +
                        "Exemples valides : \"14h30\", \"10:00\", \"9h\", \"matin\", \"midi\", \"soir\"")
                .build();
    }

    private ChatbotResponse handleRdvIdForCancellation(SessionContext session, String rawMsg, Long patientId) {
        Long extractedId = nlpUtils.extractId(rawMsg);
        if (extractedId != null) {
            return handleCancelAppointment(extractedId, patientId, session);
        }
        session.setErrorCount(session.getErrorCount() + 1);
        if (session.getErrorCount() >= 2) {
            session.setState(SessionContext.ChatState.IDLE);
            return ChatbotResponse.builder()
                    .response("Je n'ai pas pu identifier le numéro de rendez-vous à annuler. " +
                            "Pouvez-vous consulter vos rendez-vous et réessayer avec le numéro correct ?")
                    .build();
        }
        return ChatbotResponse.builder()
                .response("Je n'ai pas compris le numéro de rendez-vous. Pouvez-vous l'indiquer à nouveau ? " +
                        "Exemple : \"Annuler le rdv 1\" ou simplement \"1\". " +
                        "Dites \"mes rendez-vous\" pour voir la liste avec les numéros.")
                .build();
    }

    private ChatbotResponse handleConfirmation(SessionContext session, String normalizedMsg, String rawMsg,
            Long patientId) {
        session.setState(SessionContext.ChatState.IDLE);
        return ChatbotResponse.builder()
                .response("Opération confirmée. Que souhaitez-vous faire maintenant ?")
                .build();
    }

    // --- Méthodes utilitaires ---

    private ChatbotResponse attemptDirectBooking(SessionContext session, LocalDate date, LocalTime time,
            Long cabinetId, Long patientId) {
        session.setTempDate(date);
        return attemptBooking(session, date, time, cabinetId, patientId);
    }

    private ChatbotResponse attemptBooking(SessionContext session, LocalDate date, LocalTime time,
            Long cabinetId, Long patientId) {
        
        if (patientId == null) {
             return ChatbotResponse.builder()
                    .response("Je ne peux pas prendre de rendez-vous car je ne parviens pas à vous identifier. Veuillez vous connecter ou fournir votre identifiant.")
                    .build();
        }
        
        CreateRendezVousRequest bookingReq = new CreateRendezVousRequest();
        bookingReq.setCabinetId(cabinetId);
        bookingReq.setPatientId(patientId);
        bookingReq.setUtilisateurId(patientId);
        bookingReq.setDateRdv(date);
        bookingReq.setHeureRdv(time);
        bookingReq.setMotif(MotifRendezvous.CONSULTATION);

        try {
            org.springframework.http.ResponseEntity<RendezVousDTO> resp = appointmentClient
                    .prendreRendezVous(bookingReq);
            if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
                throw new IllegalStateException(
                        "Impossible de réserver le rendez-vous (service indisponible ou réponse invalide)");
            }
            RendezVousDTO rdv = resp.getBody();
            session.setState(SessionContext.ChatState.IDLE);
            session.setTempDate(null);
            session.setErrorCount(0);
            String dateStr = rdv.getDateRdv().format(DATE_FORMATTER);
            String timeStr = rdv.getHeureRdv().format(TIME_FORMATTER);
            return ChatbotResponse.builder()
                    .response("✅ Parfait ! Votre rendez-vous est confirmé pour le " + dateStr + " à " + timeStr
                            + ".\n\n" +
                            "Numéro de rendez-vous : #" + rdv.getIdRendezVous() + "\n" +
                            "Statut : " + rdv.getStatut() + "\n\n" +
                            "N'oubliez pas de venir à l'heure. À bientôt ! 😊")
                    .data(rdv)
                    .build();
        } catch (FeignException.BadRequest e) {
            log.warn("Erreur de validation lors de la réservation: {}", e.getMessage());
            return ChatbotResponse.builder()
                    .response("Je n'ai pas pu réserver ce rendez-vous. Il semble y avoir un problème avec les informations fournies ou le créneau n'est pas valide.")
                    .build();
        } catch (FeignException.Conflict e) {
            log.info("Créneau déjà pris: {} {}", date, time);
            session.setErrorCount(session.getErrorCount() + 1);
            List<LocalTime> slots = getAvailableSlots(date, cabinetId);
            if (slots == null || slots.isEmpty()) {
                session.setState(SessionContext.ChatState.AWAITING_DATE_FOR_BOOKING);
                return ChatbotResponse.builder()
                        .response(
                                "Désolé, ce créneau vient d'être réservé et il n'y a plus d'autres créneaux pour cette date. "
                                        +
                                        "Souhaitez-vous choisir une autre date ?")
                        .build();
            }
            String slotsStr = formatTimeSlots(slots);
            return ChatbotResponse.builder()
                    .response("Désolé, ce créneau n'est plus disponible. Voici les créneaux encore disponibles pour le "
                            +
                            date.format(DATE_FORMATTER) + " :\n" + slotsStr + "\n\nQuelle heure vous convient ?")
                    .data(slots)
                    .build();
        } catch (Exception e) {
            log.error("Erreur lors de la réservation", e);
            return ChatbotResponse.builder()
                    .response("Une erreur technique est survenue lors de la réservation. Veuillez réessayer plus tard.")
                    .build();
        }
    }

    private ChatbotResponse handleViewMyAppointments(Long patientId) {
        if (patientId == null) {
             return ChatbotResponse.builder()
                    .response("Je ne peux pas accéder à vos rendez-vous car je ne parviens pas à vous identifier.")
                    .build();
        }
        
        List<RendezVousDTO> appointments = getMyAppointments(patientId);
        if (appointments == null) {
            return ChatbotResponse.builder()
                    .response("Désolé, je ne peux pas accéder à vos rendez-vous pour le moment. Veuillez réessayer plus tard.")
                    .build();
        }
        if (appointments.isEmpty()) {
            return ChatbotResponse.builder()
                    .response("Vous n'avez actuellement aucun rendez-vous programmé. " +
                            "Souhaitez-vous en prendre un ?")
                    .data(new ArrayList<>())
                    .build();
        }
        String appointmentsList = formatAppointmentsList(appointments);
        return ChatbotResponse.builder()
                .response("📅 **Vos rendez-vous**\n\n" + appointmentsList +
                        "\n\nSouhaitez-vous annuler l'un de ces rendez-vous ou prendre un nouveau rendez-vous ?")
                .data(appointments)
                .build();
    }

    private ChatbotResponse handleCancelAppointment(Long rdvId, Long patientId, SessionContext session) {
        if (patientId == null) {
             return ChatbotResponse.builder()
                    .response("Je ne peux pas annuler de rendez-vous car je ne parviens pas à vous identifier.")
                    .build();
        }
        
        try {
            List<RendezVousDTO> myAppointments = getMyAppointments(patientId);
            if (myAppointments == null) {
                return ChatbotResponse.builder()
                        .response("Désolé, je ne peux pas vérifier vos rendez-vous pour le moment. Veuillez réessayer plus tard.")
                        .build();
            }
            boolean isMyAppointment = myAppointments.stream().anyMatch(rdv -> rdv.getIdRendezVous().equals(rdvId));

            if (!isMyAppointment) {
                return ChatbotResponse.builder()
                        .response("Désolé, je n'ai pas trouvé de rendez-vous avec le numéro #" + rdvId
                                + " dans votre liste. " +
                                "Pouvez-vous vérifier le numéro ?")
                        .build();
            }

            cancelAppointment(rdvId);

            session.setState(SessionContext.ChatState.IDLE);
            session.setErrorCount(0);
            return ChatbotResponse.builder()
                    .response("✅ Votre rendez-vous #" + rdvId + " a été annulé avec succès.\n\n" +
                            "Souhaitez-vous prendre un nouveau rendez-vous ou faire autre chose ?")
                    .build();
        } catch (Exception e) {
            log.error("Erreur lors de l'annulation du rendez-vous", e);
            return ChatbotResponse.builder()
                    .response("Désolé, une erreur s'est produite lors de l'annulation. " +
                            "Veuillez réessayer ou contacter le cabinet directement.")
                    .build();
        }
    }

    private List<RendezVousDTO> getMyAppointments(Long patientId) {
        try {
            org.springframework.http.ResponseEntity<java.util.List<RendezVousDTO>> resp = appointmentClient
                    .obtenirRendezVousParPatient(patientId);
            if (resp != null && resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return resp.getBody();
            }
            return new ArrayList<>();
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des rendez-vous", e);
            return null; // Return null to indicate error
        }
    }

    private String formatAppointmentsList(List<RendezVousDTO> appointments) {
        if (appointments == null || appointments.isEmpty()) {
            return "Aucun rendez-vous";
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < appointments.size(); i++) {
            RendezVousDTO rdv = appointments.get(i);
            sb.append(i + 1).append(". 📅 RDV #").append(rdv.getIdRendezVous())
                    .append(" - ").append(rdv.getDateRdv().format(DATE_FORMATTER))
                    .append(" à ").append(rdv.getHeureRdv().format(TIME_FORMATTER))
                    .append(" (").append(rdv.getStatut()).append(")\n");
        }
        return sb.toString();
    }

    private String formatTimeSlots(List<LocalTime> slots) {
        if (slots == null || slots.isEmpty()) {
            return "Aucun créneau disponible";
        }
        return slots.stream()
                .map(time -> time.format(TIME_FORMATTER))
                .collect(Collectors.joining(", "));
    }

    // --- Méthodes Métier ---

    public List<LocalTime> getAvailableSlots(LocalDate date, Long cabinetId) {
        try {
            org.springframework.http.ResponseEntity<java.util.List<RendezVousDTO>> resp = appointmentClient
                    .obtenirRendezVousDujour(date, cabinetId);
            List<RendezVousDTO> booked = (resp != null && resp.getStatusCode().is2xxSuccessful()
                    && resp.getBody() != null) ? resp.getBody() : new ArrayList<>();
            List<LocalTime> allSlots = generateSlots();
            if (booked != null && !booked.isEmpty()) {
                List<LocalTime> bookedTimes = booked.stream()
                        .map(RendezVousDTO::getHeureRdv)
                        .filter(time -> time != null)
                        .collect(java.util.stream.Collectors.toList());
                allSlots.removeAll(bookedTimes);
            }
            return allSlots;
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des créneaux", e);
            return null; // Return null to indicate error
        }
    }

    private List<LocalTime> generateSlots() {
        List<LocalTime> slots = new ArrayList<>();
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);
        int slotMinutes = 30;
        while (!start.isAfter(end.minusMinutes(slotMinutes))) {
            slots.add(start);
            start = start.plusMinutes(slotMinutes);
        }
        return slots;
    }

    public RendezVousDTO bookAppointment(CreateRendezVousRequest request) {
        org.springframework.http.ResponseEntity<RendezVousDTO> resp = appointmentClient.prendreRendezVous(request);
        if (resp == null || !resp.getStatusCode().is2xxSuccessful() || resp.getBody() == null) {
            throw new IllegalStateException(
                    "Impossible de réserver le rendez-vous (service indisponible ou réponse invalide)");
        }
        return resp.getBody();
    }

    public void cancelAppointment(Long id) {
        org.springframework.http.ResponseEntity<Void> resp = appointmentClient.annulerRendezVous(id);
        if (resp == null || !resp.getStatusCode().is2xxSuccessful()) {
            throw new IllegalStateException("Échec de l'annulation du rendez-vous id=" + id);
        }
    }

    public CabinetDTO getCabinetInfo(Long id) {
        try {
            org.springframework.http.ResponseEntity<CabinetDTO> resp = cabinetClient.obtenirCabinetParId(id);
            if (resp != null && resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
                return resp.getBody();
            }
            log.warn("Service cabinet a retourné une réponse invalide pour l'ID: {}", id);
            return null;
        } catch (Exception e) {
            log.error("Erreur lors de l'appel au service cabinet pour l'ID: " + id, e);
            return null;
        }
    }

    private boolean detectIntent(String message, List<String> keywords) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }
        String lowerMessage = message.toLowerCase().trim();

        for (String keyword : keywords) {
            String lowerKeyword = keyword.toLowerCase();
            if (lowerMessage.equals(lowerKeyword)) {
                return true;
            }
            if (lowerKeyword.length() > 5 && lowerMessage.contains(lowerKeyword)) {
                return true;
            }
        }

        String[] words = lowerMessage.split("\\s+");
        for (String word : words) {
            for (String keyword : keywords) {
                String lowerKeyword = keyword.toLowerCase();
                if (word.equals(lowerKeyword)) {
                    return true;
                }
                if (lowerKeyword.length() > 5 && word.length() > 4) {
                    int distance = levenshtein.apply(word, lowerKeyword);
                    if (distance <= 1 && distance < (Math.min(word.length(), lowerKeyword.length()) / 3)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private String normalizeText(String text) {
        if (text == null) {
            return "";
        }
        String normalized = Normalizer.normalize(text.toLowerCase(), Normalizer.Form.NFD);
        return normalized.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
    }
    
    private boolean isDateInPast(LocalDate date) {
        return date.isBefore(LocalDate.now());
    }
    
    private boolean isTimeWithinWorkingHours(LocalTime time) {
        LocalTime start = LocalTime.of(9, 0);
        LocalTime end = LocalTime.of(17, 0);
        return !time.isBefore(start) && !time.isAfter(end);
    }
}
