package com.university.attendance.controller;

import com.university.attendance.dto.FormationDTO;
import com.university.attendance.model.Formation;
import com.university.attendance.service.FormationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les formations
 */
@RestController
@RequestMapping("/api/formations")
@CrossOrigin(origins = "*")
public class FormationController {

    @Autowired
    private FormationService formationService;

    /**
     * Crée une nouvelle formation
     * POST /api/formations
     */
    @PostMapping
    public ResponseEntity<?> createFormation(@RequestBody FormationDTO formationDTO) {
        try {
            Formation created = formationService.createFormation(formationDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Met à jour une formation
     * PUT /api/formations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateFormation(@PathVariable Long id, @RequestBody FormationDTO formationDTO) {
        try {
            Formation updated = formationService.updateFormation(id, formationDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient une formation par son ID
     * GET /api/formations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getFormationById(@PathVariable Long id) {
        return formationService.getFormationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient toutes les formations
     * GET /api/formations
     */
    @GetMapping
    public ResponseEntity<List<Formation>> getAllFormations() {
        return ResponseEntity.ok(formationService.getAllFormations());
    }

    /**
     * Obtient toutes les formations actives
     * GET /api/formations/actives
     */
    @GetMapping("/actives")
    public ResponseEntity<List<Formation>> getFormationsActives() {
        return ResponseEntity.ok(formationService.getFormationsActives());
    }

    /**
     * Obtient toutes les formations d'un département
     * GET /api/formations/departement/{departementId}
     */
    @GetMapping("/departement/{departementId}")
    public ResponseEntity<List<Formation>> getFormationsByDepartement(@PathVariable Long departementId) {
        return ResponseEntity.ok(formationService.getFormationsByDepartement(departementId));
    }

    /**
     * Obtient toutes les formations par niveau
     * GET /api/formations/niveau/{niveau}
     */
    @GetMapping("/niveau/{niveau}")
    public ResponseEntity<List<Formation>> getFormationsByNiveau(@PathVariable Integer niveau) {
        return ResponseEntity.ok(formationService.getFormationsByNiveau(niveau));
    }

    /**
     * Désactive une formation
     * PUT /api/formations/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateFormation(@PathVariable Long id) {
        try {
            formationService.deactivateFormation(id);
            return ResponseEntity.ok(Map.of("message", "Formation désactivée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Supprime une formation
     * DELETE /api/formations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteFormation(@PathVariable Long id) {
        try {
            formationService.deleteFormation(id);
            return ResponseEntity.ok(Map.of("message", "Formation supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
