package com.university.attendance.model;

/**
 * Énumération des rôles utilisateurs dans le système
 */
public enum Role {
    ETUDIANT,           // Étudiant
    ENSEIGNANT,         // Enseignant
    CHEF_DEPARTEMENT,   // Chef de département
    SECRETARIAT,        // Secrétariat (gestion étudiants, groupes, séances)
    ADMIN,              // Administration/Scolarité
    SUPER_ADMIN         // Super administrateur (optionnel)
}
