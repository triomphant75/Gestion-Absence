package com.university.attendance.dto;

/**
 * DTO pour créer/mettre à jour une matière
 */
public class MatiereDTO {
    private String nom;
    private String code;
    private String description;
    private Long formationId;
    private String typeSeance;
    private Double coefficient;
    private Integer heuresTotal;
    private Integer seuilAbsences;
    private Boolean actif;
    private Long enseignantId;

    // Constructeurs
    public MatiereDTO() {
    }

    public MatiereDTO(String nom, String code, Long formationId, String typeSeance) {
        this.nom = nom;
        this.code = code;
        this.formationId = formationId;
        this.typeSeance = typeSeance;
        this.actif = true;
    }

    // Getters et Setters
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

    public Long getFormationId() {
        return formationId;
    }

    public void setFormationId(Long formationId) {
        this.formationId = formationId;
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

    public Long getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
    }
}
