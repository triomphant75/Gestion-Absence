package com.university.attendance.model;

import jakarta.persistence.*;

/**
 * Entité représentant un avertissement académique pour absences excessives
 */
@Entity
@Table(name = "avertissements")
public class Avertissement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "etudiant_id", nullable = false)
    private User etudiant;

    @ManyToOne
    @JoinColumn(name = "matiere_id", nullable = false)
    private Matiere matiere;

    // Nombre d'absences au moment de l'avertissement
    @Column(nullable = false)
    private Integer nombreAbsences;

    @Column(length = 1000)
    private String motif;

    // Indique si l'avertissement a été généré automatiquement
    @Column(nullable = false)
    private Boolean automatique = true;

    // Personne qui a créé l'avertissement (null si automatique)
    @ManyToOne
    @JoinColumn(name = "createur_id")
    private User createur;

    @Column(name = "date_avertissement", nullable = false)
    private java.time.LocalDateTime dateAvertissement;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    // Constructeurs
    public Avertissement() {
    }

    public Avertissement(User etudiant, Matiere matiere, Integer nombreAbsences, Boolean automatique) {
        this.etudiant = etudiant;
        this.matiere = matiere;
        this.nombreAbsences = nombreAbsences;
        this.automatique = automatique;
        this.dateAvertissement = java.time.LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        if (dateAvertissement == null) {
            dateAvertissement = java.time.LocalDateTime.now();
        }
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

    public Matiere getMatiere() {
        return matiere;
    }

    public void setMatiere(Matiere matiere) {
        this.matiere = matiere;
    }

    public Integer getNombreAbsences() {
        return nombreAbsences;
    }

    public void setNombreAbsences(Integer nombreAbsences) {
        this.nombreAbsences = nombreAbsences;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public Boolean getAutomatique() {
        return automatique;
    }

    public void setAutomatique(Boolean automatique) {
        this.automatique = automatique;
    }

    public User getCreateur() {
        return createur;
    }

    public void setCreateur(User createur) {
        this.createur = createur;
    }

    public java.time.LocalDateTime getDateAvertissement() {
        return dateAvertissement;
    }

    public void setDateAvertissement(java.time.LocalDateTime dateAvertissement) {
        this.dateAvertissement = dateAvertissement;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
