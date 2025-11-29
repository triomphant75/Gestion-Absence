package com.university.attendance.service;

import com.university.attendance.model.Formation;
import com.university.attendance.model.Role;
import com.university.attendance.model.User;
import com.university.attendance.repository.FormationRepository;
import com.university.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service pour gérer les utilisateurs
 */
@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Crée un nouvel utilisateur
     */
    public User createUser(User user) {
        // Encode le mot de passe
        user.setMotDePasse(passwordEncoder.encode(user.getMotDePasse()));

        // Si une formation est associée (pour les étudiants), on la récupère
        if (user.getFormation() != null && user.getFormation().getId() != null) {
            Formation formation = formationRepository.findById(user.getFormation().getId())
                    .orElseThrow(() -> new RuntimeException("Formation non trouvée avec l'id : " + user.getFormation().getId()));
            user.setFormation(formation);
        }

        return userRepository.save(user);
    }

    /**
     * Met à jour un utilisateur
     */
    public User updateUser(Long id, User userDetails) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + id));

        user.setNom(userDetails.getNom());
        user.setPrenom(userDetails.getPrenom());
        user.setEmail(userDetails.getEmail());
        user.setTelephone(userDetails.getTelephone());

        // Met à jour le mot de passe seulement s'il est fourni
        if (userDetails.getMotDePasse() != null && !userDetails.getMotDePasse().isEmpty()) {
            user.setMotDePasse(passwordEncoder.encode(userDetails.getMotDePasse()));
        }

        return userRepository.save(user);
    }

    /**
     * Trouve un utilisateur par son ID
     */
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Trouve un utilisateur par son email
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * Trouve tous les utilisateurs
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Trouve tous les utilisateurs par rôle
     */
    public List<User> getUsersByRole(Role role) {
        return userRepository.findByRole(role);
    }

    /**
     * Trouve tous les étudiants d'une formation
     */
    public List<User> getEtudiantsByFormation(Long formationId) {
        return userRepository.findByFormationIdAndRole(formationId, Role.ETUDIANT);
    }

    /**
     * Trouve tous les enseignants d'un département
     */
    public List<User> getEnseignantsByDepartement(Long departementId) {
        return userRepository.findByDepartementIdAndRole(departementId, Role.ENSEIGNANT);
    }

    /**
     * Supprime un utilisateur
     */
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    /**
     * Désactive un utilisateur (soft delete)
     */
    public void deactivateUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec l'id : " + id));
        user.setActif(false);
        userRepository.save(user);
    }

    /**
     * Vérifie si un email existe déjà
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
