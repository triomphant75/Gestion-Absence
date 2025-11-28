package com.university.attendance.dto;

/**
 * DTO pour créer/mettre à jour une formation
 */
public class FormationDTO {
    private String nom;
    private String description;
    private Long departementId;
    private Integer niveau;
    private Boolean actif;

    // Constructeurs
    public FormationDTO() {
    }

    public FormationDTO(String nom, Long departementId, Integer niveau) {
        this.nom = nom;
        this.departementId = departementId;
        this.niveau = niveau;
        this.actif = true;
    }

    // Getters et Setters
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

    public Long getDepartementId() {
        return departementId;
    }

    public void setDepartementId(Long departementId) {
        this.departementId = departementId;
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
