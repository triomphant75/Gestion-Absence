package com.university.attendance.controller;

import com.university.attendance.model.Justificatif;
import com.university.attendance.service.JustificatifService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les justificatifs d'absence
 */
@RestController
@RequestMapping("/api/justificatifs")
@CrossOrigin(origins = "*")
public class JustificatifController {

    @Autowired
    private JustificatifService justificatifService;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Dépose un justificatif d'absence
     * POST /api/justificatifs
     */
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> deposerJustificatif(
            @RequestParam Long etudiantId,
            @RequestParam Long absenceId,
            @RequestParam String motif,
            @RequestParam("fichier") MultipartFile fichier) {
        try {
            Justificatif justificatif = justificatifService.deposerJustificatif(
                    etudiantId, absenceId, motif, fichier);
            return ResponseEntity.ok(justificatif);
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("Erreur lors de l'upload du fichier : " + e.getMessage());
        } catch (RuntimeException e) {
            String msg = e.getMessage();
            if (msg != null && msg.contains("existe déjà pour cette absence")) {
                return ResponseEntity.status(409).body("Un justificatif a déjà été déposé pour cette absence. Vous ne pouvez en déposer qu'un seul.");
            }
            return ResponseEntity.badRequest().body(msg);
        }
    }

    /**
     * Valide un justificatif
     * PUT /api/justificatifs/{id}/valider
     */
    @PutMapping("/{id}/valider")
    public ResponseEntity<?> validerJustificatif(
            @PathVariable Long id,
            @RequestParam Long validateurId,
            @RequestParam(required = false) String commentaire) {
        try {
            Justificatif justificatif = justificatifService.validerJustificatif(id, validateurId, commentaire);
            return ResponseEntity.ok(justificatif);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Refuse un justificatif
     * PUT /api/justificatifs/{id}/refuser
     */
    @PutMapping("/{id}/refuser")
    public ResponseEntity<?> refuserJustificatif(
            @PathVariable Long id,
            @RequestParam Long validateurId,
            @RequestParam(required = false) String commentaire) {
        try {
            Justificatif justificatif = justificatifService.refuserJustificatif(id, validateurId, commentaire);
            return ResponseEntity.ok(justificatif);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Télécharge un fichier justificatif
     * GET /api/justificatifs/{id}/download
     */
    @GetMapping("/{id}/download")
    public ResponseEntity<?> downloadJustificatif(@PathVariable Long id) {
        try {
            Justificatif justificatif = justificatifService.getJustificatifById(id)
                    .orElseThrow(() -> new RuntimeException("Justificatif non trouvé"));

            Path filePath = Paths.get(uploadPath).resolve(justificatif.getFichierPath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Détecte le type MIME du fichier
                String contentType = null;
                try {
                    contentType = Files.probeContentType(filePath);
                } catch (IOException ignored) {}
                if (contentType == null) {
                    contentType = "application/octet-stream";
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                                "attachment; filename=\"" + justificatif.getFichierPath() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors du téléchargement : " + e.getMessage());
        }
    }

    /**
     * Obtient un justificatif par son ID
     * GET /api/justificatifs/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getJustificatifById(@PathVariable Long id) {
        return justificatifService.getJustificatifById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient tous les justificatifs d'un étudiant
     * GET /api/justificatifs/etudiant/{etudiantId}
     */
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Justificatif>> getJustificatifsByEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(justificatifService.getJustificatifsByEtudiant(etudiantId));
    }

    /**
     * Obtient tous les justificatifs en attente
     * GET /api/justificatifs/en-attente
     */
    @GetMapping("/en-attente")
    public ResponseEntity<List<Justificatif>> getJustificatifsEnAttente() {
        return ResponseEntity.ok(justificatifService.getJustificatifsEnAttente());
    }

    /**
     * Obtient tous les justificatifs
     * GET /api/justificatifs
     */
    @GetMapping
    public ResponseEntity<List<Justificatif>> getAllJustificatifs() {
        return ResponseEntity.ok(justificatifService.getAllJustificatifs());
    }

    /**
     * Obtient tous les justificatifs traités par un validateur (chef)
     * GET /api/justificatifs/traites/{validateurId}
     */
    @GetMapping("/traites/{validateurId}")
    public ResponseEntity<List<Justificatif>> getJustificatifsTraitesByValidateur(@PathVariable Long validateurId) {
        return ResponseEntity.ok(justificatifService.getJustificatifsTraitesParValidateur(validateurId));
    }

    /**
     * Supprime un justificatif
     * DELETE /api/justificatifs/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteJustificatif(@PathVariable Long id) {
        try {
            justificatifService.deleteJustificatif(id);
            return ResponseEntity.ok(Map.of("message", "Justificatif supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
