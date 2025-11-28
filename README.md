# Système de Gestion des Absences Universitaires

Système complet de gestion des présences et absences pour établissements universitaires, développé avec **Spring Boot** (backend) et **React** (frontend).

## 📋 Fonctionnalités Principales

### ✅ Version 1.0 (Implémentée)

- **Gestion des utilisateurs** : Étudiants, Enseignants, Chefs de département, Administration
- **Gestion des séances** : CM et TD/TP avec planification
- **Pointage dynamique** : Code alphanumérique à 6 caractères qui se renouvelle toutes les 30 secondes
- **Gestion des absences** : Enregistrement automatique et modification manuelle
- **Justificatifs** : Dépôt et validation de justificatifs (PDF/images)
- **Avertissements** : Génération automatique selon seuils d'absences
- **Statistiques** : Taux d'absence par étudiant et matière

### 🚀 Fonctionnalités Futures (V2+)

- Prise de présence par QR Code
- Notifications automatiques (email/SMS)
- Rappels pour les justificatifs
- Export PDF/Excel des présences
- Dashboard analytics avancé

## 🏗️ Architecture

```
attendance-system/
├── backend/                    # API REST Spring Boot
│   ├── src/main/java/com/university/attendance/
│   │   ├── model/             # Entités JPA (User, Seance, Presence, etc.)
│   │   ├── repository/        # Repositories Spring Data
│   │   ├── service/           # Logique métier
│   │   ├── controller/        # REST Controllers
│   │   ├── config/            # Configuration (Security, CORS)
│   │   ├── dto/               # Data Transfer Objects
│   │   └── exception/         # Gestion des exceptions
│   └── src/main/resources/
│       └── application.properties
├── frontend/                   # Application React
│   ├── src/
│   │   ├── components/        # Composants réutilisables
│   │   ├── pages/             # Pages principales
│   │   ├── services/          # Services API
│   │   └── context/           # Context (Auth, etc.)
│   └── public/
└── README.md
```

## 🛠️ Technologies Utilisées

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
  - Spring Web (REST API)
  - Spring Data JPA (ORM)
  - Spring Security (Authentification/Autorisation)
  - Spring Validation
