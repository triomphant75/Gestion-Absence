package com.university.attendance.service;

import com.university.attendance.model.Departement;
import com.university.attendance.model.Role;
import com.university.attendance.model.User;
import com.university.attendance.repository.DepartementRepository;
import com.university.attendance.repository.UserRepository;
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

    @Autowired
    private UserRepository userRepository;

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

    /**
     * Affecte un chef à un département
     */
    public void affecterChef(Long departementId, Long chefId) {
        // Vérifier que le département existe
        Departement departement = departementRepository.findById(departementId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'id : " + departementId));

        // Vérifier que l'utilisateur existe et est un chef de département
        User chef = userRepository.findById(chefId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + chefId));

        if (chef.getRole() != Role.CHEF_DEPARTEMENT) {
            throw new RuntimeException("L'utilisateur sélectionné n'est pas un chef de département");
        }

        // Vérifier que le chef n'est pas déjà affecté à un autre département
        if (chef.getDepartement() != null && !chef.getDepartement().getId().equals(departementId)) {
            throw new RuntimeException("Ce chef de département est déjà affecté au département : " + chef.getDepartement().getNom());
        }

        // Affecter le département au chef
        chef.setDepartement(departement);
        userRepository.save(chef);
    }

    /**
     * Retire le chef d'un département
     */
    public void retirerChef(Long departementId) {
        // Vérifier que le département existe
        departementRepository.findById(departementId)
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'id : " + departementId));

        // Trouver le chef actuel du département
        List<User> chefs = userRepository.findByDepartementIdAndRole(departementId, Role.CHEF_DEPARTEMENT);

        if (chefs.isEmpty()) {
            throw new RuntimeException("Aucun chef affecté à ce département");
        }

        // Retirer l'affectation
        for (User chef : chefs) {
            chef.setDepartement(null);
            userRepository.save(chef);
        }
    }
}
