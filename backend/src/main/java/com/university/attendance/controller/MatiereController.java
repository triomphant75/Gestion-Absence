package com.university.attendance.controller;

import com.university.attendance.dto.MatiereDTO;
import com.university.attendance.model.Matiere;
import com.university.attendance.service.MatiereService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les matières
 */
@RestController
@RequestMapping("/api/matieres")
@CrossOrigin(origins = "*")
public class MatiereController {

    @Autowired
    private MatiereService matiereService;

    /**
     * Crée une nouvelle matière
     * POST /api/matieres
     */
    @PostMapping
    public ResponseEntity<?> createMatiere(@RequestBody MatiereDTO matiereDTO) {
        try {
            Matiere created = matiereService.createMatiere(matiereDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Met à jour une matière
     * PUT /api/matieres/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateMatiere(@PathVariable Long id, @RequestBody MatiereDTO matiereDTO) {
        try {
            Matiere updated = matiereService.updateMatiere(id, matiereDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient une matière par son ID
     * GET /api/matieres/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getMatiereById(@PathVariable Long id) {
        return matiereService.getMatiereById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient une matière par son code
     * GET /api/matieres/code/{code}
     */
    @GetMapping("/code/{code}")
    public ResponseEntity<?> getMatiereByCode(@PathVariable String code) {
        return matiereService.getMatiereByCode(code)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient toutes les matières
     * GET /api/matieres
     */
    @GetMapping
    public ResponseEntity<List<Matiere>> getAllMatieres() {
        return ResponseEntity.ok(matiereService.getAllMatieres());
    }

    /**
     * Obtient toutes les matières actives
     * GET /api/matieres/actives
     */
    @GetMapping("/actives")
    public ResponseEntity<List<Matiere>> getMatieresActives() {
        return ResponseEntity.ok(matiereService.getMatieresActives());
    }

    /**
     * Obtient toutes les matières d'une formation
     * GET /api/matieres/formation/{formationId}
     */
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<Matiere>> getMatieresByFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(matiereService.getMatieresByFormation(formationId));
    }

    /**
     * Désactive une matière
     * PUT /api/matieres/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateMatiere(@PathVariable Long id) {
        try {
            matiereService.deactivateMatiere(id);
            return ResponseEntity.ok(Map.of("message", "Matière désactivée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Supprime une matière
     * DELETE /api/matieres/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMatiere(@PathVariable Long id) {
        try {
            matiereService.deleteMatiere(id);
            return ResponseEntity.ok(Map.of("message", "Matière supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
