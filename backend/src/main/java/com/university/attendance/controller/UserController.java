package com.university.attendance.controller;

import com.university.attendance.model.Role;
import com.university.attendance.model.User;
import com.university.attendance.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les utilisateurs
 */
@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * Crée un nouvel utilisateur
     * POST /api/users
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            User createdUser = userService.createUser(user);
            return ResponseEntity.ok(createdUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Met à jour un utilisateur
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User user) {
        try {
            User updatedUser = userService.updateUser(id, user);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient un utilisateur par son ID
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient un utilisateur par son email
     * GET /api/users/email/{email}
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient tous les utilisateurs
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Obtient tous les utilisateurs par rôle
     * GET /api/users/role/{role}
     */
    @GetMapping("/role/{role}")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable Role role) {
        return ResponseEntity.ok(userService.getUsersByRole(role));
    }

    /**
     * Obtient tous les étudiants d'une formation
     * GET /api/users/formation/{formationId}/etudiants
     */
    @GetMapping("/formation/{formationId}/etudiants")
    public ResponseEntity<List<User>> getEtudiantsByFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(userService.getEtudiantsByFormation(formationId));
    }

    /**
     * Obtient tous les étudiants d'une formation qui ne sont pas encore affectés à un groupe
     * GET /api/users/formation/{formationId}/etudiants/sans-groupe
     * Utilisé par le secrétariat pour l'affectation des étudiants aux groupes
     */
    @GetMapping("/formation/{formationId}/etudiants/sans-groupe")
    public ResponseEntity<List<User>> getEtudiantsSansGroupeByFormation(@PathVariable Long formationId) {
        return ResponseEntity.ok(userService.getEtudiantsSansGroupeByFormation(formationId));
    }

    /**
     * Obtient tous les enseignants d'un département
     * GET /api/users/departement/{departementId}/enseignants
     */
    @GetMapping("/departement/{departementId}/enseignants")
    public ResponseEntity<List<User>> getEnseignantsByDepartement(@PathVariable Long departementId) {
        return ResponseEntity.ok(userService.getEnseignantsByDepartement(departementId));
    }

    /**
     * Désactive un utilisateur
     * PUT /api/users/{id}/deactivate
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        try {
            userService.deactivateUser(id);
            return ResponseEntity.ok(Map.of("message", "Utilisateur désactivé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Supprime un utilisateur
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "Utilisateur supprimé avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Vérifie si un email existe
     * GET /api/users/exists/email/{email}
     */
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Map<String, Boolean>> checkEmailExists(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(Map.of("exists", exists));
    }
}
