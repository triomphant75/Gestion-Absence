package com.university.attendance.service;

import com.university.attendance.model.Groupe;
import com.university.attendance.model.GroupeEtudiant;
import com.university.attendance.model.User;
import com.university.attendance.repository.GroupeEtudiantRepository;
import com.university.attendance.repository.GroupeRepository;
import com.university.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer l'affectation des étudiants aux groupes
 */
@Service
@Transactional
public class GroupeEtudiantService {

    @Autowired
    private GroupeEtudiantRepository groupeEtudiantRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupeRepository groupeRepository;

    /**
     * Affecte un étudiant à un groupe
     */
    public GroupeEtudiant affecterEtudiantGroupe(Long etudiantId, Long groupeId) {
        User etudiant = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id : " + etudiantId));

        Groupe groupe = groupeRepository.findById(groupeId)
                .orElseThrow(() -> new RuntimeException("Groupe non trouvé avec l'id : " + groupeId));

        // Vérifie si l'étudiant est déjà affecté à un (autre) groupe
        List<GroupeEtudiant> affectationsExistantes = groupeEtudiantRepository.findByEtudiantId(etudiantId);
        if (!affectationsExistantes.isEmpty()) {
            boolean dejaDansMemeGroupe = affectationsExistantes.stream()
                    .anyMatch(ge -> ge.getGroupe() != null && ge.getGroupe().getId().equals(groupeId));
            if (dejaDansMemeGroupe) {
                throw new RuntimeException("L'étudiant est déjà affecté à ce groupe");
            }
            // S'il est dans un autre groupe, refuser l'affectation pour éviter deux groupes simultanés
            Long autreGroupeId = affectationsExistantes.get(0).getGroupe() != null
                    ? affectationsExistantes.get(0).getGroupe().getId()
                    : null;
            throw new RuntimeException("L'étudiant est déjà affecté à un autre groupe (id=" + autreGroupeId + "). Retirez d'abord l'affectation précédente avant d'en ajouter une nouvelle.");
        }

        GroupeEtudiant groupeEtudiant = new GroupeEtudiant(etudiant, groupe);
        // Vérifie la capacité du groupe
        int currentSize = groupeEtudiantRepository.findByGroupeId(groupeId).size();
        Integer capaciteMax = groupe.getCapaciteMax() != null ? groupe.getCapaciteMax() : 0;
        if (capaciteMax > 0 && currentSize >= capaciteMax) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La capacité maximale du groupe est atteinte");
        }

        return groupeEtudiantRepository.save(groupeEtudiant);
    }

    /**
     * Retire un étudiant d'un groupe
     */
    public void retirerEtudiantGroupe(Long etudiantId, Long groupeId) {
        GroupeEtudiant groupeEtudiant = groupeEtudiantRepository
                .findByEtudiantIdAndGroupeId(etudiantId, groupeId)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée"));

        groupeEtudiantRepository.delete(groupeEtudiant);
    }

    /**
     * Obtient tous les groupes d'un étudiant
     */
    public List<GroupeEtudiant> getGroupesEtudiant(Long etudiantId) {
        return groupeEtudiantRepository.findByEtudiantId(etudiantId);
    }

    /**
     * Obtient tous les étudiants d'un groupe
     */
    public List<GroupeEtudiant> getEtudiantsGroupe(Long groupeId) {
        return groupeEtudiantRepository.findByGroupeId(groupeId);
    }

    /**
     * Obtient une affectation par ID
     */
    public Optional<GroupeEtudiant> getGroupeEtudiantById(Long id) {
        return groupeEtudiantRepository.findById(id);
    }

    /**
     * Obtient toutes les affectations
     */
    public List<GroupeEtudiant> getAllGroupeEtudiants() {
        return groupeEtudiantRepository.findAll();
    }

    /**
     * Supprime toutes les affectations d'un étudiant
     */
    public void supprimerToutesAffectationsEtudiant(Long etudiantId) {
        groupeEtudiantRepository.deleteByEtudiantId(etudiantId);
    }

    /**
     * Supprime toutes les affectations d'un groupe
     */
    public void supprimerToutesAffectationsGroupe(Long groupeId) {
        groupeEtudiantRepository.deleteByGroupeId(groupeId);
    }

    /**
     * Vérifie si un étudiant est dans un groupe
     */
    public boolean isEtudiantInGroupe(Long etudiantId, Long groupeId) {
        return groupeEtudiantRepository.existsByEtudiantIdAndGroupeId(etudiantId, groupeId);
    }
}
