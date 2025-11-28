package com.university.attendance.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

/**
 * Entité représentant une formation universitaire (Licence, Master, etc.)
 */
@Entity
@Table(name = "formations")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom de la formation est obligatoire")
    @Column(nullable = false)
    private String nom;

    @Column(length = 500)
    private String description;

    @ManyToOne
    @JoinColumn(name = "departement_id", nullable = false)
    private Departement departement;

    private Integer niveau; // 1 = L1, 2 = L2, 3 = L3, 4 = M1, 5 = M2

    @Column(nullable = false)
    private Boolean actif = true;

    // Constructeurs
    public Formation() {
    }

    public Formation(String nom, Departement departement, Integer niveau) {
        this.nom = nom;
        this.departement = departement;
        this.niveau = niveau;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }
}
