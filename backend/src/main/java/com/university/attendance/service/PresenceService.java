package com.university.attendance.service;

import com.university.attendance.dto.StatistiquesEtudiantDTO;
import com.university.attendance.model.*;
import com.university.attendance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les présences et absences
 */
@Service
@Transactional
public class PresenceService {

    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupeEtudiantRepository groupeEtudiantRepository;

    @Autowired
    private AvertissementRepository avertissementRepository;

    /**
     * Valide la présence d'un étudiant via le code dynamique
     */
    public Presence validateCode(Long seanceId, Long etudiantId, String code) {
        // Vérifie que la séance existe
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée"));

        // Vérifie que la séance est active
        if (!seance.getSeanceActive()) {
            throw new RuntimeException("La séance n'est pas active");
        }

        // Vérifie que le code n'a pas expiré
        if (seance.getCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le code a expiré");
        }

        // Vérifie que le code est correct
        if (!seance.getCodeDynamique().equals(code)) {
            throw new RuntimeException("Code incorrect");
        }

        // Vérifie que l'étudiant existe
        User etudiant = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Vérifie que l'utilisateur est bien un étudiant
        if (etudiant.getRole() != Role.ETUDIANT) {
            throw new RuntimeException("L'utilisateur n'est pas un étudiant");
        }

        // Vérifie que l'étudiant est inscrit à cette séance
        if (!isEtudiantInscritSeance(etudiant, seance)) {
            throw new RuntimeException("Vous n'êtes pas inscrit à cette séance");
        }

        // Vérifie si l'étudiant n'a pas déjà validé sa présence
        Optional<Presence> existingPresence = presenceRepository
                .findBySeanceIdAndEtudiantId(seanceId, etudiantId);

        if (existingPresence.isPresent()) {
            throw new RuntimeException("Présence déjà enregistrée");
        }

        // Crée la présence
        Presence presence = new Presence();
        presence.setSeance(seance);
        presence.setEtudiant(etudiant);
        presence.setStatut(StatutPresence.PRESENT);
        presence.setHeureValidation(LocalDateTime.now());
        presence.setModificationManuelle(false);

        return presenceRepository.save(presence);
    }

    /**
     * Vérifie si un étudiant est inscrit à une séance
     */
    private boolean isEtudiantInscritSeance(User etudiant, Seance seance) {
        if (seance.getTypeSeance() == TypeSeance.CM) {
            // Pour un CM : vérifier que l'étudiant est dans la formation
            return etudiant.getFormation() != null &&
                   etudiant.getFormation().getId().equals(seance.getMatiere().getFormation().getId());
        } else {
            // Pour un TD/TP : vérifier que l'étudiant est dans le groupe
            return groupeEtudiantRepository.existsByEtudiantIdAndGroupeId(
                    etudiant.getId(),
                    seance.getGroupe().getId()
            );
        }
    }

    /**
     * Modifie manuellement une présence (par l'enseignant)
     */
    public Presence updatePresence(Long presenceId, StatutPresence nouveauStatut, String commentaire) {
        Presence presence = presenceRepository.findById(presenceId)
                .orElseThrow(() -> new RuntimeException("Présence non trouvée"));

        // Interdire la modification si la séance est déjà terminée
        if (presence.getSeance() != null && presence.getSeance().getDateFin() != null) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (now.isAfter(presence.getSeance().getDateFin())) {
                throw new RuntimeException("La séance est terminée — modification non autorisée");
            }
        }

        presence.setStatut(nouveauStatut);
        presence.setCommentaire(commentaire);
        presence.setModificationManuelle(true);

        // Si l'enseignant marque l'étudiant comme présent ou en retard, enregistrer l'heure de validation
        if (nouveauStatut == StatutPresence.PRESENT || nouveauStatut == StatutPresence.RETARD) {
            presence.setHeureValidation(java.time.LocalDateTime.now());
        } else {
            // sinon, effacer l'heure de validation
            presence.setHeureValidation(null);
        }

        Presence updatedPresence = presenceRepository.save(presence);

        // Vérifie si un avertissement doit être généré
        verifierSeuilAbsences(presence.getEtudiant(), presence.getSeance().getMatiere());

