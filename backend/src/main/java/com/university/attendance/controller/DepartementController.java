package com.university.attendance.controller;

import com.university.attendance.model.Departement;
import com.university.attendance.service.DepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les départements
 */
@RestController
@RequestMapping("/api/departements")
@CrossOrigin(origins = "*")
public class DepartementController {

    @Autowired
    private DepartementService departementService;

    /**
     * Crée un nouveau département
     * POST /api/departements
     */
    @PostMapping
    public ResponseEntity<?> createDepartement(@RequestBody Departement departement) {
        try {
            Departement created = departementService.createDepartement(departement);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Met à jour un département
     * PUT /api/departements/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartement(@PathVariable Long id, @RequestBody Departement departement) {
        try {
            Departement updated = departementService.updateDepartement(id, departement);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient un département par son ID
     * GET /api/departements/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getDepartementById(@PathVariable Long id) {
        return departementService.getDepartementById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient tous les départements
     * GET /api/departements
     */
    @GetMapping
    public ResponseEntity<List<Departement>> getAllDepartements() {
        return ResponseEntity.ok(departementService.getAllDepartements());
    }

    /**
     * Obtient tous les départements actifs
     * GET /api/departements/actifs
     */
    @GetMapping("/actifs")
    public ResponseEntity<List<Departement>> getDepartementsActifs() {
        return ResponseEntity.ok(departementService.getDepartementsActifs());
    }

    /**
     * Désactive un département
     * PUT /api/departements/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateDepartement(@PathVariable Long id) {
        try {
            departementService.deactivateDepartement(id);
            return ResponseEntity.ok(Map.of("message", "Département désactivé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Supprime un département
     * DELETE /api/departements/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDepartement(@PathVariable Long id) {
        try {
            departementService.deleteDepartement(id);
            return ResponseEntity.ok(Map.of("message", "Département supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
