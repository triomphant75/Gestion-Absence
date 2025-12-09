package com.university.attendance.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.university.attendance.dto.EtudiantInscritDTO;
import com.university.attendance.dto.SeanceDTO;
import com.university.attendance.model.Groupe;
import com.university.attendance.model.Matiere;
import com.university.attendance.model.Seance;
import com.university.attendance.model.TypeSeance;
import com.university.attendance.model.User;
import com.university.attendance.repository.GroupeRepository;
import com.university.attendance.repository.MatiereRepository;
import com.university.attendance.repository.UserRepository;
import com.university.attendance.service.SeanceService;

import jakarta.validation.Valid;

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
        List<Seance> seances = seanceService.getSeancesByEnseignant(enseignantId);
        System.out.println("DEBUG - Récupération séances pour enseignant ID: " + enseignantId);
        System.out.println("DEBUG - Nombre de séances trouvées: " + seances.size());
        return ResponseEntity.ok(seances);
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
     * Met à jour une séance et la marque comme reportée
     * PUT /api/seances/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateSeance(@PathVariable Long id, @Valid @RequestBody SeanceDTO seanceDTO) {
        try {
            Matiere matiere = matiereRepository.findById(seanceDTO.getMatiereId())
                    .orElseThrow(() -> new RuntimeException("Matière non trouvée"));

            User enseignant = userRepository.findById(seanceDTO.getEnseignantId())
                    .orElseThrow(() -> new RuntimeException("Enseignant non trouvé"));

            Seance seanceDetails = new Seance();
            seanceDetails.setMatiere(matiere);
            seanceDetails.setEnseignant(enseignant);

            // Récupère le typeSeance depuis la matière
            TypeSeance typeSeance = TypeSeance.valueOf(matiere.getTypeSeance());
            seanceDetails.setTypeSeance(typeSeance);

            seanceDetails.setDateDebut(seanceDTO.getDateDebut());
            seanceDetails.setDateFin(seanceDTO.getDateFin());
            seanceDetails.setSalle(seanceDTO.getSalle());
            seanceDetails.setCommentaire(seanceDTO.getCommentaire());

            // Pour les TD/TP, associe le groupe
            if (typeSeance == TypeSeance.TD_TP && seanceDTO.getGroupeId() != null) {
                Groupe groupe = groupeRepository.findById(seanceDTO.getGroupeId())
                        .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
                seanceDetails.setGroupe(groupe);
            }

            Seance updatedSeance = seanceService.updateSeance(id, seanceDetails);
            return ResponseEntity.ok(updatedSeance);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

    
    /**
     * Obtient la liste de tous les étudiants inscrits à une séance
     * GET /api/seances/{id}/etudiants
     * 
     * Utile pour :
     * - Consulter la liste des étudiants qui doivent assister au cours
     * - Préparer une liste d'émargement
     * - Vérifier les effectifs
     */
    @GetMapping("/{id}/etudiants")
    public ResponseEntity<?> getEtudiantsInscrits(@PathVariable Long id) {
        try {
            List<EtudiantInscritDTO> etudiants = seanceService.getEtudiantsInscrits(id);
            
            Map<String, Object> response = Map.of(
                "seanceId", id,
                "totalEtudiants", etudiants.size(),
                "etudiants", etudiants
            );
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Obtient le nombre d'étudiants inscrits à une séance
     * GET /api/seances/{id}/etudiants/count
     */
    @GetMapping("/{id}/etudiants/count")
    public ResponseEntity<?> countEtudiantsInscrits(@PathVariable Long id) {
        try {
            int count = seanceService.countEtudiantsInscrits(id);
            return ResponseEntity.ok(Map.of(
                "seanceId", id,
                "totalEtudiants", count
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage()
            ));
        }
    }
}
