package com.university.attendance.config;

import com.university.attendance.model.*;
import com.university.attendance.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Initialise les données de test au démarrage de l'application.
 * Cette version ajoute des données supplémentaires (départements, formations,
 * matières, groupes, étudiants, enseignants, séances, présences, justificatifs)
 * sans modifier les comptes existants (notamment `admin` et les enseignants
 * déjà configurés). Si un élément existe déjà (par nom/email), il n'est pas
 * dupliqué.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepartementRepository departementRepository;

    @Autowired
    private FormationRepository formationRepository;

    @Autowired
    private MatiereRepository matiereRepository;

    @Autowired
    private GroupeRepository groupeRepository;

    @Autowired
    private GroupeEtudiantRepository groupeEtudiantRepository;

    @Autowired
    private SeanceRepository seanceRepository;

    @Autowired
    private PresenceRepository presenceRepository;

    @Autowired
    private JustificatifRepository justificatifRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer: vérification des données de base...");

        // Mot de passe commun pour les comptes de test (ne pas modifier l'admin existant)
        String defaultPassword = "password123";
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        // 1) Utilisateurs de base : ne pas remplacer si existent
        createUserIfMissing("admin@university.com", "Admin", "Système", Role.ADMIN, null, null, encodedPassword);
        createUserIfMissing("pierre.dubois@university.com", "Dubois", "Pierre", Role.CHEF_DEPARTEMENT, "ENS001", null, encodedPassword);
        createUserIfMissing("sophie.martin@university.com", "Martin", "Sophie", Role.ENSEIGNANT, "ENS002", null, encodedPassword);

        // 2) Départements / formations / matières
        Departement info = departementRepository.findByNom("Informatique")
                .orElseGet(() -> departementRepository.save(new Departement("Informatique")));

        // Formations
        Formation lInfo = findOrCreateFormation("Licence Informatique", info, 1);
        Formation mInfo = findOrCreateFormation("Master Informatique", info, 4);

        // Matières
        Matiere algo = findOrCreateMatiere("Algorithmique", "INF101", lInfo);
        Matiere db = findOrCreateMatiere("Base de Données", "INF102", lInfo);
        Matiere archi = findOrCreateMatiere("Architecture des Ordinateurs", "INF201", mInfo);

        // 3) Groupes
        Groupe td1 = findOrCreateGroupe("TD1", lInfo);
        Groupe td2 = findOrCreateGroupe("TD2", lInfo);

        // 4) Autres utilisateurs (enseignants & étudiants)
        User enseignant2 = createUserIfMissing("paul.bernard@university.com", "Bernard", "Paul", Role.ENSEIGNANT, "ENS003", info, encodedPassword);

        User etu2 = createUserIfMissing("lucie.rene@university.com", "René", "Lucie", Role.ETUDIANT, "ETU002", lInfo, encodedPassword);
        User etu3 = createUserIfMissing("anas.khalid@university.com", "Khalid", "Anas", Role.ETUDIANT, "ETU003", lInfo, encodedPassword);
        User etu4 = createUserIfMissing("sara.lemarchand@university.com", "Lemarchand", "Sara", Role.ETUDIANT, "ETU004", lInfo, encodedPassword);

        // 5) Affectations groupe <-> étudiants (TD)
        assignStudentToGroupIfMissing(etu2, td1);
        assignStudentToGroupIfMissing(etu3, td1);
        assignStudentToGroupIfMissing(etu4, td2);

        // 6) Séances (une séance passée pour tests, une séance future)
        User enseignantPrincipal = userRepository.findByEmail("sophie.martin@university.com").orElse(enseignant2);

        // Séance passée (il y a 7 jours)
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime pastStart = now.minusDays(7).withHour(9).withMinute(0).withSecond(0).withNano(0);
        java.time.LocalDateTime pastEnd = pastStart.plusHours(2);

        Seance pastSeance = seanceRepository.save(new Seance(algo, enseignantPrincipal, TypeSeance.TD_TP, pastStart, pastEnd));
        pastSeance.setTerminee(true);
        seanceRepository.save(pastSeance);

        // Séance future (dans 2 jours)
        java.time.LocalDateTime futureStart = now.plusDays(2).withHour(14).withMinute(0).withSecond(0).withNano(0);
        java.time.LocalDateTime futureEnd = futureStart.plusHours(2);
        Seance futureSeance = seanceRepository.save(new Seance(db, enseignant2, TypeSeance.CM, futureStart, futureEnd));

        // 7) Présences pour la séance passée
        // Présent pour etu2
        Presence p1 = new Presence(pastSeance, etu2, StatutPresence.PRESENT);
        p1.setHeureValidation(pastStart.plusMinutes(5));
        presenceRepository.save(p1);

        // Absent pour etu3 (on créera un justificatif pour cette absence)
        Presence p2 = new Presence(pastSeance, etu3, StatutPresence.ABSENT);
        presenceRepository.save(p2);

        // Retard pour etu4
        Presence p3 = new Presence(pastSeance, etu4, StatutPresence.RETARD);
        p3.setHeureValidation(pastStart.plusMinutes(20));
        presenceRepository.save(p3);

        // 8) Justificatif pour l'absence de etu3 (ex: fichier dummy)
        Justificatif just1 = new Justificatif();
        just1.setEtudiant(etu3);
        just1.setAbsence(p2);
        just1.setMotif("Rendez-vous médical");
        just1.setFichierPath("sample-justif.pdf");
        just1.setStatut(StatutJustificatif.EN_ATTENTE);
        justificatifRepository.save(just1);

        System.out.println("DataInitializer: données initiales créées ou vérifiées.");
    }

    // Helpers
    private User createUserIfMissing(String email, String nom, String prenom, Role role, String numero, Object optional, String encodedPassword) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }

        User u = new User();
        u.setNom(nom);
        u.setPrenom(prenom);
        u.setEmail(email);
        u.setTelephone(null);
        u.setMotDePasse(encodedPassword);
        u.setRole(role);
        u.setActif(true);
        if (role == Role.ENSEIGNANT || role == Role.CHEF_DEPARTEMENT) {
            if (numero != null) u.setNumeroEnseignant(numero);
            if (optional instanceof Departement) u.setDepartement((Departement) optional);
        }
        if (role == Role.ETUDIANT) {
            if (numero != null) u.setNumeroEtudiant(numero);
            if (optional instanceof Formation) u.setFormation((Formation) optional);
        }

        return userRepository.save(u);
    }

    private Formation findOrCreateFormation(String nom, Departement departement, Integer niveau) {
        List<Formation> list = formationRepository.findByDepartementId(departement.getId());
        for (Formation f : list) {
            if (f.getNom().equalsIgnoreCase(nom)) return f;
        }
        Formation f = new Formation(nom, departement, niveau);
        return formationRepository.save(f);
    }

    private Matiere findOrCreateMatiere(String nom, String code, Formation formation) {
        Optional<Matiere> mOpt = matiereRepository.findByCode(code);
        if (mOpt.isPresent()) return mOpt.get();
        Matiere m = new Matiere(nom, code, formation);
        return matiereRepository.save(m);
    }

    private Groupe findOrCreateGroupe(String nom, Formation formation) {
        List<Groupe> groupes = groupeRepository.findByFormationId(formation.getId());
        for (Groupe g : groupes) {
            if (g.getNom().equalsIgnoreCase(nom)) return g;
        }
        Groupe g = new Groupe(nom, formation);
        return groupeRepository.save(g);
    }

    private void assignStudentToGroupIfMissing(User etudiant, Groupe groupe) {
        if (etudiant == null || groupe == null) return;
        boolean exists = groupeEtudiantRepository.existsByEtudiantIdAndGroupeId(etudiant.getId(), groupe.getId());
        if (!exists) {
            com.university.attendance.model.GroupeEtudiant ge = new com.university.attendance.model.GroupeEtudiant();
            ge.setEtudiant(etudiant);
            ge.setGroupe(groupe);
            groupeEtudiantRepository.save(ge);
        }
    }

}
