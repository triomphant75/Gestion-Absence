# Structure Complète du Projet

## 📂 Architecture des Fichiers

```
attendance-system/
│
├── backend/                                    # API REST Spring Boot
│   ├── src/main/
│   │   ├── java/com/university/attendance/
│   │   │   ├── AttendanceApplication.java    # Classe principale
│   │   │   │
│   │   │   ├── config/                       # Configuration
│   │   │   │   ├── JwtUtil.java             # Utilitaire JWT
│   │   │   │   └── SecurityConfig.java      # Configuration Spring Security
│   │   │   │
│   │   │   ├── controller/                   # REST Controllers
│   │   │   │   ├── AuthController.java      # Authentification
│   │   │   │   ├── SeanceController.java    # Gestion des séances
│   │   │   │   └── PresenceController.java  # Gestion des présences
│   │   │   │
│   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   │   ├── LoginRequest.java
│   │   │   │   ├── LoginResponse.java
│   │   │   │   ├── SeanceDTO.java
│   │   │   │   ├── ValidateCodeRequest.java
│   │   │   │   └── StatistiquesEtudiantDTO.java
│   │   │   │
│   │   │   ├── model/                        # Entités JPA
│   │   │   │   ├── Role.java               # Enum
│   │   │   │   ├── TypeSeance.java         # Enum
│   │   │   │   ├── StatutPresence.java     # Enum
│   │   │   │   ├── StatutJustificatif.java # Enum
│   │   │   │   ├── User.java
│   │   │   │   ├── Departement.java
│   │   │   │   ├── Formation.java
│   │   │   │   ├── Matiere.java
│   │   │   │   ├── Groupe.java
│   │   │   │   ├── GroupeEtudiant.java
│   │   │   │   ├── Seance.java
│   │   │   │   ├── Presence.java
│   │   │   │   ├── Justificatif.java
│   │   │   │   └── Avertissement.java
│   │   │   │
│   │   │   ├── repository/                   # Repositories Spring Data JPA
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── DepartementRepository.java
│   │   │   │   ├── FormationRepository.java
│   │   │   │   ├── MatiereRepository.java
│   │   │   │   ├── GroupeRepository.java
│   │   │   │   ├── GroupeEtudiantRepository.java
│   │   │   │   ├── SeanceRepository.java
│   │   │   │   ├── PresenceRepository.java
│   │   │   │   ├── JustificatifRepository.java
│   │   │   │   └── AvertissementRepository.java
│   │   │   │
│   │   │   └── service/                      # Logique métier
│   │   │       ├── AuthService.java
│   │   │       ├── UserService.java
│   │   │       ├── SeanceService.java
│   │   │       ├── PresenceService.java
│   │   │       └── JustificatifService.java
│   │   │
│   │   └── resources/
│   │       ├── application.properties        # Configuration Spring
│   │       └── data.sql                      # Données de test
│   │
│   └── pom.xml                               # Configuration Maven
│
├── frontend/                                  # Application React (à créer)
│
├── README.md                                  # Documentation principale
├── GUIDE_DEMARRAGE.md                        # Guide de démarrage
├── STRUCTURE_PROJET.md                       # Ce fichier
└── .gitignore                                # Fichiers à ignorer par Git
```

## 🎯 Composants Créés

### ✅ Backend Complet

