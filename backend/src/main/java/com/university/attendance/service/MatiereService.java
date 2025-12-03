package com.university.attendance.service;

import com.university.attendance.dto.MatiereDTO;
import com.university.attendance.model.Formation;
import com.university.attendance.model.Matiere;
import com.university.attendance.repository.FormationRepository;
import com.university.attendance.repository.MatiereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import com.university.attendance.repository.UserRepository;
import com.university.attendance.model.User;
import com.university.attendance.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les matières
 */
@Service
@Transactional
public class MatiereService {

    @Autowired
    private MatiereRepository matiereRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Crée une nouvelle matière à partir d'un DTO
     */
    public Matiere createMatiere(MatiereDTO matiereDTO) {
        if (matiereDTO.getCode() != null && matiereRepository.existsByCode(matiereDTO.getCode())) {
            throw new RuntimeException("Une matière avec ce code existe déjà");
        }

        // Récupère la formation par son ID
        Formation formation = formationRepository.findById(matiereDTO.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + matiereDTO.getFormationId()));

        // Crée l'entité Matiere
        Matiere matiere = new Matiere();
        matiere.setNom(matiereDTO.getNom());
        // Assurer que le code est bien défini lors de la création
        if (matiereDTO.getCode() != null) {
            matiere.setCode(matiereDTO.getCode().toUpperCase());
        }
        // L'enseignant est obligatoire pour la création d'une matière
        if (matiereDTO.getEnseignantId() == null) {
            throw new RuntimeException("Un enseignant doit être associé à la matière");
        }
        User enseignant = userRepository.findById(matiereDTO.getEnseignantId())
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé avec l'id : " + matiereDTO.getEnseignantId()));
        if (enseignant.getRole() != Role.ENSEIGNANT) {
            throw new RuntimeException("L'utilisateur fourni n'est pas un enseignant");
        }
        matiere.setEnseignant(enseignant);
        matiere.setDescription(matiereDTO.getDescription());
        matiere.setFormation(formation);  // Assigne l'objet Formation
        matiere.setTypeSeance(matiereDTO.getTypeSeance() != null ? matiereDTO.getTypeSeance() : "CM");
        matiere.setCoefficient(matiereDTO.getCoefficient() != null ? matiereDTO.getCoefficient() : 1.0);
        // Valider heuresTotal (doit être entre 4 et 32)
        Integer heuresTotal = matiereDTO.getHeuresTotal();
        if (heuresTotal == null) {
            throw new RuntimeException("Le nombre d'heures total doit être renseigné (entre 4 et 32)");
        }
        if (heuresTotal < 4 || heuresTotal > 32) {
            throw new RuntimeException("Le nombre d'heures total doit être compris entre 4 et 32");
        }
        matiere.setHeuresTotal(heuresTotal);
        matiere.setSeuilAbsences(matiereDTO.getSeuilAbsences() != null ? matiereDTO.getSeuilAbsences() : 3);
        matiere.setActif(matiereDTO.getActif() != null ? matiereDTO.getActif() : true);

        return matiereRepository.save(matiere);
    }

    /**
     * Met à jour une matière à partir d'un DTO
     */
    public Matiere updateMatiere(Long id, MatiereDTO matiereDTO) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'id : " + id));

        // Récupère la formation par son ID
        Formation formation = formationRepository.findById(matiereDTO.getFormationId())
                .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + matiereDTO.getFormationId()));

        matiere.setNom(matiereDTO.getNom());
        matiere.setCode(matiereDTO.getCode());
        matiere.setDescription(matiereDTO.getDescription());
        matiere.setFormation(formation);  // Assigne l'objet Formation
        matiere.setTypeSeance(matiereDTO.getTypeSeance());
        matiere.setCoefficient(matiereDTO.getCoefficient());
        // Valider heuresTotal (doit être entre 4 et 32)
        Integer heuresTotalUpd = matiereDTO.getHeuresTotal();
        if (heuresTotalUpd == null) {
            throw new RuntimeException("Le nombre d'heures total doit être renseigné (entre 4 et 32)");
        }
        if (heuresTotalUpd < 4 || heuresTotalUpd > 32) {
            throw new RuntimeException("Le nombre d'heures total doit être compris entre 4 et 32");
        }
        matiere.setHeuresTotal(heuresTotalUpd);
        matiere.setSeuilAbsences(matiereDTO.getSeuilAbsences());
        matiere.setActif(matiereDTO.getActif());

        // L'enseignant est obligatoire pour la matière (mise à jour)
        if (matiereDTO.getEnseignantId() == null) {
            throw new RuntimeException("Un enseignant doit être associé à la matière");
        }
        User enseignantUpdate = userRepository.findById(matiereDTO.getEnseignantId())
                .orElseThrow(() -> new RuntimeException("Enseignant non trouvé avec l'id : " + matiereDTO.getEnseignantId()));
        if (enseignantUpdate.getRole() != Role.ENSEIGNANT) {
            throw new RuntimeException("L'utilisateur fourni n'est pas un enseignant");
        }
        matiere.setEnseignant(enseignantUpdate);

        return matiereRepository.save(matiere);
    }

    /**
     * Trouve une matière par son ID
     */
    public Optional<Matiere> getMatiereById(Long id) {
        return matiereRepository.findById(id);
    }

    /**
     * Trouve une matière par son code
     */
    public Optional<Matiere> getMatiereByCode(String code) {
        return matiereRepository.findByCode(code);
    }

    /**
     * Trouve toutes les matières
     */
    public List<Matiere> getAllMatieres() {
        return matiereRepository.findAll();
    }

    /**
     * Trouve toutes les matières actives
     */
    public List<Matiere> getMatieresActives() {
        return matiereRepository.findByActif(true);
    }

    /**
     * Trouve toutes les matières d'une formation
     */
    public List<Matiere> getMatieresByFormation(Long formationId) {
        return matiereRepository.findByFormationId(formationId);
    }

    /**
     * Supprime une matière
     */
    public void deleteMatiere(Long id) {
        matiereRepository.deleteById(id);
    }

    /**
     * Désactive une matière
     */
    public void deactivateMatiere(Long id) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'id : " + id));
        matiere.setActif(false);
        matiereRepository.save(matiere);
    }
}
