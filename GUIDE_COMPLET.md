# Guide Complet - Système de Gestion des Absences

Ce guide vous accompagne étape par étape pour démarrer l'application complète (backend + frontend).

## Prérequis

### Logiciels Nécessaires

- **Java 17** ou supérieur
- **Maven 3.8+**
- **PostgreSQL 14+**
- **Node.js 18+** et **npm**
- **Git** (optionnel)

### Vérification des Versions

```bash
java -version        # Doit afficher Java 17+
mvn -version         # Doit afficher Maven 3.8+
psql --version       # Doit afficher PostgreSQL 14+
node -version        # Doit afficher v18+
npm -version         # Doit afficher 9+
```

## Étape 1: Configuration de la Base de Données

### 1.1 Démarrer PostgreSQL

**Windows:**
```bash
# Via Services Windows ou:
pg_ctl -D "C:\Program Files\PostgreSQL\14\data" start
```

**Linux/Mac:**
```bash
sudo service postgresql start
# ou
brew services start postgresql
```

### 1.2 Créer la Base de Données

```bash
# Se connecter à PostgreSQL
psql -U postgres

# Créer la base de données
CREATE DATABASE attendance_db;

# Vérifier
\l

# Quitter
\q
```

### 1.3 Créer un Utilisateur (Optionnel)

```sql
CREATE USER attendance_user WITH PASSWORD 'votre_password';
GRANT ALL PRIVILEGES ON DATABASE attendance_db TO attendance_user;
```

Si vous créez un utilisateur personnalisé, modifiez `backend/src/main/resources/application.properties`:

```properties
spring.datasource.username=attendance_user
spring.datasource.password=votre_password
```

## Étape 2: Démarrer le Backend

### 2.1 Naviguer vers le dossier backend

```bash
cd backend
```

### 2.2 Installer les dépendances Maven

```bash
mvn clean install
```

Cette commande va:
- Télécharger toutes les dépendances
- Compiler le code
- Créer le fichier JAR

### 2.3 Lancer l'application Spring Boot

**Option 1: Avec Maven**
```bash
mvn spring-boot:run
```

**Option 2: Avec Java**
```bash
mvn package
java -jar target/attendance-system-1.0.0.jar
```

### 2.4 Vérifier le Backend

Le backend devrait démarrer sur http://localhost:8080

Vérifiez dans les logs:
```
Started AttendanceSystemApplication in X.XXX seconds
```

### 2.5 Tester l'API

```bash
# Test de connexion (compte admin par défaut créé au démarrage)
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@university.com",
    "motDePasse": "password123"
  }'
```

Si vous recevez un token JWT, le backend fonctionne correctement!

## Étape 3: Démarrer le Frontend

### 3.1 Ouvrir un nouveau terminal

Gardez le terminal du backend ouvert, ouvrez-en un nouveau.

### 3.2 Naviguer vers le dossier frontend

```bash
cd frontend
```

### 3.3 Installer les dépendances npm

```bash
npm install
```

Cette commande va installer:
- React 18
- React Router 6
- Axios
- Vite
- Et toutes les autres dépendances

### 3.4 Lancer le serveur de développement

```bash
npm run dev
```

### 3.5 Accéder à l'application

Ouvrez votre navigateur et allez sur:
```
http://localhost:5173
```

Vous devriez voir la page de connexion!

## Étape 4: Connexion et Test

### 4.1 Comptes de Test Disponibles

L'application crée automatiquement 4 comptes de test au démarrage:

| Rôle | Email | Mot de Passe | Fonctionnalités |
|------|-------|--------------|-----------------|
| **Admin** | admin@university.com | password123 | Gestion complète du système |
| **Enseignant** | sophie.martin@university.com | password123 | Créer séances, gérer code dynamique |
| **Chef Département** | pierre.dubois@university.com | password123 | Valider justificatifs, voir avertissements |
| **Étudiant** | marie.dubois@university.com | password123 | Valider présence, déposer justificatifs |

### 4.2 Test du Flux Complet

#### Scénario: Validation de Présence

1. **Connexion Enseignant**
   - Utilisez sophie.martin@university.com / password123
   - Vous arrivez sur le dashboard enseignant

2. **Créer une Séance**
   - Cliquez sur "Créer Séance"
   - Remplissez le formulaire:
     - Matière: Choisir dans la liste
     - Type: Cours Magistral ou TD/TP
     - Date/Heure: Aujourd'hui
     - Salle: Amphi A
   - Cliquez "Créer la Séance"

3. **Lancer la Séance**
   - Retournez sur "Mes Séances"
   - Cliquez "Lancer la Séance"
   - Un code de 6 caractères apparaît (ex: A3X9K2)
   - Le code change automatiquement toutes les 30 secondes

4. **Validation Étudiant**
   - Dans un autre onglet, connectez-vous avec marie.dubois@university.com
   - Allez sur "Valider Présence"
   - Entrez l'ID de la séance (visible dans l'URL ou la liste)
   - Entrez le code affiché par l'enseignant
   - Cliquez "Valider ma Présence"

5. **Arrêter la Séance**
   - Retournez sur l'onglet enseignant
   - Cliquez "Arrêter la Séance"
   - Les étudiants qui n'ont pas validé sont marqués absents automatiquement

6. **Vérification**
   - Sur l'onglet étudiant, allez sur "Mes Statistiques"
   - Vous devriez voir vos présences mises à jour

## Étape 5: Exploration des Fonctionnalités

### Dashboard Admin

1. Connectez-vous avec admin@university.com
2. Explorez:
   - Vue d'ensemble avec statistiques globales
   - Création d'utilisateurs
   - Gestion des départements
   - Gestion des formations
   - Consultation des données

### Dashboard Chef de Département

