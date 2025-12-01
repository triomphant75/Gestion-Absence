package com.university.attendance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * Entité représentant une séance de cours
 */
@Entity
@Table(name = "seances")
public class Seance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "matiere_id", nullable = false)
    @NotNull(message = "La matière est obligatoire")
    private Matiere matiere;

    @ManyToOne
    @JoinColumn(name = "enseignant_id", nullable = false)
    @NotNull(message = "L'enseignant est obligatoire")
    private User enseignant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeSeance typeSeance;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutSeance statut = StatutSeance.PREVUE;

    // Pour les CM : null (toute la promotion)
    // Pour les TD/TP : groupe spécifique
    @ManyToOne
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @Column(nullable = false)
    @NotNull(message = "La date de début est obligatoire")
    private java.time.LocalDateTime dateDebut;

    @Column(nullable = false)
    @NotNull(message = "La date de fin est obligatoire")
    private java.time.LocalDateTime dateFin;

    @Column(length = 100)
    private String salle;  // Salle de cours

    // Code dynamique pour le pointage
    @Column(length = 6)
    private String codeDynamique;

    // Date d'expiration du code dynamique (renouvellement toutes les 30 secondes)
    private java.time.LocalDateTime codeExpiration;

    // Indique si la séance est active (code en cours)
    @Column(nullable = false)
    private Boolean seanceActive = false;

    // Indique si la séance est terminée
    @Column(nullable = false)
    private Boolean terminee = false;

    // Indique si la séance est annulée
    @Column(nullable = false)
    private Boolean annulee = false;

    @Column(length = 500)
    private String commentaire;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    // Constructeurs
    public Seance() {
    }

    public Seance(Matiere matiere, User enseignant, TypeSeance typeSeance,
                  java.time.LocalDateTime dateDebut, java.time.LocalDateTime dateFin) {
        this.matiere = matiere;
        this.enseignant = enseignant;
        this.typeSeance = typeSeance;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
        this.seanceActive = false;
        this.terminee = false;
        this.annulee = false;
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

    public Matiere getMatiere() {
        return matiere;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }

    public User getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(User enseignant) {
        this.enseignant = enseignant;
    }

    public TypeSeance getTypeSeance() {
        return typeSeance;
    }

    public void setTypeSeance(TypeSeance typeSeance) {
        this.typeSeance = typeSeance;
    }

    public StatutSeance getStatut() {
        return statut;
    }

    public void setStatut(StatutSeance statut) {
        this.statut = statut;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public java.time.LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(java.time.LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public java.time.LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(java.time.LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public String getCodeDynamique() {
        return codeDynamique;
    }

    public void setCodeDynamique(String codeDynamique) {
        this.codeDynamique = codeDynamique;
    }

    public java.time.LocalDateTime getCodeExpiration() {
        return codeExpiration;
    }

    public void setCodeExpiration(java.time.LocalDateTime codeExpiration) {
        this.codeExpiration = codeExpiration;
    }

    public Boolean getSeanceActive() {
        return seanceActive;
    }

    public void setSeanceActive(Boolean seanceActive) {
        this.seanceActive = seanceActive;
    }

    public Boolean getTerminee() {
        return terminee;
    }

    public void setTerminee(Boolean terminee) {
        this.terminee = terminee;
    }

    public Boolean getAnnulee() {
        return annulee;
    }

    public void setAnnulee(Boolean annulee) {
        this.annulee = annulee;
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
