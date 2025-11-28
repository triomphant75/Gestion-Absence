package com.university.attendance.repository;

import com.university.attendance.model.Avertissement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository pour l'entité Avertissement
 */
@Repository
public interface AvertissementRepository extends JpaRepository<Avertissement, Long> {

    /**
     * Trouve tous les avertissements d'un étudiant
     */
    List<Avertissement> findByEtudiantId(Long etudiantId);

    /**
     * Trouve tous les avertissements d'un étudiant pour une matière
     */
    List<Avertissement> findByEtudiantIdAndMatiereId(Long etudiantId, Long matiereId);

    /**
     * Trouve tous les avertissements d'une matière
     */
    List<Avertissement> findByMatiereId(Long matiereId);

    /**
     * Trouve tous les avertissements automatiques
     */
    List<Avertissement> findByAutomatique(Boolean automatique);

    /**
     * Vérifie si un étudiant a déjà un avertissement pour une matière
     */
    boolean existsByEtudiantIdAndMatiereId(Long etudiantId, Long matiereId);

    /**
     * Compte le nombre d'avertissements d'un étudiant
     */
    Long countByEtudiantId(Long etudiantId);
}
