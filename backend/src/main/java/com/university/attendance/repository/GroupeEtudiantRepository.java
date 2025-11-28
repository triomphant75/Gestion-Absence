package com.university.attendance.repository;

import com.university.attendance.model.GroupeEtudiant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

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
}
