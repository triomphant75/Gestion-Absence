package com.university.attendance.service;

import com.university.attendance.dto.GroupeDTO;
import com.university.attendance.model.Formation;
import com.university.attendance.model.Groupe;
import com.university.attendance.repository.FormationRepository;
import com.university.attendance.repository.GroupeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les groupes TD/TP
 */
@Service
@Transactional
public class GroupeService {

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private FormationRepository formationRepository;

    /**
     * Crée un nouveau groupe à partir d'un DTO
     */
    public Groupe createGroupe(GroupeDTO groupeDTO) {
        // Récupère la formation par son ID
        Formation formation = formationRepository.findById(groupeDTO.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + groupeDTO.getFormationId()));

        // Crée l'entité Groupe
        Groupe groupe = new Groupe();
        groupe.setNom(groupeDTO.getNom());
        groupe.setFormation(formation);  // Assigne l'objet Formation
        groupe.setCapaciteMax(groupeDTO.getCapaciteMax() != null ? groupeDTO.getCapaciteMax() : 30);
        groupe.setActif(groupeDTO.getActif() != null ? groupeDTO.getActif() : true);

        return groupeRepository.save(groupe);
    }

    /**
     * Met à jour un groupe à partir d'un DTO
     */
    public Groupe updateGroupe(Long id, GroupeDTO groupeDTO) {
        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé avec l'id : " + id));

        // Récupère la formation par son ID
        Formation formation = formationRepository.findById(groupeDTO.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + groupeDTO.getFormationId()));

        groupe.setNom(groupeDTO.getNom());
        groupe.setFormation(formation);  // Assigne l'objet Formation
        groupe.setCapaciteMax(groupeDTO.getCapaciteMax());
        groupe.setActif(groupeDTO.getActif());

        return groupeRepository.save(groupe);
    }

    /**
     * Trouve un groupe par son ID
     */
    public Optional<Groupe> getGroupeById(Long id) {
        return groupeRepository.findById(id);
    }

    /**
     * Trouve tous les groupes
     */
    public List<Groupe> getAllGroupes() {
        return groupeRepository.findAll();
    }

    /**
     * Trouve tous les groupes actifs
     */
    public List<Groupe> getGroupesActifs() {
        return groupeRepository.findByActif(true);
    }

    /**
     * Trouve tous les groupes d'une formation
     */
    public List<Groupe> getGroupesByFormation(Long formationId) {
        return groupeRepository.findByFormationId(formationId);
    }

    /**
     * Supprime un groupe
     */
    public void deleteGroupe(Long id) {
        groupeRepository.deleteById(id);
    }

    /**
     * Désactive un groupe
     */
    public void deactivateGroupe(Long id) {
        Groupe groupe = groupeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé avec l'id : " + id));
        groupe.setActif(false);
        groupeRepository.save(groupe);
    }
}
