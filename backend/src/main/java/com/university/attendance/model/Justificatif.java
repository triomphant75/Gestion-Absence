package com.university.attendance.model;

import jakarta.persistence.*;

/**
 * Entité représentant un justificatif d'absence
 */
@Entity
@Table(name = "justificatifs")
public class Justificatif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;

    @ManyToOne
    @JoinColumn(name = "absence_id", nullable = false)
    private Presence absence;  // Référence à l'absence justifiée

    @Column(length = 500)
    private String motif;

    // Chemin du fichier uploadé (PDF ou image)
    @Column(nullable = false)
    private String fichierPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutJustificatif statut = StatutJustificatif.EN_ATTENTE;

    // Personne qui a validé/refusé le justificatif
    @ManyToOne
    @JoinColumn(name = "validateur_id")
    private User validateur;

    @Column(length = 500)
    private String commentaireValidation;

    @Column(name = "date_validation")
    private java.time.LocalDateTime dateValidation;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    // Constructeurs
    public Justificatif() {
    }

    public Justificatif(User etudiant, Presence absence, String motif, String fichierPath) {
        this.etudiant = etudiant;
        this.absence = absence;
        this.motif = motif;
        this.fichierPath = fichierPath;
        this.statut = StatutJustificatif.EN_ATTENTE;
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

    public User getEtudiant() {
        return etudiant;
    }

    public void setEtudiant(User etudiant) {
        this.etudiant = etudiant;
    }

    public Presence getAbsence() {
        return absence;
    }

    public void setAbsence(Presence absence) {
        this.absence = absence;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getFichierPath() {
        return fichierPath;
    }

    public void setFichierPath(String fichierPath) {
        this.fichierPath = fichierPath;
    }

    public StatutJustificatif getStatut() {
        return statut;
    }

    public void setStatut(StatutJustificatif statut) {
        this.statut = statut;
    }

    public User getValidateur() {
        return validateur;
    }

    public void setValidateur(User validateur) {
        this.validateur = validateur;
    }

    public String getCommentaireValidation() {
        return commentaireValidation;
    }

    public void setCommentaireValidation(String commentaireValidation) {
        this.commentaireValidation = commentaireValidation;
    }

    public java.time.LocalDateTime getDateValidation() {
        return dateValidation;
    }

    public void setDateValidation(java.time.LocalDateTime dateValidation) {
        this.dateValidation = dateValidation;
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
