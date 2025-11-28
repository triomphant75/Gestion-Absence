package com.university.attendance.repository;

import com.university.attendance.model.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Matiere
 */
@Repository
public interface MatiereRepository extends JpaRepository<Matiere, Long> {

    /**
     * Trouve une matière par son code
     */
    Optional<Matiere> findByCode(String code);

    /**
     * Trouve toutes les matières d'une formation
     */
    List<Matiere> findByFormationId(Long formationId);

    /**
     * Trouve toutes les matières actives
     */
    List<Matiere> findByActif(Boolean actif);

    /**
     * Trouve toutes les matières actives d'une formation
     */
    List<Matiere> findByFormationIdAndActif(Long formationId, Boolean actif);

    /**
     * Vérifie si une matière avec ce code existe déjà
     */
    boolean existsByCode(String code);
}
