package com.university.attendance.dto;

/**
 * DTO pour représenter un étudiant inscrit à une séance
 * Contient les informations essentielles de l'étudiant
 */
public class EtudiantInscritDTO {

    private Long id;
    private String numeroEtudiant;
    private String nom;
    private String prenom;
    private String email;
    private String telephone;
    private String nomGroupe; // Nom du groupe TD/TP (si applicable)
    private String nomFormation;
    
    // Constructeurs
    public EtudiantInscritDTO() {
    }

    public EtudiantInscritDTO(Long id, String numeroEtudiant, String nom, String prenom, 
                              String email, String telephone, String nomGroupe, String nomFormation) {
        this.id = id;
        this.numeroEtudiant = numeroEtudiant;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.telephone = telephone;
        this.nomGroupe = nomGroupe;
        this.nomFormation = nomFormation;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNumeroEtudiant() {
        return numeroEtudiant;
    }

    public void setNumeroEtudiant(String numeroEtudiant) {
        this.numeroEtudiant = numeroEtudiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getNomGroupe() {
        return nomGroupe;
    }

    public void setNomGroupe(String nomGroupe) {
        this.nomGroupe = nomGroupe;
    }

    public String getNomFormation() {
        return nomFormation;
    }

    public void setNomFormation(String nomFormation) {
        this.nomFormation = nomFormation;
    }

    /**
     * Retourne le nom complet de l'étudiant
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }
}