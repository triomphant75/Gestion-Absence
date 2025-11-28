package com.university.attendance.repository;

import com.university.attendance.model.Presence;
import com.university.attendance.model.StatutPresence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Presence
 */
@Repository
public interface PresenceRepository extends JpaRepository<Presence, Long> {

    /**
     * Trouve toutes les présences d'une séance
     */
    List<Presence> findBySeanceId(Long seanceId);

    /**
     * Trouve toutes les présences d'un étudiant
     */
    List<Presence> findByEtudiantId(Long etudiantId);

    /**
     * Trouve la présence d'un étudiant pour une séance
     */
    Optional<Presence> findBySeanceIdAndEtudiantId(Long seanceId, Long etudiantId);

    /**
     * Trouve toutes les absences d'un étudiant
     */
    List<Presence> findByEtudiantIdAndStatut(Long etudiantId, StatutPresence statut);

    /**
     * Compte le nombre d'absences d'un étudiant pour une matière
     */
    @Query("SELECT COUNT(p) FROM Presence p WHERE p.etudiant.id = :etudiantId AND p.seance.matiere.id = :matiereId AND p.statut = 'ABSENT'")
    Long countAbsencesByEtudiantAndMatiere(@Param("etudiantId") Long etudiantId, @Param("matiereId") Long matiereId);

    /**
     * Compte le nombre de présences d'un étudiant pour une matière
     */
    @Query("SELECT COUNT(p) FROM Presence p WHERE p.etudiant.id = :etudiantId AND p.seance.matiere.id = :matiereId AND p.statut = 'PRESENT'")
    Long countPresencesByEtudiantAndMatiere(@Param("etudiantId") Long etudiantId, @Param("matiereId") Long matiereId);

    /**
     * Trouve toutes les absences d'un étudiant pour une matière
     */
    @Query("SELECT p FROM Presence p WHERE p.etudiant.id = :etudiantId AND p.seance.matiere.id = :matiereId AND p.statut = 'ABSENT'")
    List<Presence> findAbsencesByEtudiantAndMatiere(@Param("etudiantId") Long etudiantId, @Param("matiereId") Long matiereId);

    /**
     * Calcule le taux d'absence d'un étudiant (en pourcentage)
     */
    @Query("SELECT (COUNT(CASE WHEN p.statut = 'ABSENT' THEN 1 END) * 100.0 / COUNT(p)) FROM Presence p WHERE p.etudiant.id = :etudiantId")
    Double calculateTauxAbsence(@Param("etudiantId") Long etudiantId);

    /**
     * Calcule le taux d'absence d'un étudiant pour une matière
     */
    @Query("SELECT (COUNT(CASE WHEN p.statut = 'ABSENT' THEN 1 END) * 100.0 / COUNT(p)) FROM Presence p WHERE p.etudiant.id = :etudiantId AND p.seance.matiere.id = :matiereId")
    Double calculateTauxAbsenceByMatiere(@Param("etudiantId") Long etudiantId, @Param("matiereId") Long matiereId);

    /**
     * Vérifie si une présence existe déjà pour un étudiant à une séance
     */
    boolean existsBySeanceIdAndEtudiantId(Long seanceId, Long etudiantId);

    /**
     * Trouve toutes les absences non justifiées d'un étudiant
     */
    @Query("SELECT p FROM Presence p WHERE p.etudiant.id = :etudiantId AND p.statut = 'ABSENT' AND NOT EXISTS (SELECT j FROM Justificatif j WHERE j.absence.id = p.id AND j.statut = 'ACCEPTE')")
    List<Presence> findAbsencesNonJustifiees(@Param("etudiantId") Long etudiantId);
}
