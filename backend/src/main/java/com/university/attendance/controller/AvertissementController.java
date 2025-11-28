package com.university.attendance.controller;

import com.university.attendance.model.Avertissement;
import com.university.attendance.service.AvertissementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les avertissements académiques
 */
@RestController
@RequestMapping("/api/avertissements")
@CrossOrigin(origins = "*")
public class AvertissementController {

    @Autowired
    private AvertissementService avertissementService;

    /**
     * Crée un avertissement manuellement
     * POST /api/avertissements
     */
    @PostMapping
    public ResponseEntity<?> createAvertissement(
            @RequestParam Long etudiantId,
            @RequestParam Long matiereId,
            @RequestParam Integer nombreAbsences,
            @RequestParam String motif,
            @RequestParam Long createurId) {
        try {
            Avertissement avertissement = avertissementService.createAvertissement(
                    etudiantId, matiereId, nombreAbsences, motif, createurId);
            return ResponseEntity.ok(avertissement);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient un avertissement par son ID
     * GET /api/avertissements/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getAvertissementById(@PathVariable Long id) {
        return avertissementService.getAvertissementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient tous les avertissements
     * GET /api/avertissements
     */
    @GetMapping
    public ResponseEntity<List<Avertissement>> getAllAvertissements() {
        return ResponseEntity.ok(avertissementService.getAllAvertissements());
    }

    /**
     * Obtient tous les avertissements d'un étudiant
     * GET /api/avertissements/etudiant/{etudiantId}
     */
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<Avertissement>> getAvertissementsByEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(avertissementService.getAvertissementsByEtudiant(etudiantId));
    }

    /**
     * Obtient tous les avertissements d'une matière
     * GET /api/avertissements/matiere/{matiereId}
     */
    @GetMapping("/matiere/{matiereId}")
    public ResponseEntity<List<Avertissement>> getAvertissementsByMatiere(@PathVariable Long matiereId) {
        return ResponseEntity.ok(avertissementService.getAvertissementsByMatiere(matiereId));
    }

    /**
     * Obtient les avertissements d'un étudiant pour une matière
     * GET /api/avertissements/etudiant/{etudiantId}/matiere/{matiereId}
     */
    @GetMapping("/etudiant/{etudiantId}/matiere/{matiereId}")
    public ResponseEntity<List<Avertissement>> getAvertissementsByEtudiantAndMatiere(
            @PathVariable Long etudiantId,
            @PathVariable Long matiereId) {
        return ResponseEntity.ok(avertissementService.getAvertissementsByEtudiantAndMatiere(etudiantId, matiereId));
    }

    /**
     * Obtient tous les avertissements automatiques
     * GET /api/avertissements/automatiques
     */
    @GetMapping("/automatiques")
    public ResponseEntity<List<Avertissement>> getAvertissementsAutomatiques() {
        return ResponseEntity.ok(avertissementService.getAvertissementsAutomatiques());
    }

    /**
     * Obtient tous les avertissements manuels
     * GET /api/avertissements/manuels
     */
    @GetMapping("/manuels")
    public ResponseEntity<List<Avertissement>> getAvertissementsManuels() {
        return ResponseEntity.ok(avertissementService.getAvertissementsManuels());
    }

    /**
     * Compte le nombre d'avertissements d'un étudiant
     * GET /api/avertissements/etudiant/{etudiantId}/count
     */
    @GetMapping("/etudiant/{etudiantId}/count")
    public ResponseEntity<Map<String, Long>> countAvertissementsByEtudiant(@PathVariable Long etudiantId) {
        Long count = avertissementService.countAvertissementsByEtudiant(etudiantId);
        return ResponseEntity.ok(Map.of("count", count));
    }

    /**
     * Met à jour le motif d'un avertissement
     * PUT /api/avertissements/{id}/motif
     */
    @PutMapping("/{id}/motif")
    public ResponseEntity<?> updateMotif(@PathVariable Long id, @RequestParam String motif) {
        try {
            Avertissement updated = avertissementService.updateMotif(id, motif);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Supprime un avertissement
     * DELETE /api/avertissements/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAvertissement(@PathVariable Long id) {
        try {
            avertissementService.deleteAvertissement(id);
            return ResponseEntity.ok(Map.of("message", "Avertissement supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
