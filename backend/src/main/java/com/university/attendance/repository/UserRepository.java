package com.university.attendance.repository;

import com.university.attendance.model.Role;
import com.university.attendance.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité User
 * Gère l'accès aux données des utilisateurs
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Trouve un utilisateur par son email
     */
    Optional<User> findByEmail(String email);

    /**
     * Trouve un utilisateur par son numéro étudiant
     */
    Optional<User> findByNumeroEtudiant(String numeroEtudiant);

    /**
     * Trouve un utilisateur par son numéro enseignant
     */
    Optional<User> findByNumeroEnseignant(String numeroEnseignant);

    /**
     * Trouve tous les utilisateurs par rôle
     */
    List<User> findByRole(Role role);

    /**
     * Trouve tous les utilisateurs actifs
     */
    List<User> findByActif(Boolean actif);

    /**
     * Trouve tous les utilisateurs actifs par rôle
     */
    List<User> findByRoleAndActif(Role role, Boolean actif);

    /**
     * Vérifie si un email existe déjà
     */
    boolean existsByEmail(String email);

    /**
     * Vérifie si un numéro étudiant existe déjà
     */
    boolean existsByNumeroEtudiant(String numeroEtudiant);

    /**
     * Trouve tous les étudiants d'une formation
     */
    List<User> findByFormationIdAndRole(Long formationId, Role role);

    /**
     * Trouve tous les enseignants d'un département
     */
    List<User> findByDepartementIdAndRole(Long departementId, Role role);
}
