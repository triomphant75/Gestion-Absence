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
 * Initialise les données de base au démarrage de l'application.
 * Crée les utilisateurs (admin, chefs de département, enseignants, étudiants),
 * les départements, les formations et les matières.
 * Le reste des données (groupes, séances, présences, justificatifs) sera créé manuellement.
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
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("DataInitializer: vérification des données de base...");

        // Mot de passe commun pour tous les comptes de test
        String defaultPassword = "password123";
        String encodedPassword = passwordEncoder.encode(defaultPassword);

        // ========================================
        // 1) DÉPARTEMENTS
        // ========================================
        Departement info = findOrCreateDepartement("Informatique", "Département d'informatique et sciences du numérique");
        Departement math = findOrCreateDepartement("Mathématiques", "Département de mathématiques et statistiques");
        Departement physique = findOrCreateDepartement("Physique", "Département de physique et sciences de la matière");

        // ========================================
        // 2) FORMATIONS
        // ========================================
        // Formations en Informatique
        Formation l1Info = findOrCreateFormation("Licence 1 Informatique", info, 1);
        Formation l2Info = findOrCreateFormation("Licence 2 Informatique", info, 2);
        Formation l3Info = findOrCreateFormation("Licence 3 Informatique", info, 3);
        Formation m1Info = findOrCreateFormation("Master 1 Informatique", info, 4);
        Formation m2Info = findOrCreateFormation("Master 2 Informatique", info, 5);

        // Formations en Mathématiques
        Formation l1Math = findOrCreateFormation("Licence 1 Mathématiques", math, 1);
        Formation l2Math = findOrCreateFormation("Licence 2 Mathématiques", math, 2);
        Formation l3Math = findOrCreateFormation("Licence 3 Mathématiques", math, 3);

        // Formations en Physique
        Formation l1Physique = findOrCreateFormation("Licence 1 Physique", physique, 1);
        Formation l2Physique = findOrCreateFormation("Licence 2 Physique", physique, 2);

        // ========================================
        // 3) UTILISATEURS
        // ========================================

        // Admin
        createUserIfMissing("admin@university.com", "Admin", "Système", Role.ADMIN, null, null, encodedPassword);

        // Secrétariat
        createUserIfMissing("secretariat@university.com", "Dupuis", "Isabelle", Role.SECRETARIAT, "SEC001", null, encodedPassword);

        // Chefs de département
        createUserIfMissing("chef.info@university.com", "Dubois", "Pierre", Role.CHEF_DEPARTEMENT, "CD001", info, encodedPassword);
        createUserIfMissing("chef.math@university.com", "Lefebvre", "Marie", Role.CHEF_DEPARTEMENT, "CD002", math, encodedPassword);
        createUserIfMissing("chef.physique@university.com", "Moreau", "Jacques", Role.CHEF_DEPARTEMENT, "CD003", physique, encodedPassword);

        // Enseignants en Informatique
        User enseignant1 = createUserIfMissing("sophie.martin@university.com", "Martin", "Sophie", Role.ENSEIGNANT, "ENS001", info, encodedPassword);
        User enseignant2 = createUserIfMissing("paul.bernard@university.com", "Bernard", "Paul", Role.ENSEIGNANT, "ENS002", info, encodedPassword);
        User enseignant3 = createUserIfMissing("julie.petit@university.com", "Petit", "Julie", Role.ENSEIGNANT, "ENS003", info, encodedPassword);
        User enseignant4 = createUserIfMissing("marc.robert@university.com", "Robert", "Marc", Role.ENSEIGNANT, "ENS004", info, encodedPassword);

        // Enseignants en Mathématiques
        User enseignantMath1 = createUserIfMissing("claire.dupont@university.com", "Dupont", "Claire", Role.ENSEIGNANT, "ENS005", math, encodedPassword);
        User enseignantMath2 = createUserIfMissing("thomas.rousseau@university.com", "Rousseau", "Thomas", Role.ENSEIGNANT, "ENS006", math, encodedPassword);

        // Enseignants en Physique
        User enseignantPhys1 = createUserIfMissing("anne.laurent@university.com", "Laurent", "Anne", Role.ENSEIGNANT, "ENS007", physique, encodedPassword);

        // Étudiants en L1 Informatique
        createUserIfMissing("lucie.rene@university.com", "René", "Lucie", Role.ETUDIANT, "ETU001", l1Info, encodedPassword);
        createUserIfMissing("anas.khalid@university.com", "Khalid", "Anas", Role.ETUDIANT, "ETU002", l1Info, encodedPassword);
        createUserIfMissing("sara.lemarchand@university.com", "Lemarchand", "Sara", Role.ETUDIANT, "ETU003", l1Info, encodedPassword);
        createUserIfMissing("alex.durand@university.com", "Durand", "Alexandre", Role.ETUDIANT, "ETU004", l1Info, encodedPassword);
        createUserIfMissing("emma.leclerc@university.com", "Leclerc", "Emma", Role.ETUDIANT, "ETU005", l1Info, encodedPassword);

        // Étudiants en L2 Informatique
        createUserIfMissing("maxime.bonnet@university.com", "Bonnet", "Maxime", Role.ETUDIANT, "ETU006", l2Info, encodedPassword);
        createUserIfMissing("lea.francois@university.com", "François", "Léa", Role.ETUDIANT, "ETU007", l2Info, encodedPassword);
        createUserIfMissing("hugo.girard@university.com", "Girard", "Hugo", Role.ETUDIANT, "ETU008", l2Info, encodedPassword);

        // Étudiants en L3 Informatique
        createUserIfMissing("camille.morel@university.com", "Morel", "Camille", Role.ETUDIANT, "ETU009", l3Info, encodedPassword);
        createUserIfMissing("lucas.andre@university.com", "André", "Lucas", Role.ETUDIANT, "ETU010", l3Info, encodedPassword);

        // Étudiants en M1 Informatique
        createUserIfMissing("chloe.blanc@university.com", "Blanc", "Chloé", Role.ETUDIANT, "ETU011", m1Info, encodedPassword);
        createUserIfMissing("arthur.garnier@university.com", "Garnier", "Arthur", Role.ETUDIANT, "ETU012", m1Info, encodedPassword);

        // Étudiants en Mathématiques
        createUserIfMissing("alice.lambert@university.com", "Lambert", "Alice", Role.ETUDIANT, "ETU013", l1Math, encodedPassword);
        createUserIfMissing("theo.martin@university.com", "Martin", "Théo", Role.ETUDIANT, "ETU014", l2Math, encodedPassword);

        // Étudiants en Physique
        createUserIfMissing("marie.thomas@university.com", "Thomas", "Marie", Role.ETUDIANT, "ETU015", l1Physique, encodedPassword);

        // ========================================
        // 4) MATIÈRES
        // ========================================

        // Matières L1 Informatique
        Matiere algo = findOrCreateMatiere("Algorithmique", "INF101", l1Info);
        algo.setEnseignant(enseignant1);
        algo.setDescription("Introduction aux algorithmes et structures de données");
        algo.setCoefficient(2.0);
        algo.setHeuresTotal(48);
        matiereRepository.save(algo);

        Matiere prog = findOrCreateMatiere("Programmation", "INF102", l1Info);
        prog.setEnseignant(enseignant2);
        prog.setDescription("Programmation orientée objet en Java");
        prog.setCoefficient(2.0);
        prog.setHeuresTotal(48);
        matiereRepository.save(prog);

        Matiere archiOrdi = findOrCreateMatiere("Architecture des Ordinateurs", "INF103", l1Info);
        archiOrdi.setEnseignant(enseignant3);
        archiOrdi.setDescription("Architecture matérielle et systèmes informatiques");
        archiOrdi.setCoefficient(1.5);
        archiOrdi.setHeuresTotal(36);
        matiereRepository.save(archiOrdi);

        // Matières L2 Informatique
        Matiere bdd = findOrCreateMatiere("Base de Données", "INF201", l2Info);
        bdd.setEnseignant(enseignant2);
        bdd.setDescription("Conception et gestion de bases de données relationnelles");
        bdd.setCoefficient(2.0);
        bdd.setHeuresTotal(48);
        matiereRepository.save(bdd);

        Matiere web = findOrCreateMatiere("Développement Web", "INF202", l2Info);
        web.setEnseignant(enseignant4);
        web.setDescription("Technologies web modernes (HTML, CSS, JavaScript, React)");
        web.setCoefficient(2.0);
        web.setHeuresTotal(48);
        matiereRepository.save(web);

        Matiere reseaux = findOrCreateMatiere("Réseaux Informatiques", "INF203", l2Info);
        reseaux.setEnseignant(enseignant3);
        reseaux.setDescription("Fondamentaux des réseaux et protocoles");
        reseaux.setCoefficient(1.5);
        reseaux.setHeuresTotal(36);
        matiereRepository.save(reseaux);

        // Matières L3 Informatique
        Matiere ia = findOrCreateMatiere("Intelligence Artificielle", "INF301", l3Info);
        ia.setEnseignant(enseignant1);
        ia.setDescription("Introduction à l'IA et au machine learning");
        ia.setCoefficient(2.5);
        ia.setHeuresTotal(48);
        matiereRepository.save(ia);

        Matiere genielog = findOrCreateMatiere("Génie Logiciel", "INF302", l3Info);
        genielog.setEnseignant(enseignant4);
        genielog.setDescription("Méthodologies de développement logiciel");
        genielog.setCoefficient(2.0);
        genielog.setHeuresTotal(48);
        matiereRepository.save(genielog);

        // Matières M1 Informatique
        Matiere cloudComp = findOrCreateMatiere("Cloud Computing", "INF401", m1Info);
        cloudComp.setEnseignant(enseignant2);
        cloudComp.setDescription("Technologies cloud et architectures distribuées");
        cloudComp.setCoefficient(3.0);
        cloudComp.setHeuresTotal(48);
        matiereRepository.save(cloudComp);

        Matiere securite = findOrCreateMatiere("Sécurité Informatique", "INF402", m1Info);
        securite.setEnseignant(enseignant3);
        securite.setDescription("Cryptographie et sécurité des systèmes");
        securite.setCoefficient(3.0);
        securite.setHeuresTotal(48);
        matiereRepository.save(securite);

        // Matières Mathématiques
        Matiere analyseL1 = findOrCreateMatiere("Analyse Mathématique", "MATH101", l1Math);
        analyseL1.setEnseignant(enseignantMath1);
        analyseL1.setDescription("Fonctions, limites, dérivées et intégrales");
        analyseL1.setCoefficient(2.5);
        analyseL1.setHeuresTotal(48);
        matiereRepository.save(analyseL1);

        Matiere algebreL2 = findOrCreateMatiere("Algèbre Linéaire", "MATH201", l2Math);
        algebreL2.setEnseignant(enseignantMath2);
        algebreL2.setDescription("Espaces vectoriels et applications linéaires");
        algebreL2.setCoefficient(2.5);
        algebreL2.setHeuresTotal(48);
        matiereRepository.save(algebreL2);

        // Matières Physique
        Matiere mecanique = findOrCreateMatiere("Mécanique Classique", "PHYS101", l1Physique);
        mecanique.setEnseignant(enseignantPhys1);
        mecanique.setDescription("Cinématique, dynamique et énergétique");
        mecanique.setCoefficient(2.5);
        mecanique.setHeuresTotal(48);
        matiereRepository.save(mecanique);

        System.out.println("========================================");
        System.out.println("DataInitializer: données initiales créées avec succès!");
        System.out.println("========================================");
        System.out.println("- Départements créés: 3");
        System.out.println("- Formations créées: 10");
        System.out.println("- Utilisateurs créés: 27 (1 admin, 1 secrétariat, 3 chefs dept, 7 enseignants, 15 étudiants)");
        System.out.println("- Matières créées: 15");
        System.out.println("========================================");
        System.out.println("Identifiants de connexion:");
        System.out.println("  Admin: admin@university.com | Mot de passe: password123");
        System.out.println("  Secrétariat: secretariat@university.com | Mot de passe: password123");
        System.out.println("  (Tous les comptes utilisent le mot de passe: password123)");
        System.out.println("========================================");
    }

    // ========================================
    // MÉTHODES HELPER
    // ========================================

    /**
     * Crée un département s'il n'existe pas déjà
     */
    private Departement findOrCreateDepartement(String nom, String description) {
        Optional<Departement> existing = departementRepository.findByNom(nom);
        if (existing.isPresent()) {
            return existing.get();
        }
        Departement dept = new Departement(nom);
        dept.setDescription(description);
        return departementRepository.save(dept);
    }

    /**
     * Crée une formation si elle n'existe pas déjà
     */
    private Formation findOrCreateFormation(String nom, Departement departement, Integer niveau) {
        List<Formation> list = formationRepository.findByDepartementId(departement.getId());
        for (Formation f : list) {
            if (f.getNom().equalsIgnoreCase(nom)) {
                return f;
            }
        }
        Formation formation = new Formation(nom, departement, niveau);
        return formationRepository.save(formation);
    }

    /**
     * Crée une matière si elle n'existe pas déjà
     */
    private Matiere findOrCreateMatiere(String nom, String code, Formation formation) {
        Optional<Matiere> existing = matiereRepository.findByCode(code);
        if (existing.isPresent()) {
            return existing.get();
        }
        Matiere matiere = new Matiere(nom, code, formation);
        return matiereRepository.save(matiere);
    }

    /**
     * Crée un utilisateur s'il n'existe pas déjà
     */
    private User createUserIfMissing(String email, String nom, String prenom, Role role,
                                     String numero, Object optional, String encodedPassword) {
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isPresent()) {
            return existing.get();
        }

        User user = new User();
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotDePasse(encodedPassword);
        user.setRole(role);
        user.setActif(true);

        // Configuration spécifique selon le rôle
        if (role == Role.ENSEIGNANT || role == Role.CHEF_DEPARTEMENT) {
            if (numero != null) {
                user.setNumeroEnseignant(numero);
            }
            if (optional instanceof Departement) {
                user.setDepartement((Departement) optional);
            }
        } else if (role == Role.ETUDIANT) {
            if (numero != null) {
                user.setNumeroEtudiant(numero);
            }
            if (optional instanceof Formation) {
                user.setFormation((Formation) optional);
            }
        }

        return userRepository.save(user);
    }
}
