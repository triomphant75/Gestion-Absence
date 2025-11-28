package com.university.attendance.dto;

/**
 * DTO pour les statistiques d'un étudiant
 */
public class StatistiquesEtudiantDTO {

    private Long etudiantId;
    private String nomComplet;
    private Long totalSeances;
    private Long totalPresences;
    private Long totalAbsences;
    private Long totalRetards;
    private Double tauxAbsence;
    private Long nombreAvertissements;

    // Constructeurs
    public StatistiquesEtudiantDTO() {
    }

    public StatistiquesEtudiantDTO(Long etudiantId, String nomComplet, Long totalSeances,
                                   Long totalPresences, Long totalAbsences, Long totalRetards,
                                   Double tauxAbsence, Long nombreAvertissements) {
        this.etudiantId = etudiantId;
        this.nomComplet = nomComplet;
        this.totalSeances = totalSeances;
        this.totalPresences = totalPresences;
        this.totalAbsences = totalAbsences;
        this.totalRetards = totalRetards;
        this.tauxAbsence = tauxAbsence;
        this.nombreAvertissements = nombreAvertissements;
    }

    // Getters et Setters
    public Long getEtudiantId() {
        return etudiantId;
    }

    public void setEtudiantId(Long etudiantId) {
        this.etudiantId = etudiantId;
    }

    public String getNomComplet() {
        return nomComplet;
    }

    public void setNomComplet(String nomComplet) {
        this.nomComplet = nomComplet;
    }

    public Long getTotalSeances() {
        return totalSeances;
    }

    public void setTotalSeances(Long totalSeances) {
        this.totalSeances = totalSeances;
    }

    public Long getTotalPresences() {
        return totalPresences;
    }

    public void setTotalPresences(Long totalPresences) {
        this.totalPresences = totalPresences;
    }

    public Long getTotalAbsences() {
        return totalAbsences;
    }

    public void setTotalAbsences(Long totalAbsences) {
        this.totalAbsences = totalAbsences;
    }

    public Long getTotalRetards() {
        return totalRetards;
    }

    public void setTotalRetards(Long totalRetards) {
        this.totalRetards = totalRetards;
    }

    public Double getTauxAbsence() {
        return tauxAbsence;
    }

    public void setTauxAbsence(Double tauxAbsence) {
        this.tauxAbsence = tauxAbsence;
    }

    public Long getNombreAvertissements() {
        return nombreAvertissements;
    }

    public void setNombreAvertissements(Long nombreAvertissements) {
        this.nombreAvertissements = nombreAvertissements;
    }
}
