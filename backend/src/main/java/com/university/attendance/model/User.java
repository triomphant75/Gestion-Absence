package com.university.attendance.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Entité représentant un utilisateur du système
 * Peut être un étudiant, enseignant, chef de département ou administrateur
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Le nom est obligatoire")
    @Column(nullable = false)
    private String nom;

    @NotBlank(message = "Le prénom est obligatoire")
    @Column(nullable = false)
    private String prenom;

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    @Column(nullable = false, unique = true)
    private String email;

    private String telephone;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String motDePasse;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean actif = true;

    // Champs spécifiques aux étudiants
    private String numeroEtudiant;  // Numéro unique de l'étudiant

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "formation_id")
    @JsonIgnoreProperties({"etudiants", "matieres", "groupes"})
    private Formation formation;

    // Champs spécifiques aux enseignants
    private String numeroEnseignant;  // Numéro unique de l'enseignant

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departement_id")
    @JsonIgnoreProperties({"enseignants", "formations"})
    private Departement departement;

    @Column(name = "created_at", updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    // Constructeurs
    public User() {
    }

    public User(String nom, String prenom, String email, String motDePasse, Role role) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.actif = true;
    }

    // Méthodes du cycle de vie
    @PrePersist
    protected void onCreate() {
        createdAt = java.time.LocalDateTime.now();
        updatedAt = java.time.LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = java.time.LocalDateTime.now();
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

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public String getNumeroEtudiant() {
        return numeroEtudiant;
    }

    public void setNumeroEtudiant(String numeroEtudiant) {
        this.numeroEtudiant = numeroEtudiant;
    }

    public Formation getFormation() {
        return formation;
    }

    public void setFormation(Formation formation) {
        this.formation = formation;
    }

    public String getNumeroEnseignant() {
        return numeroEnseignant;
    }

    public void setNumeroEnseignant(String numeroEnseignant) {
        this.numeroEnseignant = numeroEnseignant;
    }

    public Departement getDepartement() {
        return departement;
    }

    public void setDepartement(Departement departement) {
        this.departement = departement;
    }

    public java.time.LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(java.time.LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public java.time.LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(java.time.LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    /**
     * Retourne le nom complet de l'utilisateur
     */
    public String getNomComplet() {
        return prenom + " " + nom;
    }
}
