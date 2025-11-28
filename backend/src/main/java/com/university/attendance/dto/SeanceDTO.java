package com.university.attendance.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * DTO pour créer ou modifier une séance
 */
public class SeanceDTO {

    @NotNull(message = "L'ID de la matière est obligatoire")
    private Long matiereId;

    @NotNull(message = "L'ID de l'enseignant est obligatoire")
    private Long enseignantId;

    // Null pour CM (toute la promotion), requis pour TD/TP
    private Long groupeId;

    @NotNull(message = "La date de début est obligatoire")
    private LocalDateTime dateDebut;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDateTime dateFin;

    private String salle;
    private String commentaire;

    // Constructeurs
    public SeanceDTO() {
    }

    // Getters et Setters
    public Long getMatiereId() {
        return matiereId;
    }

    public void setMatiereId(Long matiereId) {
        this.matiereId = matiereId;
    }

    public Long getEnseignantId() {
        return enseignantId;
    }

    public void setEnseignantId(Long enseignantId) {
        this.enseignantId = enseignantId;
    }

    public Long getGroupeId() {
        return groupeId;
    }

    public void setGroupeId(Long groupeId) {
        this.groupeId = groupeId;
    }

    public LocalDateTime getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDateTime dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public String getSalle() {
        return salle;
    }

    public void setSalle(String salle) {
        this.salle = salle;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }
}
