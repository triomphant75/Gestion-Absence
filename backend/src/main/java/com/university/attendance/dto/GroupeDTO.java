package com.university.attendance.dto;

/**
 * DTO pour créer/mettre à jour un groupe
 */
public class GroupeDTO {
    private String nom;
    private Long formationId;
    private Integer capaciteMax;
    private Boolean actif;

    // Constructeurs
    public GroupeDTO() {
    }

    public GroupeDTO(String nom, Long formationId) {
        this.nom = nom;
        this.formationId = formationId;
        this.actif = true;
    }

    // Getters et Setters
    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Long getFormationId() {
        return formationId;
    }

    public void setFormationId(Long formationId) {
        this.formationId = formationId;
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
}
