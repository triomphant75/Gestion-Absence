package com.university.attendance.repository;

import com.university.attendance.model.Departement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository pour l'entité Departement
 */
@Repository
public interface DepartementRepository extends JpaRepository<Departement, Long> {

    /**
     * Trouve un département par son nom
     */
    Optional<Departement> findByNom(String nom);

    /**
     * Trouve tous les départements actifs
     */
    List<Departement> findByActif(Boolean actif);

    /**
     * Vérifie si un département avec ce nom existe déjà
     */
    boolean existsByNom(String nom);
}
