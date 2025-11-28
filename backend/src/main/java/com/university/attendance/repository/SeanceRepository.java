package com.university.attendance.repository;

import com.university.attendance.model.Seance;
import com.university.attendance.model.TypeSeance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Seance
 */
@Repository
public interface SeanceRepository extends JpaRepository<Seance, Long> {

    /**
     * Trouve toutes les séances d'une matière
     */
    List<Seance> findByMatiereId(Long matiereId);

    /**
     * Trouve toutes les séances d'un enseignant
     */
    List<Seance> findByEnseignantId(Long enseignantId);

    /**
     * Trouve toutes les séances d'un groupe
     */
    List<Seance> findByGroupeId(Long groupeId);

    /**
     * Trouve toutes les séances actives
     */
    List<Seance> findBySeanceActive(Boolean active);

    /**
     * Trouve toutes les séances terminées
     */
    List<Seance> findByTerminee(Boolean terminee);

    /**
     * Trouve toutes les séances non annulées d'un enseignant
     */
    List<Seance> findByEnseignantIdAndAnnulee(Long enseignantId, Boolean annulee);

    /**
     * Trouve les séances par type
     */
    List<Seance> findByTypeSeance(TypeSeance typeSeance);

    /**
     * Trouve les séances entre deux dates
     */
    List<Seance> findByDateDebutBetween(LocalDateTime debut, LocalDateTime fin);

    /**
     * Trouve les séances d'un enseignant entre deux dates
     */
    List<Seance> findByEnseignantIdAndDateDebutBetween(Long enseignantId, LocalDateTime debut, LocalDateTime fin);

    /**
     * Trouve les séances d'un groupe entre deux dates
     */
    List<Seance> findByGroupeIdAndDateDebutBetween(Long groupeId, LocalDateTime debut, LocalDateTime fin);

    /**
     * Trouve une séance par son code dynamique
     */
    Optional<Seance> findByCodeDynamique(String codeDynamique);

    /**
     * Trouve toutes les séances actives avec code non expiré
     */
    @Query("SELECT s FROM Seance s WHERE s.seanceActive = true AND s.codeExpiration > :now")
    List<Seance> findActiveSeancesWithValidCode(@Param("now") LocalDateTime now);

    /**
     * Trouve les séances futures d'un enseignant
     */
    @Query("SELECT s FROM Seance s WHERE s.enseignant.id = :enseignantId AND s.dateDebut > :now AND s.annulee = false ORDER BY s.dateDebut ASC")
    List<Seance> findUpcomingSeancesByEnseignant(@Param("enseignantId") Long enseignantId, @Param("now") LocalDateTime now);

    /**
     * Trouve les séances en cours (date actuelle entre début et fin)
     */
    @Query("SELECT s FROM Seance s WHERE :now BETWEEN s.dateDebut AND s.dateFin AND s.annulee = false")
    List<Seance> findCurrentSeances(@Param("now") LocalDateTime now);
}
