package com.university.attendance.repository;

import com.university.attendance.model.Groupe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité Groupe
 */
@Repository
public interface GroupeRepository extends JpaRepository<Groupe, Long> {

    /**
     * Trouve tous les groupes d'une formation
     */
    List<Groupe> findByFormationId(Long formationId);

    /**
     * Trouve tous les groupes actifs
     */
    List<Groupe> findByActif(Boolean actif);

    /**
     * Trouve tous les groupes actifs d'une formation
     */
    List<Groupe> findByFormationIdAndActif(Long formationId, Boolean actif);
}
