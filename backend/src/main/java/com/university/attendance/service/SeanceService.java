package com.university.attendance.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.university.attendance.dto.EtudiantInscritDTO;
import com.university.attendance.model.GroupeEtudiant;
import com.university.attendance.model.Presence;
import com.university.attendance.model.Role;
import com.university.attendance.model.Seance;
import com.university.attendance.model.StatutPresence;
import com.university.attendance.model.StatutSeance;
import com.university.attendance.model.TypeSeance;
import com.university.attendance.model.User;
import com.university.attendance.repository.GroupeEtudiantRepository;
import com.university.attendance.repository.PresenceRepository;
import com.university.attendance.repository.SeanceRepository;
import com.university.attendance.repository.UserRepository;

/**
 * Service pour gérer les séances de cours
 */
@Service
@Transactional
public class SeanceService {

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private GroupeEtudiantRepository groupeEtudiantRepository;

    @Autowired
    private UserRepository userRepository;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int CODE_VALIDITY_SECONDS = 30;

    /**
     * Crée une nouvelle séance
     */
    public Seance createSeance(Seance seance) {
        return seanceRepository.save(seance);
    }

    /**
     * Met à jour une séance et change son statut en REPORTEE
     */
    public Seance updateSeance(Long id, Seance seanceDetails) {
        Seance seance = seanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée avec l'id : " + id));

        seance.setMatiere(seanceDetails.getMatiere());
        seance.setEnseignant(seanceDetails.getEnseignant());
        seance.setTypeSeance(seanceDetails.getTypeSeance());
        seance.setGroupe(seanceDetails.getGroupe());
        seance.setDateDebut(seanceDetails.getDateDebut());
        seance.setDateFin(seanceDetails.getDateFin());
        seance.setSalle(seanceDetails.getSalle());
        seance.setCommentaire(seanceDetails.getCommentaire());

        // Marquer la séance comme reportée lors de la modification
        seance.setStatut(StatutSeance.REPORTEE);

        return seanceRepository.save(seance);
    }

    /**
     * Lance une séance et génère le code dynamique
     */
    public Seance startSeance(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée avec l'id : " + seanceId));

        if (seance.getTerminee()) {
            throw new RuntimeException("Cette séance est déjà terminée");
        }

        if (seance.getAnnulee()) {
            throw new RuntimeException("Cette séance est annulée");
        }

        // Génère un nouveau code dynamique
        String code = generateDynamicCode();
        seance.setCodeDynamique(code);
        seance.setCodeExpiration(LocalDateTime.now().plusSeconds(CODE_VALIDITY_SECONDS));
        seance.setSeanceActive(true);
        seance.setStatut(StatutSeance.EN_COURS);

        return seanceRepository.save(seance);
    }

    /**
     * Renouvelle le code dynamique d'une séance active
     */
    public Seance renewCode(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée avec l'id : " + seanceId));

        if (!seance.getSeanceActive()) {
            throw new RuntimeException("La séance n'est pas active");
        }

        // Génère un nouveau code
        String newCode = generateDynamicCode();
        seance.setCodeDynamique(newCode);
        seance.setCodeExpiration(LocalDateTime.now().plusSeconds(CODE_VALIDITY_SECONDS));

        return seanceRepository.save(seance);
    }

    /**
     * Arrête une séance et enregistre les absences
     */
    public Seance stopSeance(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée avec l'id : " + seanceId));

        seance.setSeanceActive(false);
        seance.setTerminee(true);
        seance.setCodeDynamique(null);
        seance.setCodeExpiration(null);
        seance.setStatut(StatutSeance.TERMINEE);

        seanceRepository.save(seance);

        // Enregistre les absences pour tous les étudiants qui n'ont pas validé leur présence
        enregistrerAbsences(seance);

        return seance;
    }

    /**
     * Enregistre automatiquement les absences pour les étudiants non présents
     */
    private void enregistrerAbsences(Seance seance) {
        List<User> etudiants;

        // Récupère la liste des étudiants concernés par cette séance
        if (seance.getTypeSeance() == TypeSeance.CM) {
            // Pour un CM : tous les étudiants de la formation
            etudiants = userRepository.findByFormationIdAndRole(
                    seance.getMatiere().getFormation().getId(),
                    Role.ETUDIANT
            );
        } else {
            // Pour un TD/TP : les étudiants du groupe
            List<GroupeEtudiant> groupeEtudiants = groupeEtudiantRepository
                    .findByGroupeId(seance.getGroupe().getId());
            etudiants = groupeEtudiants.stream()
                    .map(GroupeEtudiant::getEtudiant)
                    .toList();
        }

        // Pour chaque étudiant, vérifie s'il a déjà une présence enregistrée
        for (User etudiant : etudiants) {
            boolean presenceExiste = presenceRepository
                    .existsBySeanceIdAndEtudiantId(seance.getId(), etudiant.getId());

            if (!presenceExiste) {
                // Crée une absence automatique
                Presence absence = new Presence();
                absence.setSeance(seance);
                absence.setEtudiant(etudiant);
                absence.setStatut(StatutPresence.ABSENT);
                absence.setModificationManuelle(false);
                presenceRepository.save(absence);
            }
        }
    }

