package com.university.attendance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * Entité représentant l'affectation d'un étudiant à un groupe TD/TP
 * Table de liaison entre User (étudiant) et Groupe
 */
@Entity
@Table(name = "groupe_etudiants")
public class GroupeEtudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "etudiant_id", nullable = false)
    @JsonIgnoreProperties({"formation", "departement", "motDePasse", "actif", "createdAt", "updatedAt"})
    private User etudiant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "groupe_id", nullable = false)
    @JsonIgnoreProperties({"formation", "etudiants"})
    private Groupe groupe;

    @Column(name = "date_affectation")
    private java.time.LocalDateTime dateAffectation;

    // Constructeurs
    public GroupeEtudiant() {
    }

    public GroupeEtudiant(User etudiant, Groupe groupe) {
        this.etudiant = etudiant;
        this.groupe = groupe;
        this.dateAffectation = java.time.LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        dateAffectation = java.time.LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public java.time.LocalDateTime getDateAffectation() {
        return dateAffectation;
    }

    public void setDateAffectation(java.time.LocalDateTime dateAffectation) {
        this.dateAffectation = dateAffectation;
    }
}
