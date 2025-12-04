package com.university.attendance.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.university.attendance.model.Justificatif;
import com.university.attendance.model.StatutJustificatif;

/**
 * Repository pour l'entité Justificatif
 */
@Repository
public interface JustificatifRepository extends JpaRepository<Justificatif, Long> {

    /**
     * Trouve tous les justificatifs d'un étudiant
     */
    List<Justificatif> findByEtudiantId(Long etudiantId);

    /**
     * Trouve tous les justificatifs par statut
     */
    List<Justificatif> findByStatut(StatutJustificatif statut);

    /**
     * Trouve tous les justificatifs d'un étudiant par statut
     */
    List<Justificatif> findByEtudiantIdAndStatut(Long etudiantId, StatutJustificatif statut);

    /**
     * Trouve tous les justificatifs en attente
     */
    List<Justificatif> findByStatutOrderByCreatedAtAsc(StatutJustificatif statut);

    /**
     * Trouve tous les justificatifs traités par un validateur (validateur_id)
     */
    List<Justificatif> findByValidateurIdOrderByDateValidationDesc(Long validateurId);

    /**
     * Compte le nombre de justificatifs en attente
     */
    Long countByStatut(StatutJustificatif statut);

    /**
     * Vérifie si une absence a déjà un justificatif
     */
    boolean existsByAbsenceId(Long absenceId);
}
