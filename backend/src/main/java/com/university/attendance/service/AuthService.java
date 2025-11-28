package com.university.attendance.service;

import com.university.attendance.config.JwtUtil;
import com.university.attendance.dto.LoginRequest;
import com.university.attendance.dto.LoginResponse;
import com.university.attendance.model.User;
import com.university.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Service d'authentification
 */
@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Authentifie un utilisateur et génère un token JWT
     */
    public LoginResponse login(LoginRequest loginRequest) {
        // Trouve l'utilisateur par email
        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou mot de passe incorrect"));

        // Vérifie si l'utilisateur est actif
        if (!user.getActif()) {
            throw new RuntimeException("Compte désactivé");
        }

        // Vérifie le mot de passe
        if (!passwordEncoder.matches(loginRequest.getMotDePasse(), user.getMotDePasse())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        // Génère le token JWT
        String token = jwtUtil.generateToken(
                user.getEmail(),
                user.getId(),
                user.getRole().name()
        );

        // Crée la réponse
        return new LoginResponse(
                token,
                user.getId(),
                user.getEmail(),
                user.getNom(),
                user.getPrenom(),
                user.getRole()
        );
    }

    /**
     * Valide un token JWT
     */
    public boolean validateToken(String token, String email) {
        return jwtUtil.validateToken(token, email);
    }

    /**
     * Extrait l'email du token
     */
    public String extractEmailFromToken(String token) {
        return jwtUtil.extractEmail(token);
    }

    /**
     * Extrait l'ID utilisateur du token
     */
    public Long extractUserIdFromToken(String token) {
        return jwtUtil.extractUserId(token);
    }

    /**
     * Extrait le rôle du token
     */
    public String extractRoleFromToken(String token) {
        return jwtUtil.extractRole(token);
    }
}