    /**
     * Génère un code alphanumérique aléatoire de 6 caractères
     */
    private String generateDynamicCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    /**
     * Obtient le code dynamique actuel d'une séance
     */
    public String getCurrentCode(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée avec l'id : " + seanceId));

        if (!seance.getSeanceActive()) {
            throw new RuntimeException("La séance n'est pas active");
        }

        if (seance.getCodeExpiration().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Le code a expiré");
        }

        return seance.getCodeDynamique();
    }

    /**
     * Trouve une séance par son ID
     */
    public Optional<Seance> getSeanceById(Long id) {
        return seanceRepository.findById(id);
    }

    /**
     * Trouve toutes les séances
     */
    public List<Seance> getAllSeances() {
        return seanceRepository.findAll();
    }

    /**
     * Trouve les séances d'un enseignant
     */
    public List<Seance> getSeancesByEnseignant(Long enseignantId) {
        return seanceRepository.findByEnseignantIdAndAnnulee(enseignantId, false);
    }

    /**
     * Trouve les séances futures d'un enseignant
     */
    public List<Seance> getUpcomingSeancesByEnseignant(Long enseignantId) {
        return seanceRepository.findUpcomingSeancesByEnseignant(enseignantId, LocalDateTime.now());
    }

    /**
     * Trouve les séances d'un groupe
     */
    public List<Seance> getSeancesByGroupe(Long groupeId) {
        return seanceRepository.findByGroupeId(groupeId);
    }

    /**
     * Annule une séance
     */
    public Seance cancelSeance(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée avec l'id : " + seanceId));

        seance.setAnnulee(true);
        seance.setSeanceActive(false);
        seance.setCodeDynamique(null);
        seance.setCodeExpiration(null);
        seance.setStatut(StatutSeance.ANNULEE);

        return seanceRepository.save(seance);
    }

    /**
     * Supprime une séance
     */
    public void deleteSeance(Long id) {
        seanceRepository.deleteById(id);
    }

    /** Récupère la liste de tous les étudiants inscrits à une séance CM ou TP/TD */
    public List<EtudiantInscritDTO> getEtudiantsInscritsASeance(Long seanceId) {
        Seance seance = seanceRepository.findById(seanceId)
                .orElseThrow(() -> new RuntimeException("Séance non trouvée avec l'id : " + seanceId));

        List<User> etudiants;

        // Si c'est un CM, récupérer tous les étudiants de la formation
        if (seance.getTypeSeance() == TypeSeance.CM) {
            Long formationId = seance.getMatiere().getFormation().getId();
            etudiants = userRepository.findByFormationIdAndRoleAndActif(
                    formationId, Role.ETUDIANT, true);
        } 
        // Si c'est un TD/TP, récupérer les étudiants du groupe
        else if (seance.getGroupe() != null) {
            etudiants = groupeEtudiantRepository.findEtudiantsByGroupeId(
                    seance.getGroupe().getId());
        } 
        else {
            throw new RuntimeException("Séance TD/TP sans groupe assigné");
        }

        // Convertir en DTO avec les informations nécessaires
        return etudiants.stream()
                .map(etudiant -> {
                    EtudiantInscritDTO dto = new EtudiantInscritDTO();
                    dto.setId(etudiant.getId());
                    dto.setNumeroEtudiant(etudiant.getNumeroEtudiant());
                    dto.setNom(etudiant.getNom());
                    dto.setPrenom(etudiant.getPrenom());
                    dto.setEmail(etudiant.getEmail());
                    dto.setTelephone(etudiant.getTelephone());
                    
                    // Ajouter le nom du groupe si c'est un TD/TP
                    if (seance.getGroupe() != null) {
                        dto.setNomGroupe(seance.getGroupe().getNom());
                    }
                    
                    // Ajouter le nom de la formation
                    if (etudiant.getFormation() != null) {
                        dto.setNomFormation(etudiant.getFormation().getNom());
                    }
                    
                    return dto;
                })
                .sorted((e1, e2) -> {
                    // Tri par nom puis prénom
                    int compareNom = e1.getNom().compareToIgnoreCase(e2.getNom());
                    if (compareNom != 0) return compareNom;
                    return e1.getPrenom().compareToIgnoreCase(e2.getPrenom());
                })
                .collect(Collectors.toList());
    }
    
    /**
     * Alias utilisé par le contrôleur et d'autres services — délègue
     * à `getEtudiantsInscritsASeance` pour préserver l'API existante.
     */
    public List<EtudiantInscritDTO> getEtudiantsInscrits(Long seanceId) {
        return getEtudiantsInscritsASeance(seanceId);
    }

    /**
     * Compte le nombre d'étudiants inscrits à une séance
     */
    public int countEtudiantsInscrits(Long seanceId) {
        return getEtudiantsInscrits(seanceId).size();
    }


}
