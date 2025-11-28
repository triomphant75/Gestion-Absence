package com.university.attendance.controller;

import com.university.attendance.dto.LoginRequest;
import com.university.attendance.dto.LoginResponse;
import com.university.attendance.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour l'authentification
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * Endpoint de connexion
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Endpoint pour valider un token
     * GET /api/auth/validate
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String token) {
        try {
            if (token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            String email = authService.extractEmailFromToken(token);
            boolean isValid = authService.validateToken(token, email);

            if (isValid) {
                return ResponseEntity.ok("Token valide");
            } else {
                return ResponseEntity.badRequest().body("Token invalide");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Token invalide");
        }
    }
}