- **PostgreSQL** (Base de données)
- **JWT** (JSON Web Tokens pour l'authentification)
- **Maven** (Gestion des dépendances)

### Frontend
- **React 18**
- **React Router** (Navigation)
- **Axios** (Requêtes HTTP)
- **Context API** (Gestion d'état)
- **CSS moderne** (Dashboard responsive)

## 📊 Modèle de Données

### Entités Principales

1. **User** : Utilisateurs (étudiants, enseignants, admins)
2. **Departement** : Départements universitaires
3. **Formation** : Formations (Licence, Master, etc.)
4. **Matiere** : Matières enseignées
5. **Groupe** : Groupes TD/TP
6. **GroupeEtudiant** : Affectation étudiants-groupes
7. **Seance** : Séances de cours
8. **Presence** : Présences/Absences
9. **Justificatif** : Justificatifs d'absence
10. **Avertissement** : Avertissements académiques

### Énumérations

- **Role** : ETUDIANT, ENSEIGNANT, CHEF_DEPARTEMENT, ADMIN, SUPER_ADMIN
- **TypeSeance** : CM, TD_TP
- **StatutPresence** : PRESENT, ABSENT, RETARD
- **StatutJustificatif** : EN_ATTENTE, ACCEPTE, REFUSE

## 🚀 Installation et Configuration

### Prérequis

- Java 17 ou supérieur
- Maven 3.8+
- PostgreSQL 14+
- Node.js 18+ et npm
- Git

### Configuration de la Base de Données

1. Créez une base de données PostgreSQL :

```sql
CREATE DATABASE attendance_db;
```

2. Configurez les identifiants dans `backend/src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/attendance_db
spring.datasource.username=votre_utilisateur
spring.datasource.password=votre_mot_de_passe
```

### Installation du Backend

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Le serveur démarre sur `http://localhost:8080`

### Installation du Frontend

```bash
cd frontend
npm install
npm start
```

L'application démarre sur `http://localhost:3000`

## 🔐 Rôles et Permissions

### Étudiant
- Consulter ses absences et présences
- Voir son taux d'absence
- Saisir sa présence via code dynamique
- Déposer des justificatifs
- Consulter ses avertissements

### Enseignant
- Voir la liste de ses étudiants
- Lancer/arrêter le code dynamique
- Pointer les présences/absences/retards
- Ajouter des commentaires
- Consulter les justificatifs (lecture seule)

### Chef de Département
- Toutes les permissions de l'enseignant
- Valider/refuser les justificatifs
- Modifier les absences
- Gérer les avertissements
- Gérer les matières et groupes
- Consulter les statistiques du département

### Administration/Scolarité
- Toutes les permissions
- Gérer les utilisateurs (CRUD)
- Gérer les formations, matières, groupes
- Gérer les emplois du temps
- Générer toutes les statistiques
- Gestion globale du système

## 📡 API REST - Endpoints Principaux

### Authentification
- `POST /api/auth/login` - Connexion
- `POST /api/auth/register` - Inscription
- `GET /api/auth/me` - Profil utilisateur

### Utilisateurs
- `GET /api/users` - Liste des utilisateurs
- `POST /api/users` - Créer un utilisateur
- `PUT /api/users/{id}` - Modifier un utilisateur
- `DELETE /api/users/{id}` - Supprimer un utilisateur

### Séances
- `GET /api/seances` - Liste des séances
- `POST /api/seances` - Créer une séance
- `POST /api/seances/{id}/start` - Lancer le code dynamique
- `POST /api/seances/{id}/stop` - Arrêter le code dynamique
- `GET /api/seances/{id}/code` - Obtenir le code actuel

### Présences
- `POST /api/presences/validate-code` - Valider sa présence
- `GET /api/presences/etudiant/{id}` - Présences d'un étudiant
- `PUT /api/presences/{id}` - Modifier une présence

### Justificatifs
- `POST /api/justificatifs` - Déposer un justificatif
- `PUT /api/justificatifs/{id}/validate` - Valider un justificatif
- `PUT /api/justificatifs/{id}/reject` - Refuser un justificatif

### Statistiques
- `GET /api/stats/etudiant/{id}` - Stats d'un étudiant
- `GET /api/stats/matiere/{id}` - Stats d'une matière
- `GET /api/stats/groupe/{id}` - Stats d'un groupe

## 🎯 Flux de Pointage

1. **L'enseignant** lance la séance en cliquant sur "Lancer la séance"
2. Le système génère un **code alphanumérique de 6 caractères**
3. Le code se **renouvelle automatiquement toutes les 30 secondes**
4. Les **étudiants** saisissent le code depuis leur espace personnel
5. Chaque validation est **horodatée** et liée à la séance
6. Seuls les étudiants du groupe peuvent valider le code
7. À la **fin de la séance**, l'enseignant arrête le code
8. Les absences sont **automatiquement enregistrées** pour ceux qui n'ont pas validé
9. L'enseignant peut **modifier manuellement** les statuts si nécessaire

## 📈 Gestion des Avertissements

- Chaque matière a un **seuil d'absences** (par défaut : 3)
- Quand un étudiant dépasse ce seuil, un **avertissement automatique** est généré
- Les chefs de département peuvent :
  - Consulter tous les avertissements
  - Modifier ou supprimer un avertissement
  - Créer des avertissements manuels

## 🔄 Prochaines Étapes de Développement

1. ✅ Entités et Repositories créés
2. ⏳ Services et Controllers à créer
3. ⏳ Configuration de la sécurité JWT
4. ⏳ Création du frontend React
5. ⏳ Implémentation du système de code dynamique
6. ⏳ Tests et déploiement

## 📝 Licence

Ce projet est développé pour un usage universitaire.

## 👥 Support

Pour toute question ou problème, contactez l'équipe de développement.