#### 1. **Modèle de Données** (10 entités + 4 énumérations)
- ✅ User (utilisateurs multi-rôles)
- ✅ Departement
- ✅ Formation
- ✅ Matiere (avec seuil d'absences)
- ✅ Groupe (TD/TP)
- ✅ GroupeEtudiant (liaison)
- ✅ Seance (avec code dynamique)
- ✅ Presence (avec horodatage)
- ✅ Justificatif (avec upload fichier)
- ✅ Avertissement (automatique/manuel)

#### 2. **Repositories** (10 interfaces)
- Toutes les méthodes CRUD standard
- Requêtes personnalisées avec @Query
- Méthodes de comptage et statistiques

#### 3. **Services** (5 services)
- ✅ **AuthService** : Authentification JWT
- ✅ **UserService** : Gestion des utilisateurs
- ✅ **SeanceService** : Gestion des séances et code dynamique
- ✅ **PresenceService** : Validation et statistiques
- ✅ **JustificatifService** : Upload et validation

#### 4. **Controllers REST** (3 controllers)
- ✅ **AuthController** : Login et validation token
- ✅ **SeanceController** : CRUD séances + gestion code dynamique
- ✅ **PresenceController** : Pointage et statistiques

#### 5. **Configuration**
- ✅ **JwtUtil** : Génération et validation JWT
- ✅ **SecurityConfig** : Spring Security + CORS
- ✅ **application.properties** : Configuration PostgreSQL
- ✅ **data.sql** : Données de test

## 🔐 Système de Sécurité

### JWT (JSON Web Token)
- Token généré à la connexion
- Expiration configurable (24h par défaut)
- Contient : userId, email, role

### Spring Security
- Endpoints publics : `/api/auth/**`
- Tous les autres endpoints requièrent authentification (en développement : ouvert)
- CORS activé pour localhost:3000 et localhost:5173

## 🔄 Flux du Système de Pointage

```
┌─────────────────────────────────────────────────────────────┐
│                    FLUX DE POINTAGE                          │
└─────────────────────────────────────────────────────────────┘

1. ENSEIGNANT                           2. SYSTÈME
   └─> Lance la séance                     └─> Génère code (6 chars)
       POST /seances/{id}/start                Ex: "A3X9K2"
                                               Expiration: +30 secondes

3. ÉTUDIANTS                            4. SYSTÈME
   └─> Saisit le code                      └─> Vérifie :
       POST /presences/validate-code            • Code valide ?
       + userId + seanceId + code               • Non expiré ?
                                                • Étudiant inscrit ?
                                            └─> Enregistre présence
                                                + Horodatage

5. SYSTÈME (auto)                       6. ENSEIGNANT
   └─> Renouvelle code                     └─> Arrête la séance
       Toutes les 30 secondes                  POST /seances/{id}/stop
       (peut être manuel ou auto)

7. SYSTÈME                              8. RÉSULTAT
   └─> Enregistre absences                 └─> Présences/Absences
       Pour étudiants non-pointés              + Vérification seuils
                                               + Génération avertissements
```

## 📊 Endpoints API Complets

### 🔐 Authentification (`/api/auth`)
| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| POST | `/login` | Connexion | ❌ |
| GET | `/validate` | Valider token | ❌ |

### 🎓 Séances (`/api/seances`)
| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| GET | `/` | Liste des séances | ✅ |
| POST | `/` | Créer séance | ✅ |
| GET | `/{id}` | Détail séance | ✅ |
| POST | `/{id}/start` | Lancer séance | ✅ |
| POST | `/{id}/stop` | Arrêter séance | ✅ |
| POST | `/{id}/renew-code` | Renouveler code | ✅ |
| GET | `/{id}/code` | Code actuel | ✅ |
| GET | `/enseignant/{id}` | Séances enseignant | ✅ |
| GET | `/enseignant/{id}/upcoming` | Séances futures | ✅ |
| GET | `/groupe/{id}` | Séances groupe | ✅ |
| PUT | `/{id}/cancel` | Annuler séance | ✅ |
| DELETE | `/{id}` | Supprimer séance | ✅ |

### ✔️ Présences (`/api/presences`)
| Méthode | Endpoint | Description | Auth |
|---------|----------|-------------|------|
| POST | `/validate-code` | Valider présence par code | ✅ |
| POST | `/` | Créer présence manuelle | ✅ |
| PUT | `/{id}` | Modifier présence | ✅ |
| GET | `/etudiant/{id}` | Présences d'un étudiant | ✅ |
| GET | `/seance/{id}` | Présences d'une séance | ✅ |
| GET | `/statistiques/{id}` | Stats étudiant | ✅ |
| GET | `/absences/count` | Compter absences | ✅ |
| DELETE | `/{id}` | Supprimer présence | ✅ |

## 🧪 Données de Test

### Comptes Prêts à Utiliser
**Tous les mots de passe : `password123`**

```
📧 admin@university.com           [ADMIN]
📧 chef.info@university.com       [CHEF_DEPARTEMENT]
📧 sophie.martin@university.com   [ENSEIGNANT]
📧 pierre.bernard@university.com  [ENSEIGNANT]
📧 marie.dubois@student.university.com   [ETUDIANT - Groupe A]
📧 luc.petit@student.university.com      [ETUDIANT - Groupe A]
📧 julie.moreau@student.university.com   [ETUDIANT - Groupe B]
📧 thomas.simon@student.university.com   [ETUDIANT - Groupe B]
```

## 🎨 Prochaines Étapes

### Phase 1 : Backend (✅ TERMINÉ)
- [x] Architecture MVC
- [x] Entités et relations
- [x] Repositories avec requêtes personnalisées
- [x] Services métier
- [x] API REST complète
- [x] Sécurité JWT
- [x] Système de code dynamique
- [x] Données de test

### Phase 2 : Frontend React (À FAIRE)
- [ ] Configuration React + Router
- [ ] Services API (Axios)
- [ ] Context d'authentification
- [ ] Dashboard layout (Navbar + Sidebar)
- [ ] Pages par rôle :
  - [ ] Login
  - [ ] Dashboard Étudiant
  - [ ] Dashboard Enseignant
  - [ ] Dashboard Chef Département
  - [ ] Dashboard Admin
- [ ] Composants réutilisables

### Phase 3 : Fonctionnalités Avancées
- [ ] Renouvellement automatique du code (WebSocket/SSE)
- [ ] Upload de justificatifs
- [ ] Validation des justificatifs
- [ ] Statistiques avancées
- [ ] Export PDF/Excel
- [ ] Notifications email

## 💡 Points Clés

### Code Dynamique
- **Longueur** : 6 caractères alphanumériques
- **Validité** : 30 secondes
- **Renouvellement** : Manuel ou automatique
- **Sécurité** : Vérifie l'inscription de l'étudiant

### Gestion des Absences
- Enregistrement automatique à la fin de la séance
- Vérification du seuil par matière
- Génération automatique d'avertissements

### Roles et Permissions
- **ETUDIANT** : Consulter, valider présence
- **ENSEIGNANT** : Gérer séances, modifier présences
- **CHEF_DEPARTEMENT** : Valider justificatifs, stats département
- **ADMIN** : Accès complet

## 📞 Commandes Utiles

```bash
# Démarrer le backend
cd backend
mvn spring-boot:run

# Compiler
mvn clean install

# Tests
mvn test

# Vérifier PostgreSQL
psql -U postgres -d attendance_db

# Voir les logs
tail -f backend/logs/application.log
```

## 📚 Ressources

- **Spring Boot** : https://spring.io/projects/spring-boot
- **Spring Data JPA** : https://spring.io/projects/spring-data-jpa
- **JWT** : https://jwt.io/
- **PostgreSQL** : https://www.postgresql.org/docs/

---

**Projet créé avec ❤️ pour la gestion des absences universitaires**
