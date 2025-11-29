package com.university.attendance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.util.ArrayList;
import java.util.List;

/**
 * Entité représentant un groupe TD/TP
 */
@Entity
@Table(name = "groupes")
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom du groupe est obligatoire")
    @Column(nullable = false)
    private String nom;  // Ex: Groupe A, Groupe B, TD1, TP2

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formation_id", nullable = false)
    @JsonIgnoreProperties({"groupes", "matieres", "etudiants"})
    private Formation formation;

    @Column(name = "capacite_max")
    private Integer capaciteMax = 30;  // Capacité maximale du groupe

    @Column(nullable = false)
    private Boolean actif = true;

    // Relation avec les affectations d'étudiants
    @OneToMany(mappedBy = "groupe", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"groupe"})
    private List<GroupeEtudiant> affectations = new ArrayList<>();

    // Constructeurs
    public Groupe() {
    }

    public Groupe(String nom, Formation formation) {
        this.nom = nom;
        this.formation = formation;
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

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public Integer getCapaciteMax() {
        return capaciteMax;
    }

    public void setCapaciteMax(Integer capaciteMax) {
        this.capaciteMax = capaciteMax;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public List<GroupeEtudiant> getAffectations() {
        return affectations;
    }

    public void setAffectations(List<GroupeEtudiant> affectations) {
        this.affectations = affectations;
    }

    /**
     * Retourne le nombre d'étudiants dans le groupe
     * Calculé dynamiquement à partir des affectations
     */
    public Integer getNombreEtudiants() {
        return affectations != null ? affectations.size() : 0;
    }
}
