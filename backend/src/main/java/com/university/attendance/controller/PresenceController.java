package com.university.attendance.controller;

import com.university.attendance.dto.StatistiquesEtudiantDTO;
import com.university.attendance.dto.ValidateCodeRequest;
import com.university.attendance.model.Presence;
import com.university.attendance.model.StatutPresence;
import com.university.attendance.service.PresenceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les présences
 */
@RestController
@RequestMapping("/api/presences")
@CrossOrigin(origins = "*")
public class PresenceController {

    @Autowired
    private PresenceService presenceService;

    /**
     * Valide la présence d'un étudiant via le code dynamique
     * POST /api/presences/validate-code
     */
    @PostMapping("/validate-code")
    public ResponseEntity<?> validateCode(@Valid @RequestBody ValidateCodeRequest request,
                                          @RequestHeader("X-User-Id") Long etudiantId) {
        try {
            Presence presence = presenceService.validateCode(
                    request.getSeanceId(),
                    etudiantId,
                    request.getCode()
            );
            return ResponseEntity.ok(Map.of(
                    "message", "Présence validée avec succès",
                    "presence", presence
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Crée une présence manuellement (enseignant)
     * POST /api/presences
     */
    @PostMapping
    public ResponseEntity<?> createPresenceManuelle(
            @RequestParam Long seanceId,
            @RequestParam Long etudiantId,
            @RequestParam StatutPresence statut) {
        try {
            Presence presence = presenceService.createPresenceManuelle(seanceId, etudiantId, statut);
            return ResponseEntity.ok(presence);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Modifie une présence (enseignant)
     * PUT /api/presences/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updatePresence(
            @PathVariable Long id,
            @RequestParam StatutPresence statut,
            @RequestParam(required = false) String commentaire) {
        try {
            Presence presence = presenceService.updatePresence(id, statut, commentaire);
            return ResponseEntity.ok(presence);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient les présences d'un étudiant
     * GET /api/presences/etudiant/{etudiantId}
     */
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Presence>> getPresencesByEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(presenceService.getPresencesByEtudiant(etudiantId));
    }

    /**
     * Obtient les présences d'une séance
     * GET /api/presences/seance/{seanceId}
     */
    @GetMapping("/seance/{seanceId}")
    public ResponseEntity<List<Presence>> getPresencesBySeance(@PathVariable Long seanceId) {
        return ResponseEntity.ok(presenceService.getPresencesBySeance(seanceId));
    }

    /**
     * Obtient les statistiques d'un étudiant
     * GET /api/presences/statistiques/{etudiantId}
     */
    @GetMapping("/statistiques/{etudiantId}")
    public ResponseEntity<StatistiquesEtudiantDTO> getStatistiquesEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(presenceService.getStatistiquesEtudiant(etudiantId));
    }

    /**
     * Compte le nombre d'absences d'un étudiant pour une matière
     * GET /api/presences/absences/count
     */
    @GetMapping("/absences/count")
    public ResponseEntity<Long> countAbsences(
            @RequestParam Long etudiantId,
            @RequestParam Long matiereId) {
        Long count = presenceService.countAbsencesByEtudiantAndMatiere(etudiantId, matiereId);
        return ResponseEntity.ok(count);
    }

    /**
     * Supprime une présence
     * DELETE /api/presences/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePresence(@PathVariable Long id) {
        try {
            presenceService.deletePresence(id);
            return ResponseEntity.ok(Map.of("message", "Présence supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
