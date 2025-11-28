package com.university.attendance.controller;

import com.university.attendance.model.GroupeEtudiant;
import com.university.attendance.service.GroupeEtudiantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer l'affectation des étudiants aux groupes
 */
@RestController
@RequestMapping("/api/groupe-etudiants")
@CrossOrigin(origins = "*")
public class GroupeEtudiantController {

    @Autowired
    private GroupeEtudiantService groupeEtudiantService;

    /**
     * Affecte un étudiant à un groupe
     * POST /api/groupe-etudiants/affecter
     */
    @PostMapping("/affecter")
    public ResponseEntity<?> affecterEtudiantGroupe(
            @RequestParam Long etudiantId,
            @RequestParam Long groupeId) {
        try {
            GroupeEtudiant affectation = groupeEtudiantService.affecterEtudiantGroupe(etudiantId, groupeId);
            return ResponseEntity.ok(affectation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retire un étudiant d'un groupe
     * DELETE /api/groupe-etudiants/retirer
     */
    @DeleteMapping("/retirer")
    public ResponseEntity<?> retirerEtudiantGroupe(
            @RequestParam Long etudiantId,
            @RequestParam Long groupeId) {
        try {
            groupeEtudiantService.retirerEtudiantGroupe(etudiantId, groupeId);
            return ResponseEntity.ok(Map.of("message", "Étudiant retiré du groupe avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient tous les groupes d'un étudiant
     * GET /api/groupe-etudiants/etudiant/{etudiantId}
     */
    @GetMapping("/etudiant/{etudiantId}")
    public ResponseEntity<List<GroupeEtudiant>> getGroupesEtudiant(@PathVariable Long etudiantId) {
        return ResponseEntity.ok(groupeEtudiantService.getGroupesEtudiant(etudiantId));
    }

    /**
     * Obtient tous les étudiants d'un groupe
     * GET /api/groupe-etudiants/groupe/{groupeId}
     */
    @GetMapping("/groupe/{groupeId}")
    public ResponseEntity<List<GroupeEtudiant>> getEtudiantsGroupe(@PathVariable Long groupeId) {
        return ResponseEntity.ok(groupeEtudiantService.getEtudiantsGroupe(groupeId));
    }

    /**
     * Obtient toutes les affectations
     * GET /api/groupe-etudiants
     */
    @GetMapping
    public ResponseEntity<List<GroupeEtudiant>> getAllGroupeEtudiants() {
        return ResponseEntity.ok(groupeEtudiantService.getAllGroupeEtudiants());
    }

    /**
     * Obtient une affectation par ID
     * GET /api/groupe-etudiants/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupeEtudiantById(@PathVariable Long id) {
        return groupeEtudiantService.getGroupeEtudiantById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Supprime toutes les affectations d'un étudiant
     * DELETE /api/groupe-etudiants/etudiant/{etudiantId}/all
     */
    @DeleteMapping("/etudiant/{etudiantId}/all")
    public ResponseEntity<?> supprimerToutesAffectationsEtudiant(@PathVariable Long etudiantId) {
        try {
            groupeEtudiantService.supprimerToutesAffectationsEtudiant(etudiantId);
            return ResponseEntity.ok(Map.of("message", "Toutes les affectations de l'étudiant ont été supprimées"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Supprime toutes les affectations d'un groupe
     * DELETE /api/groupe-etudiants/groupe/{groupeId}/all
     */
    @DeleteMapping("/groupe/{groupeId}/all")
    public ResponseEntity<?> supprimerToutesAffectationsGroupe(@PathVariable Long groupeId) {
        try {
            groupeEtudiantService.supprimerToutesAffectationsGroupe(groupeId);
            return ResponseEntity.ok(Map.of("message", "Toutes les affectations du groupe ont été supprimées"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Vérifie si un étudiant est dans un groupe
     * GET /api/groupe-etudiants/verifier
     */
    @GetMapping("/verifier")
    public ResponseEntity<Map<String, Boolean>> verifierEtudiantInGroupe(
            @RequestParam Long etudiantId,
            @RequestParam Long groupeId) {
        boolean isIn = groupeEtudiantService.isEtudiantInGroupe(etudiantId, groupeId);
        return ResponseEntity.ok(Map.of("isInGroupe", isIn));
    }
}