1. Connectez-vous avec pierre.dubois@university.com
2. Testez:
   - Validation de justificatifs
   - Consultation des avertissements
   - Vue des statistiques département
   - Liste des étudiants

### Dashboard Enseignant

1. Connectez-vous avec sophie.martin@university.com
2. Utilisez:
   - Création de séances
   - Système de code dynamique
   - Consultation des présences

### Dashboard Étudiant

1. Connectez-vous avec marie.dubois@university.com
2. Accédez:
   - Validation de présence
   - Statistiques personnelles
   - Liste des absences
   - Dépôt de justificatifs

## Étape 6: Arrêt de l'Application

### Arrêter le Frontend

Dans le terminal du frontend:
```bash
Ctrl + C
```

### Arrêter le Backend

Dans le terminal du backend:
```bash
Ctrl + C
```

### Arrêter PostgreSQL (Optionnel)

```bash
# Windows
pg_ctl -D "C:\Program Files\PostgreSQL\14\data" stop

# Linux/Mac
sudo service postgresql stop
# ou
brew services stop postgresql
```

## Résolution des Problèmes Courants

### Problème 1: Port 8080 déjà utilisé

**Erreur:**
```
Port 8080 is already in use
```

**Solution:**
Modifier le port dans `backend/src/main/resources/application.properties`:
```properties
server.port=8081
```

Et dans `frontend/vite.config.js`:
```javascript
target: 'http://localhost:8081'
```

### Problème 2: Erreur de connexion à PostgreSQL

**Erreur:**
```
Connection refused to database
```

**Solutions:**
1. Vérifier que PostgreSQL est démarré
2. Vérifier les credentials dans application.properties
3. Vérifier que la base attendance_db existe

### Problème 3: CORS Error dans le frontend

**Erreur:**
```
Access to XMLHttpRequest has been blocked by CORS policy
```

**Solution:**
Vérifier que le backend est bien démarré sur http://localhost:8080

### Problème 4: Token JWT invalide

**Erreur:**
```
401 Unauthorized
```

**Solution:**
1. Se déconnecter et se reconnecter
2. Vérifier que le token est bien stocké dans localStorage
3. Effacer le localStorage du navigateur (F12 > Application > Local Storage > Clear)

### Problème 5: Dépendances npm non installées

**Erreur:**
```
Cannot find module 'react'
```

**Solution:**
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

## Commandes Utiles

### Backend

```bash
# Nettoyer et recompiler
mvn clean install

# Lancer les tests
mvn test

# Créer le JAR
mvn package

# Lancer avec profil de production
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

### Frontend

```bash
# Installer les dépendances
npm install

# Lancer en dev
npm run dev

# Build de production
npm run build

# Prévisualiser le build
npm run preview

# Nettoyer cache
rm -rf node_modules .vite
npm install
```

### Base de Données

```bash
# Se connecter
psql -U postgres -d attendance_db

# Voir les tables
\dt

# Voir les utilisateurs
SELECT * FROM utilisateurs;

# Voir les séances
SELECT * FROM seances;

# Supprimer toutes les données (ATTENTION!)
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```

## Architecture du Projet

```
newProject/
├── backend/                          # Application Spring Boot
│   ├── src/main/java/.../
│   │   ├── config/                   # JWT, Security
│   │   ├── controller/               # 11 contrôleurs REST
│   │   ├── entity/                   # 10 entités JPA
│   │   ├── repository/               # 10 repositories
│   │   ├── service/                  # 11 services métier
│   │   └── exception/                # Gestion des erreurs
│   ├── src/main/resources/
│   │   └── application.properties    # Configuration
│   └── pom.xml                       # Dépendances Maven
│
├── frontend/                         # Application React
│   ├── src/
│   │   ├── components/layout/        # Layout réutilisable
│   │   ├── context/                  # AuthContext
│   │   ├── pages/                    # 4 dashboards + Login
│   │   ├── services/                 # API service (93 endpoints)
│   │   ├── App.jsx                   # Routing
│   │   └── main.jsx                  # Point d'entrée
│   ├── package.json                  # Dépendances npm
│   └── vite.config.js                # Configuration Vite
│
└── Documentation/
    ├── API_COMPLETE.md               # 93 endpoints documentés
    ├── BACKEND_COMPLET.md            # Résumé backend
    ├── GUIDE_COMPLET.md              # Ce fichier
    └── README.md                     # Vue d'ensemble
```

## Technologies et Versions

### Backend
- Spring Boot 3.2.0
- Java 17
- PostgreSQL 14+
- JWT (jjwt 0.12.3)
- Maven 3.8+

### Frontend
- React 18.2.0
- Vite 5.0.0
- React Router 6.20.1
- Axios 1.6.2
- Node.js 18+

## Prochaines Étapes

1. **Personnaliser les Données**
   - Modifier les comptes de test dans DataInitializer.java
   - Ajouter vos propres départements et formations

2. **Sécurité**
   - Changer la clé JWT dans application.properties
   - Utiliser des mots de passe forts en production

3. **Déploiement**
   - Configurer une base de données de production
   - Build du frontend: `npm run build`
   - Build du backend: `mvn package`
   - Déployer sur un serveur (Heroku, AWS, etc.)

4. **Améliorations**
   - Ajouter des graphiques (Chart.js)
   - Implémenter les notifications temps réel
   - Ajouter l'export PDF/Excel
   - Tests automatisés

## Support et Documentation

- **API Documentation**: [API_COMPLETE.md](API_COMPLETE.md)
- **Backend Details**: [BACKEND_COMPLET.md](BACKEND_COMPLET.md)
- **Frontend README**: [frontend/README.md](frontend/README.md)
- **Project Structure**: [STRUCTURE_PROJET.md](STRUCTURE_PROJET.md)

Bon développement! 🚀
