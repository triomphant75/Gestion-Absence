package com.university.attendance.service;

import com.university.attendance.model.*;
import com.university.attendance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour les fonctionnalités spécifiques au Chef de Département
 */
@Service
public class ChefDepartementService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private JustificatifRepository justificatifRepository;

    /**
     * Récupère tous les étudiants du département du Chef de Département
     */
    public List<Map<String, Object>> getEtudiantsByDepartement(Long chefDepartementId) {
        User chefDepartement = userRepository.findById(chefDepartementId)
            .orElseThrow(() -> new RuntimeException("Chef de département non trouvé"));

        if (chefDepartement.getDepartement() == null) {
            throw new RuntimeException("Le chef de département n'est rattaché à aucun département");
        }

        Long departementId = chefDepartement.getDepartement().getId();

        // Récupérer toutes les formations du département
        List<Formation> formations = formationRepository.findByDepartementId(departementId);

        // Récupérer tous les étudiants de ces formations
        List<User> etudiants = new ArrayList<>();
        for (Formation formation : formations) {
            etudiants.addAll(userRepository.findByFormationIdAndRoleAndActif(
                formation.getId(), Role.ETUDIANT, true));
        }

        // Construire la réponse avec les statistiques
        return etudiants.stream()
            .map(this::buildEtudiantSummary)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les étudiants filtrés par formation
     */
    public List<Map<String, Object>> getEtudiantsByFormation(Long chefDepartementId, Long formationId) {
        User chefDepartement = userRepository.findById(chefDepartementId)
            .orElseThrow(() -> new RuntimeException("Chef de département non trouvé"));

        Formation formation = formationRepository.findById(formationId)
            .orElseThrow(() -> new RuntimeException("Formation non trouvée"));

        // Vérifier que la formation appartient au département du chef
        if (!formation.getDepartement().getId().equals(chefDepartement.getDepartement().getId())) {
            throw new RuntimeException("Cette formation n'appartient pas à votre département");
        }

        List<User> etudiants = userRepository.findByFormationIdAndRoleAndActif(
            formationId, Role.ETUDIANT, true);

        return etudiants.stream()
            .map(this::buildEtudiantSummary)
            .collect(Collectors.toList());
    }

    /**
     * Récupère les informations détaillées d'un étudiant
     */
    public Map<String, Object> getEtudiantDetails(Long chefDepartementId, Long etudiantId) {
        User chefDepartement = userRepository.findById(chefDepartementId)
            .orElseThrow(() -> new RuntimeException("Chef de département non trouvé"));

        User etudiant = userRepository.findById(etudiantId)
            .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Vérifier que l'étudiant appartient au département du chef
        if (etudiant.getFormation() == null ||
            !etudiant.getFormation().getDepartement().getId().equals(chefDepartement.getDepartement().getId())) {
            throw new RuntimeException("Cet étudiant n'appartient pas à votre département");
        }

        return buildEtudiantDetailsMap(etudiant);
    }

    /**
     * Construit un résumé de l'étudiant avec statistiques
     */
    private Map<String, Object> buildEtudiantSummary(User etudiant) {
        Map<String, Object> summary = new HashMap<>();

        // Informations de base
        summary.put("id", etudiant.getId());
        summary.put("nom", etudiant.getNom());
        summary.put("prenom", etudiant.getPrenom());
        summary.put("email", etudiant.getEmail());
        summary.put("telephone", etudiant.getTelephone());
        summary.put("numeroEtudiant", etudiant.getNumeroEtudiant());

        // Formation
        if (etudiant.getFormation() != null) {
            Map<String, Object> formationMap = new HashMap<>();
            formationMap.put("id", etudiant.getFormation().getId());
            formationMap.put("nom", etudiant.getFormation().getNom());
            formationMap.put("niveau", etudiant.getFormation().getNiveau());
            summary.put("formation", formationMap);
        }

        // Statistiques de présence
        List<Presence> presences = presenceRepository.findByEtudiantId(etudiant.getId());
        long totalSeances = presences.size();
        long absences = presences.stream()
            .filter(p -> p.getStatut() == StatutPresence.ABSENT)
            .count();
        long retards = presences.stream()
            .filter(p -> p.getStatut() == StatutPresence.RETARD)
            .count();

        // Compter absences justifiées vs non justifiées
        List<Presence> absencesList = presences.stream()
            .filter(p -> p.getStatut() == StatutPresence.ABSENT)
            .collect(Collectors.toList());

        long absencesJustifiees = absencesList.stream()
            .filter(absence -> {
                Optional<Justificatif> justif = justificatifRepository.findByAbsenceId(absence.getId());
                return justif.isPresent() && justif.get().getStatut() == StatutJustificatif.ACCEPTE;
            })
            .count();

        long absencesNonJustifiees = absences - absencesJustifiees;

        // Taux de présence
        double tauxPresence = totalSeances > 0
            ? ((totalSeances - absences) * 100.0 / totalSeances)
            : 100.0;

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalSeances", totalSeances);
        stats.put("absences", absences);
        stats.put("absencesJustifiees", absencesJustifiees);
        stats.put("absencesNonJustifiees", absencesNonJustifiees);
        stats.put("retards", retards);
        stats.put("tauxPresence", Math.round(tauxPresence * 10) / 10.0);

        summary.put("statistiques", stats);

        return summary;
    }

    /**
     * Construit les informations détaillées d'un étudiant
     */
    private Map<String, Object> buildEtudiantDetailsMap(User etudiant) {
        Map<String, Object> details = new HashMap<>();

        // Informations personnelles
        details.put("id", etudiant.getId());
        details.put("nom", etudiant.getNom());
        details.put("prenom", etudiant.getPrenom());
        details.put("email", etudiant.getEmail());
        details.put("telephone", etudiant.getTelephone());
        details.put("numeroEtudiant", etudiant.getNumeroEtudiant());

        // Formation
        if (etudiant.getFormation() != null) {
            Map<String, Object> formationMap = new HashMap<>();
            formationMap.put("id", etudiant.getFormation().getId());
            formationMap.put("nom", etudiant.getFormation().getNom());
            formationMap.put("niveau", etudiant.getFormation().getNiveau());
            formationMap.put("description", etudiant.getFormation().getDescription());
            details.put("formation", formationMap);
        }

        // Toutes les présences
        List<Presence> presences = presenceRepository.findByEtudiantId(etudiant.getId());

        // Statistiques globales
        long totalSeances = presences.size();
        long presents = presences.stream()
            .filter(p -> p.getStatut() == StatutPresence.PRESENT)
            .count();
        long absences = presences.stream()
            .filter(p -> p.getStatut() == StatutPresence.ABSENT)
            .count();
        long retards = presences.stream()
            .filter(p -> p.getStatut() == StatutPresence.RETARD)
            .count();

        double tauxPresence = totalSeances > 0
            ? (presents * 100.0 / totalSeances)
            : 100.0;

        Map<String, Object> statsGlobales = new HashMap<>();
        statsGlobales.put("totalSeances", totalSeances);
        statsGlobales.put("presents", presents);
        statsGlobales.put("absences", absences);
        statsGlobales.put("retards", retards);
        statsGlobales.put("tauxPresence", Math.round(tauxPresence * 10) / 10.0);
        details.put("statistiquesGlobales", statsGlobales);

        // Historique des présences (limité aux 50 dernières)
        List<Map<String, Object>> historique = presences.stream()
            .sorted((p1, p2) -> {
                if (p2.getSeance() == null || p1.getSeance() == null ||
                    p2.getSeance().getDateDebut() == null || p1.getSeance().getDateDebut() == null) {
                    return 0;
                }
                return p2.getSeance().getDateDebut().compareTo(p1.getSeance().getDateDebut());
            })
            .limit(50)
            .map(this::buildPresenceMap)
            .collect(Collectors.toList());
        details.put("historiquePresences", historique);

        // Justificatifs
        List<Justificatif> justificatifs = presences.stream()
            .filter(p -> p.getStatut() == StatutPresence.ABSENT)
            .map(absence -> justificatifRepository.findByAbsenceId(absence.getId()))
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

        List<Map<String, Object>> justifMaps = justificatifs.stream()
            .map(this::buildJustificatifMap)
            .collect(Collectors.toList());
        details.put("justificatifs", justifMaps);

        return details;
    }

    /**
     * Construit un map pour une présence
     */
    private Map<String, Object> buildPresenceMap(Presence presence) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", presence.getId());
        map.put("statut", presence.getStatut());
        map.put("heureValidation", presence.getHeureValidation());

        if (presence.getSeance() != null) {
            Map<String, Object> seanceMap = new HashMap<>();
            seanceMap.put("id", presence.getSeance().getId());
            seanceMap.put("dateDebut", presence.getSeance().getDateDebut());
            seanceMap.put("dateFin", presence.getSeance().getDateFin());
            seanceMap.put("type", presence.getSeance().getTypeSeance());

            if (presence.getSeance().getMatiere() != null) {
                Map<String, Object> matiereMap = new HashMap<>();
                matiereMap.put("id", presence.getSeance().getMatiere().getId());
                matiereMap.put("nom", presence.getSeance().getMatiere().getNom());
                matiereMap.put("code", presence.getSeance().getMatiere().getCode());
                seanceMap.put("matiere", matiereMap);
            }

            map.put("seance", seanceMap);
        }

        // Vérifier s'il y a un justificatif
        if (presence.getStatut() == StatutPresence.ABSENT) {
            Optional<Justificatif> justif = justificatifRepository.findByAbsenceId(presence.getId());
            map.put("justifie", justif.isPresent() && justif.get().getStatut() == StatutJustificatif.ACCEPTE);
        }

        return map;
    }

    /**
     * Construit un map pour un justificatif
     */
    private Map<String, Object> buildJustificatifMap(Justificatif justificatif) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", justificatif.getId());
        map.put("motif", justificatif.getMotif());
        map.put("statut", justificatif.getStatut());
        map.put("createdAt", justificatif.getCreatedAt());
        map.put("fichierPath", justificatif.getFichierPath());
        map.put("commentaireValidation", justificatif.getCommentaireValidation());

        if (justificatif.getAbsence() != null && justificatif.getAbsence().getSeance() != null) {
            Map<String, Object> seanceMap = new HashMap<>();
            seanceMap.put("dateDebut", justificatif.getAbsence().getSeance().getDateDebut());
            seanceMap.put("dateFin", justificatif.getAbsence().getSeance().getDateFin());

            if (justificatif.getAbsence().getSeance().getMatiere() != null) {
                seanceMap.put("matiere", justificatif.getAbsence().getSeance().getMatiere().getNom());
            }

            map.put("seance", seanceMap);
        }

        return map;
    }
}
