package com.university.attendance.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.attendance.dto.FormationDTO;
import com.university.attendance.model.Departement;
import com.university.attendance.model.Formation;
import com.university.attendance.repository.DepartementRepository;
import com.university.attendance.repository.FormationRepository;

/**
 * Service pour gérer les formations
 */
@Service
@Transactional
public class FormationService {

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private DepartementRepository departementRepository;

    /**
     * Crée une nouvelle formation à partir d'un DTO
     */
    public Formation createFormation(FormationDTO formationDTO) {
        // Récupère le département par son ID
        Departement departement = departementRepository.findById(formationDTO.getDepartementId())
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'id : " + formationDTO.getDepartementId()));

        // Crée l'entité Formation
        Formation formation = new Formation();
        formation.setNom(formationDTO.getNom());
        formation.setDescription(formationDTO.getDescription());
        formation.setDepartement(departement);  // Assigne l'objet Departement
        formation.setNiveau(formationDTO.getNiveau());
        formation.setActif(formationDTO.getActif() != null ? formationDTO.getActif() : true);

        return formationRepository.save(formation);
    }

    /**
     * Met à jour une formation à partir d'un DTO
     */
    public Formation updateFormation(Long id, FormationDTO formationDTO) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + id));

        // Récupère le département par son ID
        Departement departement = departementRepository.findById(formationDTO.getDepartementId())
                .orElseThrow(() -> new RuntimeException("Département non trouvé avec l'id : " + formationDTO.getDepartementId()));

        formation.setNom(formationDTO.getNom());
        formation.setDescription(formationDTO.getDescription());
        formation.setDepartement(departement);  // Assigne l'objet Departement
        formation.setNiveau(formationDTO.getNiveau());
        formation.setActif(formationDTO.getActif());

        return formationRepository.save(formation);
    }

    /**
     * Trouve une formation par son ID
     */
    public Optional<Formation> getFormationById(Long id) {
        return formationRepository.findById(id);
    }

    /**
     * Trouve toutes les formations
     */
    public List<Formation> getAllFormations() {
        return formationRepository.findAll();
    }

    /**
     * Trouve toutes les formations actives
     */
    public List<Formation> getFormationsActives() {
        return formationRepository.findByActif(true);
    }

    /**
     * Trouve toutes les formations d'un département
     */
    public List<Formation> getFormationsByDepartement(Long departementId) {
        return formationRepository.findByDepartementId(departementId);
    }

    /**
     * Trouve toutes les formations par niveau
     */
    public List<Formation> getFormationsByNiveau(Integer niveau) {
        return formationRepository.findByNiveau(niveau);
    }

    /**
     * Supprime une formation
     */
    public void deleteFormation(Long id) {
        formationRepository.deleteById(id);
    }

    /**
     * Désactive une formation
     */
    public void deactivateFormation(Long id) {
        Formation formation = formationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + id));
        formation.setActif(false);
        formationRepository.save(formation);
    }
}
