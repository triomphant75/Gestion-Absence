# Guide de Démarrage Rapide

## 📦 Prérequis

- **Java 17** ou supérieur
- **PostgreSQL 14+**
- **Maven 3.8+**
- **Node.js 18+** (pour le frontend plus tard)

## 🔧 Configuration de la Base de Données

### 1. Créer la base de données PostgreSQL

```sql
-- Connectez-vous à PostgreSQL
psql -U postgres

-- Créez la base de données
CREATE DATABASE attendance_db;

-- Créez un utilisateur (optionnel)
CREATE USER attendance_user WITH PASSWORD 'votre_mot_de_passe';
GRANT ALL PRIVILEGES ON DATABASE attendance_db TO attendance_user;
```

### 2. Configurer application.properties

Éditez `backend/src/main/resources/application.properties` :

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/attendance_db
spring.datasource.username=postgres
spring.datasource.password=votre_mot_de_passe
```

## 🚀 Démarrage du Backend

### Option 1 : Via Maven

```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### Option 2 : Via votre IDE

1. Importez le projet Maven dans votre IDE (IntelliJ IDEA, Eclipse, VS Code)
2. Attendez que Maven télécharge les dépendances
3. Exécutez la classe principale : `AttendanceApplication.java`

Le serveur démarre sur **http://localhost:8080**

## 📊 Données de Test

Le système charge automatiquement des données de test au démarrage :

### Comptes Utilisateurs

**Mot de passe pour tous : `password123`**

| Rôle | Email | Description |
|------|-------|-------------|
| **Admin** | admin@university.com | Administrateur système |
| **Chef Département** | chef.info@university.com | Chef du département Informatique |
| **Enseignant** | sophie.martin@university.com | Enseignante |
| **Enseignant** | pierre.bernard@university.com | Enseignant |
| **Étudiant** | marie.dubois@student.university.com | Étudiante - Groupe A |
| **Étudiant** | luc.petit@student.university.com | Étudiant - Groupe A |
| **Étudiant** | julie.moreau@student.university.com | Étudiante - Groupe B |
| **Étudiant** | thomas.simon@student.university.com | Étudiant - Groupe B |

### Données Créées

- 3 Départements (Informatique, Mathématiques, Physique)
- 3 Formations
- 4 Matières
- 3 Groupes TD/TP
- 8 Utilisateurs (1 admin, 1 chef, 2 enseignants, 4 étudiants)
- 3 Séances programmées

## 🧪 Tester l'API avec Postman/cURL

### 1. Connexion (Login)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@university.com",
    "motDePasse": "password123"
  }'
```

**Réponse :**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "id": 1,
  "email": "admin@university.com",
  "nom": "Admin",
  "prenom": "Système",
  "role": "ADMIN"
}
```

Copiez le `token` pour les requêtes suivantes.

### 2. Obtenir toutes les séances

```bash
curl -X GET http://localhost:8080/api/seances \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

### 3. Lancer une séance (générer le code dynamique)

```bash
curl -X POST http://localhost:8080/api/seances/1/start \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

**Réponse :**
```json
{
  "message": "Séance lancée avec succès",
  "code": "A3X9K2",
  "expiration": "2024-01-15T10:30:30"
}
```

### 4. Valider sa présence (étudiant)

D'abord, connectez-vous en tant qu'étudiant :

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "marie.dubois@student.university.com",
    "motDePasse": "password123"
  }'
```

Puis validez la présence avec le code :

```bash
curl -X POST http://localhost:8080/api/presences/validate-code \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer TOKEN_ETUDIANT" \
  -H "X-User-Id: 5" \
  -d '{
    "seanceId": 1,
    "code": "A3X9K2"
  }'
```

### 5. Renouveler le code (toutes les 30 secondes)

```bash
curl -X POST http://localhost:8080/api/seances/1/renew-code \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

### 6. Arrêter la séance

```bash
curl -X POST http://localhost:8080/api/seances/1/stop \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

Cela enregistre automatiquement les absences pour les étudiants qui n'ont pas validé leur présence.

### 7. Voir les statistiques d'un étudiant

```bash
curl -X GET http://localhost:8080/api/presences/statistiques/5 \
  -H "Authorization: Bearer VOTRE_TOKEN"
```

## 🔄 Flux Complet de Test

### Scénario : Une séance de TD avec pointage

1. **Enseignant se connecte**
   ```bash
   # Login enseignant
   POST /api/auth/login
   {
     "email": "sophie.martin@university.com",
     "motDePasse": "password123"
   }
   ```

2. **Enseignant lance la séance**
   ```bash
   POST /api/seances/2/start
   # Retourne le code : "B7Y4M1"
   ```

3. **Étudiants valident leur présence**
   ```bash
   # Marie valide
   POST /api/presences/validate-code
   {
     "seanceId": 2,
     "code": "B7Y4M1"
   }

   # Luc valide aussi
   # ...
   ```

4. **Le code se renouvelle automatiquement** (simulez avec)
   ```bash
   POST /api/seances/2/renew-code
   # Nouveau code : "X9K3P5"
   ```

5. **Enseignant arrête la séance**
   ```bash
   POST /api/seances/2/stop
   # Les absences sont automatiquement enregistrées
   ```

6. **Vérification des présences**
   ```bash
   GET /api/presences/seance/2
   # Liste tous les étudiants avec leur statut
   ```

## 📱 Endpoints API Disponibles

### Authentification
- `POST /api/auth/login` - Connexion
- `GET /api/auth/validate` - Valider un token

### Séances
- `GET /api/seances` - Liste des séances
- `POST /api/seances` - Créer une séance
- `POST /api/seances/{id}/start` - Lancer une séance
- `POST /api/seances/{id}/stop` - Arrêter une séance
- `POST /api/seances/{id}/renew-code` - Renouveler le code
- `GET /api/seances/{id}/code` - Obtenir le code actuel
- `GET /api/seances/enseignant/{id}` - Séances d'un enseignant

### Présences
- `POST /api/presences/validate-code` - Valider présence par code
- `GET /api/presences/etudiant/{id}` - Présences d'un étudiant
- `GET /api/presences/seance/{id}` - Présences d'une séance
- `GET /api/presences/statistiques/{id}` - Statistiques d'un étudiant
- `PUT /api/presences/{id}` - Modifier une présence

## 🐛 Dépannage

### Erreur : "Cannot find symbol Jwts.parser()"

Le code utilise JWT 0.12.3. Si vous avez des erreurs de compilation, assurez-vous que Maven a bien téléchargé les dépendances :

```bash
mvn clean install -U
```

### Erreur de connexion PostgreSQL

Vérifiez que :
1. PostgreSQL est démarré
2. La base `attendance_db` existe
3. Les identifiants dans `application.properties` sont corrects

### Le port 8080 est déjà utilisé

Changez le port dans `application.properties` :
```properties
server.port=8081
```

## 📝 Prochaines Étapes

1. ✅ Backend fonctionnel avec API REST
2. 🔄 Créer le frontend React
3. 🔄 Implémenter le système de renouvellement automatique du code
4. 🔄 Ajouter les justificatifs d'absence
5. 🔄 Créer les dashboards par rôle

## 📞 Support

Pour toute question, consultez le [README.md](README.md) principal.
