package com.university.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.attendance.model.Role;
import com.university.attendance.model.User;

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

    /**
     * Trouve tous les étudiants actifs d'une formation
     * Utilisé pour les CM (cours magistraux)
     */
    List<User> findByFormationIdAndRoleAndActif(Long formationId, Role role, Boolean actif);

    /**
     * Trouve tous les étudiants d'une formation qui ne sont pas encore affectés à un groupe
     * Utilisé pour l'affectation des étudiants aux groupes (interface secrétariat)
     */
    @Query("SELECT u FROM User u WHERE u.formation.id = :formationId " +
           "AND u.role = :role " +
           "AND u.actif = true " +
           "AND u.id NOT IN (SELECT ge.etudiant.id FROM GroupeEtudiant ge)")
    List<User> findEtudiantsSansGroupeByFormation(@Param("formationId") Long formationId, @Param("role") Role role);
}
