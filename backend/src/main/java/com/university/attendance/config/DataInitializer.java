package com.university.attendance.config;

import com.university.attendance.model.User;
import com.university.attendance.model.Role;
import com.university.attendance.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Initialise les données de test au démarrage de l'application
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Vérifie si les utilisateurs existent déjà
        if (userRepository.count() > 0) {
            System.out.println("Les utilisateurs de test existent déjà.");
            return;
        }

        System.out.println("Création des utilisateurs de test...");

        // Mot de passe commun pour tous les comptes de test
        String password = "password123";
        String encodedPassword = passwordEncoder.encode(password);

        // 1. ADMIN
        User admin = new User();
        admin.setNom("Admin");
        admin.setPrenom("Système");
        admin.setEmail("admin@university.com");
        admin.setTelephone("0601020304");
        admin.setMotDePasse(encodedPassword);
        admin.setRole(Role.ADMIN);
        admin.setActif(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setUpdatedAt(LocalDateTime.now());
        userRepository.save(admin);

        // 2. CHEF DE DÉPARTEMENT
        User chef = new User();
        chef.setNom("Dubois");
        chef.setPrenom("Pierre");
        chef.setEmail("pierre.dubois@university.com");
        chef.setTelephone("0601020305");
        chef.setMotDePasse(encodedPassword);
        chef.setRole(Role.CHEF_DEPARTEMENT);
        chef.setNumeroEnseignant("ENS001");
        chef.setActif(true);
        chef.setCreatedAt(LocalDateTime.now());
        chef.setUpdatedAt(LocalDateTime.now());
        userRepository.save(chef);

        // 3. ENSEIGNANT
        User enseignant = new User();
        enseignant.setNom("Martin");
        enseignant.setPrenom("Sophie");
        enseignant.setEmail("sophie.martin@university.com");
        enseignant.setTelephone("0601020306");
        enseignant.setMotDePasse(encodedPassword);
        enseignant.setRole(Role.ENSEIGNANT);
        enseignant.setNumeroEnseignant("ENS002");
        enseignant.setActif(true);
        enseignant.setCreatedAt(LocalDateTime.now());
        enseignant.setUpdatedAt(LocalDateTime.now());
        userRepository.save(enseignant);

        // 4. ÉTUDIANT
        User etudiant = new User();
        etudiant.setNom("Dubois");
        etudiant.setPrenom("Marie");
        etudiant.setEmail("marie.dubois@university.com");
        etudiant.setTelephone("0601020308");
        etudiant.setMotDePasse(encodedPassword);
        etudiant.setRole(Role.ETUDIANT);
        etudiant.setNumeroEtudiant("ETU001");
        etudiant.setActif(true);
        etudiant.setCreatedAt(LocalDateTime.now());
        etudiant.setUpdatedAt(LocalDateTime.now());
        userRepository.save(etudiant);

        System.out.println("✅ Utilisateurs de test créés avec succès!");
        System.out.println("Comptes disponibles (tous avec le mot de passe 'password123'):");
        System.out.println("  - Admin: admin@university.com");
        System.out.println("  - Chef Dép.: pierre.dubois@university.com");
        System.out.println("  - Enseignant: sophie.martin@university.com");
        System.out.println("  - Étudiant: marie.dubois@university.com");
    }
}
