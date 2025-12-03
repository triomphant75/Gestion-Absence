package com.university.attendance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entité représentant une matière enseignée
 */
@Entity
@Table(name = "matieres")
public class Matiere {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la matière est obligatoire")
    @Column(nullable = false)
    private String nom;

    @Column(length = 10)
    private String code;  // Code de la matière (ex: INF101)

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "formation_id", nullable = false)
    private Formation formation;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "enseignant_id")
    @JsonIgnoreProperties({"motDePasse", "formation", "departement"})
    private User enseignant;

    // Type de séance: CM (Cours Magistral) ou TD_TP (Travaux Dirigés/Pratiques)
    @Column(nullable = false, length = 10)
    private String typeSeance = "CM";

    // Coefficient de la matière
    @Column(nullable = false)
    private Double coefficient = 1.0;

    // Nombre total d'heures de cours
    @Column(nullable = false)
    private Integer heuresTotal = 0;

    // Seuil d'absences autorisées avant avertissement
    @Column(nullable = false)
    private Integer seuilAbsences = 3;

    @Column(nullable = false)
    private Boolean actif = true;

    // Constructeurs
    public Matiere() {
    }

    public Matiere(String nom, String code, Formation formation) {
        this.nom = nom;
        this.code = code;
        this.formation = formation;
        this.seuilAbsences = 3;
        this.actif = true;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public String getTypeSeance() {
        return typeSeance;
    }

    public void setTypeSeance(String typeSeance) {
        this.typeSeance = typeSeance;
    }

    public Double getCoefficient() {
        return coefficient;
    }

    public void setCoefficient(Double coefficient) {
        this.coefficient = coefficient;
    }

    public Integer getHeuresTotal() {
        return heuresTotal;
    }

    public void setHeuresTotal(Integer heuresTotal) {
        this.heuresTotal = heuresTotal;
    }

    public Integer getSeuilAbsences() {
        return seuilAbsences;
    }

    public void setSeuilAbsences(Integer seuilAbsences) {
        this.seuilAbsences = seuilAbsences;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public User getEnseignant() {
        return enseignant;
    }

    public void setEnseignant(User enseignant) {
        this.enseignant = enseignant;
    }
}