        return updatedPresence;
    }

    /**
     * Crée une présence manuellement
     */
    public Presence createPresenceManuelle(Long seanceId, Long etudiantId, StatutPresence statut) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée"));

        // Interdire la création/modification manuelle si la séance est terminée
        if (seance.getDateFin() != null) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            if (now.isAfter(seance.getDateFin())) {
                throw new RuntimeException("La séance est terminée — action non autorisée");
            }
        }

        User etudiant = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Vérifie si une présence existe déjà
        Optional<Presence> existingPresence = presenceRepository
                .findBySeanceIdAndEtudiantId(seanceId, etudiantId);

        if (existingPresence.isPresent()) {
            throw new RuntimeException("Une présence existe déjà pour cet étudiant");
        }

        Presence presence = new Presence();
        presence.setSeance(seance);
        presence.setEtudiant(etudiant);
        presence.setStatut(statut);
        presence.setModificationManuelle(true);

        Presence savedPresence = presenceRepository.save(presence);

        // Vérifie si un avertissement doit être généré
        if (statut == StatutPresence.ABSENT) {
            verifierSeuilAbsences(etudiant, seance.getMatiere());
        }

        return savedPresence;
    }

    /**
     * Vérifie si le seuil d'absences est dépassé et génère un avertissement
     */
    private void verifierSeuilAbsences(User etudiant, Matiere matiere) {
        Long nombreAbsences = presenceRepository.countAbsencesByEtudiantAndMatiere(
                etudiant.getId(),
                matiere.getId()
        );

        // Si le seuil est dépassé et qu'aucun avertissement n'existe déjà
        if (nombreAbsences >= matiere.getSeuilAbsences() &&
            !avertissementRepository.existsByEtudiantIdAndMatiereId(etudiant.getId(), matiere.getId())) {

            Avertissement avertissement = new Avertissement();
            avertissement.setEtudiant(etudiant);
            avertissement.setMatiere(matiere);
            avertissement.setNombreAbsences(nombreAbsences.intValue());
            avertissement.setAutomatique(true);
            avertissement.setMotif("Dépassement du seuil d'absences autorisées (" +
                    matiere.getSeuilAbsences() + " absences)");

            avertissementRepository.save(avertissement);
        }
    }

    /**
     * Obtient toutes les présences d'un étudiant
     */
    public List<Presence> getPresencesByEtudiant(Long etudiantId) {
        return presenceRepository.findByEtudiantId(etudiantId);
    }

    /**
     * Obtient toutes les présences d'une séance
     */
    public List<Presence> getPresencesBySeance(Long seanceId) {
        return presenceRepository.findBySeanceId(seanceId);
    }

    /**
     * Obtient les statistiques d'un étudiant
     */
    public StatistiquesEtudiantDTO getStatistiquesEtudiant(Long etudiantId) {
        User etudiant = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        List<Presence> presences = presenceRepository.findByEtudiantId(etudiantId);

        long totalSeances = presences.size();
        long totalPresences = presences.stream()
                .filter(p -> p.getStatut() == StatutPresence.PRESENT)
                .count();
        long totalAbsences = presences.stream()
                .filter(p -> p.getStatut() == StatutPresence.ABSENT)
                .count();
        long totalRetards = presences.stream()
                .filter(p -> p.getStatut() == StatutPresence.RETARD)
                .count();

        // Avoid division by zero at the DB level: compute taux locally using counts
        Double tauxAbsence;
        if (totalSeances == 0) {
            tauxAbsence = 0.0;
        } else {
            tauxAbsence = (totalAbsences * 100.0) / (double) totalSeances;
        }

        Long nombreAvertissements = avertissementRepository.countByEtudiantId(etudiantId);

        return new StatistiquesEtudiantDTO(
                etudiantId,
                etudiant.getNomComplet(),
                totalSeances,
                totalPresences,
                totalAbsences,
                totalRetards,
                tauxAbsence,
                nombreAvertissements
        );
    }

    /**
     * Obtient le nombre d'absences d'un étudiant pour une matière
     */
    public Long countAbsencesByEtudiantAndMatiere(Long etudiantId, Long matiereId) {
        return presenceRepository.countAbsencesByEtudiantAndMatiere(etudiantId, matiereId);
    }

    /**
     * Supprime une présence
     */
    public void deletePresence(Long id) {
        presenceRepository.deleteById(id);
    }
}
