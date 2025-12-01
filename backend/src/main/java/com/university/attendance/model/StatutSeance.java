package com.university.attendance.model;

/**
 * Statut d'une séance de cours
 */
public enum StatutSeance {
    PREVUE,     // Séance prévue (créée initialement)
    REPORTEE,   // Séance reportée (modifiée)
    EN_COURS,   // Séance en cours
    TERMINEE,   // Séance terminée
    ANNULEE     // Séance annulée
}
