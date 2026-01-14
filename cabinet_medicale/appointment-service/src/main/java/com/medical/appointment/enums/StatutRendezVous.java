package com.medical.appointment.enums;

/**
 * Enum représentant le statut d'un rendez-vous
 * Selon le diagramme ERD: ENUM('CONFIRME', 'ANNULE', 'EN_ATTENTE', 'TERMINE')
 */
public enum StatutRendezVous {
    CONFIRME,
    ANNULE,
    EN_ATTENTE,
    TERMINE
}
