package com.university.attendance.controller;

import com.university.attendance.dto.GroupeDTO;
import com.university.attendance.model.Groupe;
import com.university.attendance.service.GroupeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les groupes TD/TP
 */
@RestController
@RequestMapping("/api/groupes")
@CrossOrigin(origins = "*")
public class GroupeController {

    @Autowired
    private GroupeService groupeService;

    /**
     * Crée un nouveau groupe
     * POST /api/groupes
     */
    @PostMapping
    public ResponseEntity<?> createGroupe(@RequestBody GroupeDTO groupeDTO) {
        try {
            Groupe created = groupeService.createGroupe(groupeDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Met à jour un groupe
     * PUT /api/groupes/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateGroupe(@PathVariable Long id, @RequestBody GroupeDTO groupeDTO) {
        try {
            Groupe updated = groupeService.updateGroupe(id, groupeDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient un groupe par son ID
     * GET /api/groupes/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupeById(@PathVariable Long id) {
        return groupeService.getGroupeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient tous les groupes
     * GET /api/groupes
     */
    @GetMapping
    public ResponseEntity<List<Groupe>> getAllGroupes() {
        return ResponseEntity.ok(groupeService.getAllGroupes());
    }

    /**
     * Obtient tous les groupes actifs
     * GET /api/groupes/actifs
     */
    @GetMapping("/actifs")
    public ResponseEntity<List<Groupe>> getGroupesActifs() {
        return ResponseEntity.ok(groupeService.getGroupesActifs());
    }

    /**
     * Obtient tous les groupes d'une formation
     * GET /api/groupes/formation/{formationId}
     */
    @GetMapping("/formation/{formationId}")
    public ResponseEntity<List<Groupe>> getGroupesByFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(groupeService.getGroupesByFormation(formationId));
    }

    /**
     * Désactive un groupe
     * PUT /api/groupes/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateGroupe(@PathVariable Long id) {
        try {
            groupeService.deactivateGroupe(id);
            return ResponseEntity.ok(Map.of("message", "Groupe désactivé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Supprime un groupe
     * DELETE /api/groupes/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGroupe(@PathVariable Long id) {
        try {
            groupeService.deleteGroupe(id);
            return ResponseEntity.ok(Map.of("message", "Groupe supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
