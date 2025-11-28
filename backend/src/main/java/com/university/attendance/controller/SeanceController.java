package com.university.attendance.controller;

import com.university.attendance.dto.SeanceDTO;
import com.university.attendance.model.*;
import com.university.attendance.repository.GroupeRepository;
import com.university.attendance.repository.MatiereRepository;
import com.university.attendance.repository.UserRepository;
import com.university.attendance.service.SeanceService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Controller pour gérer les séances
 */
@RestController
@RequestMapping("/api/seances")
@CrossOrigin(origins = "*")
public class SeanceController {

    @Autowired
    private SeanceService seanceService;

    @Autowired
    private MatiereRepository matiereRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupeRepository groupeRepository;

    /**
     * Crée une nouvelle séance
     * POST /api/seances
     */
    @PostMapping
    public ResponseEntity<?> createSeance(@Valid @RequestBody SeanceDTO seanceDTO) {
        try {
            Matiere matiere = matiereRepository.findById(seanceDTO.getMatiereId())
                    .orElseThrow(() -> new RuntimeException("Matière non trouvée"));

            User enseignant = userRepository.findById(seanceDTO.getEnseignantId())
                    .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));

            Seance seance = new Seance();
            seance.setMatiere(matiere);
            seance.setEnseignant(enseignant);

            // Récupère le typeSeance depuis la matière
            TypeSeance typeSeance = TypeSeance.valueOf(matiere.getTypeSeance());
            seance.setTypeSeance(typeSeance);

            seance.setDateDebut(seanceDTO.getDateDebut());
            seance.setDateFin(seanceDTO.getDateFin());
            seance.setSalle(seanceDTO.getSalle());
            seance.setCommentaire(seanceDTO.getCommentaire());

            // Pour les TD/TP, associe le groupe
            if (typeSeance == TypeSeance.TD_TP && seanceDTO.getGroupeId() != null) {
                Groupe groupe = groupeRepository.findById(seanceDTO.getGroupeId())
                        .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
                seance.setGroupe(groupe);
            }

            Seance createdSeance = seanceService.createSeance(seance);
            return ResponseEntity.ok(createdSeance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Lance une séance et génère le code dynamique
     * POST /api/seances/{id}/start
     */
    @PostMapping("/{id}/start")
    public ResponseEntity<?> startSeance(@PathVariable Long id) {
        try {
            Seance seance = seanceService.startSeance(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Séance lancée avec succès");
            response.put("code", seance.getCodeDynamique());
            response.put("expiration", seance.getCodeExpiration());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Renouvelle le code dynamique d'une séance
     * POST /api/seances/{id}/renew-code
     */
    @PostMapping("/{id}/renew-code")
    public ResponseEntity<?> renewCode(@PathVariable Long id) {
        try {
            Seance seance = seanceService.renewCode(id);
            Map<String, Object> response = new HashMap<>();
            response.put("code", seance.getCodeDynamique());
            response.put("expiration", seance.getCodeExpiration());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient le code actuel d'une séance
     * GET /api/seances/{id}/code
     */
    @GetMapping("/{id}/code")
    public ResponseEntity<?> getCurrentCode(@PathVariable Long id) {
        try {
            String code = seanceService.getCurrentCode(id);
            Seance seance = seanceService.getSeanceById(id)
                    .orElseThrow(() -> new RuntimeException("Séance non trouvée"));

            Map<String, Object> response = new HashMap<>();
            response.put("code", code);
            response.put("expiration", seance.getCodeExpiration());
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Arrête une séance
     * POST /api/seances/{id}/stop
     */
    @PostMapping("/{id}/stop")
    public ResponseEntity<?> stopSeance(@PathVariable Long id) {
        try {
            Seance seance = seanceService.stopSeance(id);
            return ResponseEntity.ok(Map.of("message", "Séance terminée", "seance", seance));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Obtient toutes les séances
     * GET /api/seances
     */
    @GetMapping
    public ResponseEntity<List<Seance>> getAllSeances() {
        return ResponseEntity.ok(seanceService.getAllSeances());
    }

    /**
     * Obtient une séance par son ID
     * GET /api/seances/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getSeanceById(@PathVariable Long id) {
        return seanceService.getSeanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtient les séances d'un enseignant
     * GET /api/seances/enseignant/{enseignantId}
     */
    @GetMapping("/enseignant/{enseignantId}")
    public ResponseEntity<List<Seance>> getSeancesByEnseignant(@PathVariable Long enseignantId) {
        return ResponseEntity.ok(seanceService.getSeancesByEnseignant(enseignantId));
    }

    /**
     * Obtient les séances futures d'un enseignant
     * GET /api/seances/enseignant/{enseignantId}/upcoming
     */
    @GetMapping("/enseignant/{enseignantId}/upcoming")
    public ResponseEntity<List<Seance>> getUpcomingSeances(@PathVariable Long enseignantId) {
        return ResponseEntity.ok(seanceService.getUpcomingSeancesByEnseignant(enseignantId));
    }

    /**
     * Obtient les séances d'un groupe
     * GET /api/seances/groupe/{groupeId}
     */
    @GetMapping("/groupe/{groupeId}")
    public ResponseEntity<List<Seance>> getSeancesByGroupe(@PathVariable Long groupeId) {
        return ResponseEntity.ok(seanceService.getSeancesByGroupe(groupeId));
    }

    /**
     * Annule une séance
     * PUT /api/seances/{id}/cancel
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelSeance(@PathVariable Long id) {
        try {
            Seance seance = seanceService.cancelSeance(id);
            return ResponseEntity.ok(seance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Supprime une séance
     * DELETE /api/seances/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteSeance(@PathVariable Long id) {
        try {
            seanceService.deleteSeance(id);
            return ResponseEntity.ok(Map.of("message", "Séance supprimée avec succès"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
