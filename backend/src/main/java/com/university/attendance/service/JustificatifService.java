package com.university.attendance.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.university.attendance.model.Justificatif;
import com.university.attendance.model.Presence;
import com.university.attendance.model.StatutJustificatif;
import com.university.attendance.model.User;
import com.university.attendance.repository.JustificatifRepository;
import com.university.attendance.repository.PresenceRepository;
import com.university.attendance.repository.UserRepository;

/**
 * Service pour gérer les justificatifs d'absence
 */
@Service
@Transactional
public class JustificatifService {

    @Autowired
    private JustificatifRepository justificatifRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private UserRepository userRepository;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Dépose un justificatif d'absence
     */
    public Justificatif deposerJustificatif(Long etudiantId, Long absenceId, String motif, MultipartFile fichier) throws IOException {
        // Vérifie que l'étudiant existe
        User etudiant = userRepository.findById(etudiantId)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé"));

        // Vérifie que l'absence existe
        Presence absence = presenceRepository.findById(absenceId)
                .orElseThrow(() -> new RuntimeException("Absence non trouvée"));

        // Vérifie que c'est bien l'absence de cet étudiant
        if (!absence.getEtudiant().getId().equals(etudiantId)) {
            throw new RuntimeException("Cette absence n'appartient pas à cet étudiant");
        }

        // Vérifie qu'aucun justificatif n'existe déjà pour cette absence
        if (justificatifRepository.existsByAbsenceId(absenceId)) {
            throw new RuntimeException("Un justificatif existe déjà pour cette absence");
        }

        // Sauvegarde le fichier
        String fichierPath = saveFile(fichier);

        // Crée le justificatif
        Justificatif justificatif = new Justificatif();
        justificatif.setEtudiant(etudiant);
        justificatif.setAbsence(absence);
        justificatif.setMotif(motif);
        justificatif.setFichierPath(fichierPath);
        justificatif.setStatut(StatutJustificatif.EN_ATTENTE);

        return justificatifRepository.save(justificatif);
    }

    /**
     * Valide un justificatif
     */
    public Justificatif validerJustificatif(Long justificatifId, Long validateurId, String commentaire) {
        Justificatif justificatif = justificatifRepository.findById(justificatifId)
                .orElseThrow(() -> new RuntimeException("Justificatif non trouvé"));

        User validateur = userRepository.findById(validateurId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));

        justificatif.setStatut(StatutJustificatif.ACCEPTE);
        justificatif.setValidateur(validateur);
        justificatif.setCommentaireValidation(commentaire);
        justificatif.setDateValidation(LocalDateTime.now());

        return justificatifRepository.save(justificatif);
    }

    /**
     * Refuse un justificatif
     */
    public Justificatif refuserJustificatif(Long justificatifId, Long validateurId, String commentaire) {
        Justificatif justificatif = justificatifRepository.findById(justificatifId)
                .orElseThrow(() -> new RuntimeException("Justificatif non trouvé"));

        User validateur = userRepository.findById(validateurId)
                .orElseThrow(() -> new RuntimeException("Validateur non trouvé"));

        justificatif.setStatut(StatutJustificatif.REFUSE);
        justificatif.setValidateur(validateur);
        justificatif.setCommentaireValidation(commentaire);
        justificatif.setDateValidation(LocalDateTime.now());

        return justificatifRepository.save(justificatif);
    }

    /**
     * Sauvegarde un fichier uploadé
     */
    private String saveFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new RuntimeException("Le fichier est vide");
        }

        // Crée le répertoire si nécessaire
        Path uploadDir = Paths.get(uploadPath);
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Génère un nom de fichier unique
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + extension;

        // Sauvegarde le fichier
        Path filePath = uploadDir.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return uniqueFilename;
    }

    /**
     * Obtient un justificatif par son ID
     */
    public Optional<Justificatif> getJustificatifById(Long id) {
        return justificatifRepository.findById(id);
    }

    /**
     * Obtient tous les justificatifs d'un étudiant
     */
    public List<Justificatif> getJustificatifsByEtudiant(Long etudiantId) {
        return justificatifRepository.findByEtudiantId(etudiantId);
    }

    /**
     * Obtient tous les justificatifs en attente
     */
    public List<Justificatif> getJustificatifsEnAttente() {
        return justificatifRepository.findByStatutOrderByCreatedAtAsc(StatutJustificatif.EN_ATTENTE);
    }

    /**
     * Obtient tous les justificatifs
     */
    public List<Justificatif> getAllJustificatifs() {
        return justificatifRepository.findAll();
    }

    /**
     * Obtient tous les justificatifs traités par un validateur (chef)
     */
    public List<Justificatif> getJustificatifsTraitesParValidateur(Long validateurId) {
        return justificatifRepository.findByValidateurIdOrderByDateValidationDesc(validateurId);
    }

    /**
     * Supprime un justificatif
     */
    public void deleteJustificatif(Long id) {
        justificatifRepository.deleteById(id);
    }
}
