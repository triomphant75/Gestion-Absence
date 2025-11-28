package com.university.attendance.service;

import com.university.attendance.model.Avertissement;
import com.university.attendance.model.Matiere;
import com.university.attendance.model.User;
import com.university.attendance.repository.AvertissementRepository;
import com.university.attendance.repository.MatiereRepository;
import com.university.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les avertissements académiques
 */
@Service
@Transactional
public class AvertissementService {

    @Autowired
    private AvertissementRepository avertissementRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MatiereRepository matiereRepository;

    /**
     * Crée un avertissement manuellement
     */
    public Avertissement createAvertissement(Long etudiantId, Long matiereId, Integer nombreAbsences,
                                            String motif, Long createurId) {
        User etudiant = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id : " + etudiantId));

        Matiere matiere = matiereRepository.findById(matiereId)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'id : " + matiereId));

        User createur = userRepository.findById(createurId)
                .orElseThrow(() -> new RuntimeException("Créateur non trouvé avec l'id : " + createurId));

        // Vérifie si un avertissement existe déjà
        if (avertissementRepository.existsByEtudiantIdAndMatiereId(etudiantId, matiereId)) {
            throw new RuntimeException("Un avertissement existe déjà pour cet étudiant dans cette matière");
        }

        Avertissement avertissement = new Avertissement(etudiant, matiere, nombreAbsences, false);
        avertissement.setMotif(motif);
        avertissement.setCreateur(createur);

        return avertissementRepository.save(avertissement);
    }

    /**
     * Obtient un avertissement par son ID
     */
    public Optional<Avertissement> getAvertissementById(Long id) {
        return avertissementRepository.findById(id);
    }

    /**
     * Obtient tous les avertissements
     */
    public List<Avertissement> getAllAvertissements() {
        return avertissementRepository.findAll();
    }

    /**
     * Obtient tous les avertissements d'un étudiant
     */
    public List<Avertissement> getAvertissementsByEtudiant(Long etudiantId) {
        return avertissementRepository.findByEtudiantId(etudiantId);
    }

    /**
     * Obtient tous les avertissements d'une matière
     */
    public List<Avertissement> getAvertissementsByMatiere(Long matiereId) {
        return avertissementRepository.findByMatiereId(matiereId);
    }

    /**
     * Obtient les avertissements d'un étudiant pour une matière
     */
    public List<Avertissement> getAvertissementsByEtudiantAndMatiere(Long etudiantId, Long matiereId) {
        return avertissementRepository.findByEtudiantIdAndMatiereId(etudiantId, matiereId);
    }

    /**
     * Obtient tous les avertissements automatiques
     */
    public List<Avertissement> getAvertissementsAutomatiques() {
        return avertissementRepository.findByAutomatique(true);
    }

    /**
     * Obtient tous les avertissements manuels
     */
    public List<Avertissement> getAvertissementsManuels() {
        return avertissementRepository.findByAutomatique(false);
    }

    /**
     * Compte le nombre d'avertissements d'un étudiant
     */
    public Long countAvertissementsByEtudiant(Long etudiantId) {
        return avertissementRepository.countByEtudiantId(etudiantId);
    }

    /**
     * Supprime un avertissement
     */
    public void deleteAvertissement(Long id) {
        avertissementRepository.deleteById(id);
    }

    /**
     * Met à jour le motif d'un avertissement
     */
    public Avertissement updateMotif(Long id, String nouveauMotif) {
        Avertissement avertissement = avertissementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Avertissement non trouvé avec l'id : " + id));

        avertissement.setMotif(nouveauMotif);
        return avertissementRepository.save(avertissement);
    }
}
