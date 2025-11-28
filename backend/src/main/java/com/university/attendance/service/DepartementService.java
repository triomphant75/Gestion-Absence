package com.university.attendance.service;

import com.university.attendance.model.Departement;
import com.university.attendance.repository.DepartementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les départements
 */
@Service
@Transactional
public class DepartementService {

    @Autowired
    private DepartementRepository departementRepository;

    /**
     * Crée un nouveau département
     */
    public Departement createDepartement(Departement departement) {
        if (departementRepository.existsByNom(departement.getNom())) {
            throw new RuntimeException("Un département avec ce nom existe déjà");
        }
        return departementRepository.save(departement);
    }

    /**
     * Met à jour un département
     */
    public Departement updateDepartement(Long id, Departement departementDetails) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'id : " + id));

        departement.setNom(departementDetails.getNom());
        departement.setDescription(departementDetails.getDescription());
        departement.setActif(departementDetails.getActif());

        return departementRepository.save(departement);
    }

    /**
     * Trouve un département par son ID
     */
    public Optional<Departement> getDepartementById(Long id) {
        return departementRepository.findById(id);
    }

    /**
     * Trouve tous les départements
     */
    public List<Departement> getAllDepartements() {
        return departementRepository.findAll();
    }

    /**
     * Trouve tous les départements actifs
     */
    public List<Departement> getDepartementsActifs() {
        return departementRepository.findByActif(true);
    }

    /**
     * Supprime un département
     */
    public void deleteDepartement(Long id) {
        departementRepository.deleteById(id);
    }

    /**
     * Désactive un département
     */
    public void deactivateDepartement(Long id) {
        Departement departement = departementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'id : " + id));
        departement.setActif(false);
        departementRepository.save(departement);
    }
}
