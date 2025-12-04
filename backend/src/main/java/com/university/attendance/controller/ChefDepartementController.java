package com.university.attendance.controller;

import com.university.attendance.service.ChefDepartementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour les fonctionnalités du Chef de Département
 */
@RestController
@RequestMapping("/api/chef-departement")
@CrossOrigin(origins = "*")
public class ChefDepartementController {

    @Autowired
    private ChefDepartementService chefDepartementService;

    /**
     * Récupère tous les étudiants du département du Chef de Département
     * GET /api/chef-departement/{chefId}/etudiants
     */
    @GetMapping("/{chefId}/etudiants")
    public ResponseEntity<?> getEtudiantsDuDepartement(@PathVariable Long chefId) {
        try {
            List<Map<String, Object>> etudiants = chefDepartementService.getEtudiantsByDepartement(chefId);
            return ResponseEntity.ok(etudiants);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupère les étudiants filtrés par formation
     * GET /api/chef-departement/{chefId}/etudiants/formation/{formationId}
     */
    @GetMapping("/{chefId}/etudiants/formation/{formationId}")
    public ResponseEntity<?> getEtudiantsByFormation(
            @PathVariable Long chefId,
            @PathVariable Long formationId) {
        try {
            List<Map<String, Object>> etudiants = chefDepartementService.getEtudiantsByFormation(chefId, formationId);
            return ResponseEntity.ok(etudiants);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Récupère les informations détaillées d'un étudiant
     * GET /api/chef-departement/{chefId}/etudiant/{etudiantId}
     */
    @GetMapping("/{chefId}/etudiant/{etudiantId}")
    public ResponseEntity<?> getEtudiantDetails(
            @PathVariable Long chefId,
            @PathVariable Long etudiantId) {
        try {
            Map<String, Object> details = chefDepartementService.getEtudiantDetails(chefId, etudiantId);
            return ResponseEntity.ok(details);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
