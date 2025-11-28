package com.university.attendance.model;

import jakarta.persistence.*;

/**
 * Entité représentant la présence/absence d'un étudiant à une séance
 */
@Entity
@Table(name = "presences")
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "seance_id", nullable = false)
    private Seance seance;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPresence statut;

    // Horodatage de la validation du code (pour les présents)
    private java.time.LocalDateTime heureValidation;

    // Indique si la présence a été modifiée manuellement par l'enseignant
    @Column(nullable = false)
    private Boolean modificationManuelle = false;

    @Column(length = 500)
    private String commentaire;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    // Constructeurs
    public Presence() {
    }

    public Presence(Seance seance, User etudiant, StatutPresence statut) {
        this.seance = seance;
        this.etudiant = etudiant;
        this.statut = statut;
        this.modificationManuelle = false;
    }

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Seance getSeance() {
        return seance;
    }

    public void setSeance(Seance seance) {
        this.seance = seance;
    }

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public StatutPresence getStatut() {
        return statut;
    }

    public void setStatut(StatutPresence statut) {
        this.statut = statut;
    }

    public java.time.LocalDateTime getHeureValidation() {
        return heureValidation;
    }

    public void setHeureValidation(java.time.LocalDateTime heureValidation) {
        this.heureValidation = heureValidation;
    }

    public Boolean getModificationManuelle() {
        return modificationManuelle;
    }

    public void setModificationManuelle(Boolean modificationManuelle) {
        this.modificationManuelle = modificationManuelle;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
