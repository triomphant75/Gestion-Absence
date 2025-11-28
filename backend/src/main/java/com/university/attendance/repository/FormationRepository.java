package com.university.attendance.repository;

import com.university.attendance.model.Formation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité Formation
 */
@Repository
public interface FormationRepository extends JpaRepository<Formation, Long> {

    /**
     * Trouve toutes les formations d'un département
     */
    List<Formation> findByDepartementId(Long departementId);

    /**
     * Trouve toutes les formations actives
     */
    List<Formation> findByActif(Boolean actif);

    /**
     * Trouve toutes les formations d'un département actives
     */
    List<Formation> findByDepartementIdAndActif(Long departementId, Boolean actif);

    /**
     * Trouve toutes les formations par niveau
     */
    List<Formation> findByNiveau(Integer niveau);
}
