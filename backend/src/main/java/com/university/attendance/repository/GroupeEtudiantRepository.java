package com.university.attendance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.university.attendance.model.GroupeEtudiant;
import com.university.attendance.model.User;

/**
 * Repository pour l'entité GroupeEtudiant
 */
@Repository
public interface GroupeEtudiantRepository extends JpaRepository<GroupeEtudiant, Long> {

    /**
     * Trouve tous les groupes d'un étudiant
     */
    List<GroupeEtudiant> findByEtudiantId(Long etudiantId);

    /**
     * Trouve tous les étudiants d'un groupe
     */
    List<GroupeEtudiant> findByGroupeId(Long groupeId);

    /**
     * Trouve l'affectation d'un étudiant à un groupe
     */
    Optional<GroupeEtudiant> findByEtudiantIdAndGroupeId(Long etudiantId, Long groupeId);

    /**
     * Vérifie si un étudiant est dans un groupe
     */
    boolean existsByEtudiantIdAndGroupeId(Long etudiantId, Long groupeId);

    /**
     * Supprime toutes les affectations d'un étudiant
     */
    void deleteByEtudiantId(Long etudiantId);

    /**
     * Supprime toutes les affectations d'un groupe
     */
    void deleteByGroupeId(Long groupeId);



    /**
     * Trouve tous les étudiants d'un groupe spécifique
     */
    @Query("SELECT ge.etudiant FROM GroupeEtudiant ge WHERE ge.groupe.id = :groupeId")
    List<User> findEtudiantsByGroupeId(@Param("groupeId") Long groupeId);

    /**
     * Trouve tous les étudiants d'une formation (pour les CM)
     */
    @Query("SELECT u FROM User u WHERE u.formation.id = :formationId AND u.role = 'ETUDIANT' AND u.actif = true")
    List<User> findEtudiantsByFormationId(@Param("formationId") Long formationId);
    
}
