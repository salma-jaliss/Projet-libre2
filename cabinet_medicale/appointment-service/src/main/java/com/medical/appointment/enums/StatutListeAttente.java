package com.medical.appointment.enums;

/**
 * Enum représentant le statut d'une entrée dans la liste d'attente
 * Selon le diagramme ERD: ENUM('EN_ATTENTE', 'EN_CONSULTATION', 'TERMINE')
 */
public enum StatutListeAttente {
    EN_ATTENTE,
    EN_CONSULTATION,
    TERMINE
}
