# 📡 API REST Complète - Système de Gestion des Absences

## 🔐 Authentification

Tous les endpoints (sauf `/api/auth/**`) nécessitent un token JWT dans le header :
```
Authorization: Bearer {votre_token_jwt}
```

---

## 1. 🔑 Authentification (`/api/auth`)

### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@university.com",
  "motDePasse": "password123"
}
```

**Réponse:**
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

### Valider Token
```http
GET /api/auth/validate
Authorization: Bearer {token}
```

---

## 2. 👥 Utilisateurs (`/api/users`)

### Créer un utilisateur
```http
POST /api/users
Authorization: Bearer {token}
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@university.com",
  "telephone": "0601020304",
  "motDePasse": "password123",
  "role": "ETUDIANT",
  "numeroEtudiant": "ETU005",
  "formation": { "id": 1 }
}
```

### Obtenir tous les utilisateurs
```http
GET /api/users
```

### Obtenir un utilisateur par ID
```http
GET /api/users/{id}
```

### Obtenir un utilisateur par email
```http
GET /api/users/email/{email}
```

### Obtenir utilisateurs par rôle
```http
GET /api/users/role/ETUDIANT
GET /api/users/role/ENSEIGNANT
GET /api/users/role/ADMIN
```

### Obtenir étudiants d'une formation
```http
GET /api/users/formation/{formationId}/etudiants
```

### Obtenir enseignants d'un département
```http
GET /api/users/departement/{departementId}/enseignants
```

### Mettre à jour un utilisateur
```http
PUT /api/users/{id}
Content-Type: application/json

{
  "nom": "Dupont",
  "prenom": "Jean",
  "email": "jean.dupont@university.com",
  "telephone": "0601020305"
}
```

### Désactiver un utilisateur
```http
PUT /api/users/{id}/deactivate
```

### Supprimer un utilisateur
```http
DELETE /api/users/{id}
```

### Vérifier si email existe
```http
GET /api/users/exists/email/{email}
```

---

## 3. 🏛️ Départements (`/api/departements`)

### Créer un département
```http
POST /api/departements
Content-Type: application/json

{
  "nom": "Informatique",
  "description": "Département des Sciences Informatiques",
  "actif": true
}
```

### Obtenir tous les départements
```http
GET /api/departements
```

### Obtenir départements actifs
```http
GET /api/departements/actifs
```

### Obtenir un département par ID
```http
GET /api/departements/{id}
```

### Mettre à jour un département
```http
PUT /api/departements/{id}
```

### Désactiver un département
```http
PUT /api/departements/{id}/deactivate
```

### Supprimer un département
```http
DELETE /api/departements/{id}
```

---

## 4. 🎓 Formations (`/api/formations`)

### Créer une formation
```http
POST /api/formations
Content-Type: application/json

{
  "nom": "Licence Informatique",
  "description": "Licence en Sciences Informatiques",
  "departement": { "id": 1 },
  "niveau": 3,
  "actif": true
}
```

### Obtenir toutes les formations
```http
GET /api/formations
```

### Obtenir formations actives
```http
GET /api/formations/actives
```

### Obtenir formations d'un département
```http
GET /api/formations/departement/{departementId}
```

### Obtenir formations par niveau
```http
GET /api/formations/niveau/3
```

### Obtenir une formation par ID
```http
GET /api/formations/{id}
```

### Mettre à jour une formation
```http
PUT /api/formations/{id}
```

### Désactiver une formation
```http
PUT /api/formations/{id}/deactivate
```

### Supprimer une formation
```http
DELETE /api/formations/{id}
```

---

## 5. 📚 Matières (`/api/matieres`)

### Créer une matière
```http
POST /api/matieres
Content-Type: application/json

{
  "nom": "Programmation Java",
  "code": "INF301",
  "description": "Cours de programmation orientée objet en Java",
  "formation": { "id": 1 },
  "seuilAbsences": 3,
  "actif": true
}
```

### Obtenir toutes les matières
```http
GET /api/matieres
```

### Obtenir matières actives
```http
GET /api/matieres/actives
```

### Obtenir matières d'une formation
```http
GET /api/matieres/formation/{formationId}
```

### Obtenir une matière par code
```http
GET /api/matieres/code/INF301
```

### Obtenir une matière par ID
```http
GET /api/matieres/{id}
```

### Mettre à jour une matière
```http
PUT /api/matieres/{id}
```

### Désactiver une matière
```http
PUT /api/matieres/{id}/deactivate
```

### Supprimer une matière
```http
DELETE /api/matieres/{id}
```

---

## 6. 👨‍👩‍👧‍👦 Groupes TD/TP (`/api/groupes`)

### Créer un groupe
```http
POST /api/groupes
Content-Type: application/json

{
  "nom": "Groupe A",
  "formation": { "id": 1 },
  "actif": true
}
```

### Obtenir tous les groupes
```http
GET /api/groupes
```

### Obtenir groupes actifs
```http
GET /api/groupes/actifs
```

### Obtenir groupes d'une formation
```http
GET /api/groupes/formation/{formationId}
```

### Obtenir un groupe par ID
```http
GET /api/groupes/{id}
```

### Mettre à jour un groupe
```http
PUT /api/groupes/{id}
```

### Désactiver un groupe
```http
PUT /api/groupes/{id}/deactivate
```

### Supprimer un groupe
```http
DELETE /api/groupes/{id}
```

---

## 7. 🔗 Affectations Groupe-Étudiant (`/api/groupe-etudiants`)

### Affecter un étudiant à un groupe
```http
POST /api/groupe-etudiants/affecter?etudiantId=5&groupeId=1
```

### Retirer un étudiant d'un groupe
```http
DELETE /api/groupe-etudiants/retirer?etudiantId=5&groupeId=1
```

### Obtenir groupes d'un étudiant
```http
GET /api/groupe-etudiants/etudiant/{etudiantId}
```

### Obtenir étudiants d'un groupe
```http
GET /api/groupe-etudiants/groupe/{groupeId}
```

### Obtenir toutes les affectations
```http
GET /api/groupe-etudiants
```

### Obtenir une affectation par ID
```http
GET /api/groupe-etudiants/{id}
```

### Supprimer toutes affectations d'un étudiant
```http
DELETE /api/groupe-etudiants/etudiant/{etudiantId}/all
```

### Supprimer toutes affectations d'un groupe
```http
DELETE /api/groupe-etudiants/groupe/{groupeId}/all
```

### Vérifier si étudiant est dans un groupe
```http
GET /api/groupe-etudiants/verifier?etudiantId=5&groupeId=1
```

---

## 8. 📅 Séances (`/api/seances`)

### Créer une séance
```http
POST /api/seances
Content-Type: application/json

{
  "matiereId": 1,
  "enseignantId": 3,
  "typeSeance": "TD_TP",
  "groupeId": 1,
  "dateDebut": "2024-01-15T10:00:00",
  "dateFin": "2024-01-15T12:00:00",
  "salle": "Salle TP1",
  "commentaire": "Séance de TP Java"
}
```

### Lancer une séance (générer code)
```http
POST /api/seances/{id}/start
```

**Réponse:**
```json
{
  "message": "Séance lancée avec succès",
  "code": "A3X9K2",
  "expiration": "2024-01-15T10:30:30"
}
```

### Renouveler le code dynamique
```http
POST /api/seances/{id}/renew-code
```

### Obtenir le code actuel
```http
GET /api/seances/{id}/code
```

### Arrêter une séance
```http
POST /api/seances/{id}/stop
```

### Obtenir toutes les séances
```http
GET /api/seances
```

### Obtenir une séance par ID
```http
GET /api/seances/{id}
```

### Obtenir séances d'un enseignant
```http
GET /api/seances/enseignant/{enseignantId}
```

### Obtenir séances futures d'un enseignant
```http
GET /api/seances/enseignant/{enseignantId}/upcoming
```

### Obtenir séances d'un groupe
```http
GET /api/seances/groupe/{groupeId}
```

### Annuler une séance
```http
PUT /api/seances/{id}/cancel
```

### Supprimer une séance
```http
DELETE /api/seances/{id}
```

---

## 9. ✅ Présences (`/api/presences`)

### Valider présence avec code (ÉTUDIANT)
```http
POST /api/presences/validate-code
Content-Type: application/json
X-User-Id: 5

{
  "seanceId": 1,
  "code": "A3X9K2"
}
```

### Créer présence manuelle (ENSEIGNANT)
```http
POST /api/presences?seanceId=1&etudiantId=5&statut=PRESENT
```

### Modifier une présence
```http
PUT /api/presences/{id}?statut=RETARD&commentaire=Arrivé%20avec%2010min%20de%20retard
```

### Obtenir présences d'un étudiant
```http
GET /api/presences/etudiant/{etudiantId}
```

### Obtenir présences d'une séance
```http
GET /api/presences/seance/{seanceId}
```

### Obtenir statistiques d'un étudiant
```http
GET /api/presences/statistiques/{etudiantId}
```

**Réponse:**
```json
{
  "etudiantId": 5,
  "nomComplet": "Marie Dubois",
  "totalSeances": 10,
  "totalPresences": 8,
  "totalAbsences": 2,
  "totalRetards": 0,
  "tauxAbsence": 20.0,
  "nombreAvertissements": 0
}
```

### Compter absences par matière
```http
GET /api/presences/absences/count?etudiantId=5&matiereId=1
```

### Supprimer une présence
```http
DELETE /api/presences/{id}
```

---

## 10. 📄 Justificatifs (`/api/justificatifs`)

### Déposer un justificatif
```http
POST /api/justificatifs
Content-Type: multipart/form-data

etudiantId=5
absenceId=10
motif=Certificat médical
fichier=@/path/to/certificat.pdf
```

### Valider un justificatif
```http
PUT /api/justificatifs/{id}/valider?validateurId=2&commentaire=Justificatif%20accepté
```

### Refuser un justificatif
```http
PUT /api/justificatifs/{id}/refuser?validateurId=2&commentaire=Document%20illisible
```

### Télécharger un justificatif
```http
GET /api/justificatifs/{id}/download
```

### Obtenir un justificatif par ID
```http
GET /api/justificatifs/{id}
```

### Obtenir justificatifs d'un étudiant
```http
GET /api/justificatifs/etudiant/{etudiantId}
```

### Obtenir justificatifs en attente
```http
GET /api/justificatifs/en-attente
```

### Obtenir tous les justificatifs
```http
GET /api/justificatifs
```

### Supprimer un justificatif
```http
DELETE /api/justificatifs/{id}
```

---

## 11. ⚠️ Avertissements (`/api/avertissements`)

### Créer un avertissement manuel
```http
POST /api/avertissements?etudiantId=5&matiereId=1&nombreAbsences=5&motif=Trop%20d'absences&createurId=2
```

### Obtenir un avertissement par ID
```http
GET /api/avertissements/{id}
```

### Obtenir tous les avertissements
```http
GET /api/avertissements
```

### Obtenir avertissements d'un étudiant
```http
GET /api/avertissements/etudiant/{etudiantId}
```

### Obtenir avertissements d'une matière
```http
GET /api/avertissements/matiere/{matiereId}
```

### Obtenir avertissements par étudiant et matière
```http
GET /api/avertissements/etudiant/{etudiantId}/matiere/{matiereId}
```

### Obtenir avertissements automatiques
```http
GET /api/avertissements/automatiques
```

### Obtenir avertissements manuels
```http
GET /api/avertissements/manuels
```

### Compter avertissements d'un étudiant
```http
GET /api/avertissements/etudiant/{etudiantId}/count
```

### Modifier le motif
```http
PUT /api/avertissements/{id}/motif?motif=Nouveau%20motif
```

### Supprimer un avertissement
```http
DELETE /api/avertissements/{id}
```

---

## 📊 Récapitulatif des Endpoints

| Module | Nombre d'endpoints |
|--------|-------------------|
| Authentification | 2 |
| Utilisateurs | 11 |
| Départements | 7 |
| Formations | 9 |
| Matières | 9 |
| Groupes | 7 |
| Affectations | 9 |
| Séances | 11 |
| Présences | 8 |
| Justificatifs | 9 |
| Avertissements | 11 |
| **TOTAL** | **93 endpoints** |

---

## 🔒 Codes de Statut HTTP

| Code | Signification |
|------|---------------|
| 200 | OK - Requête réussie |
| 201 | Created - Ressource créée |
| 400 | Bad Request - Données invalides |
| 401 | Unauthorized - Non authentifié |
| 403 | Forbidden - Non autorisé |
| 404 | Not Found - Ressource non trouvée |
| 409 | Conflict - Ressource déjà existante |
| 413 | Payload Too Large - Fichier trop volumineux |
| 500 | Internal Server Error - Erreur serveur |

---

## 🎯 Exemple de Flux Complet

### 1. Connexion Enseignant
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"sophie.martin@university.com","motDePasse":"password123"}'
```

### 2. Lancer Séance
```bash
curl -X POST http://localhost:8080/api/seances/2/start \
  -H "Authorization: Bearer {token}"
```

### 3. Étudiant Valide Présence
```bash
curl -X POST http://localhost:8080/api/presences/validate-code \
  -H "Authorization: Bearer {token_etudiant}" \
  -H "Content-Type: application/json" \
  -H "X-User-Id: 5" \
  -d '{"seanceId":2,"code":"A3X9K2"}'
```

### 4. Arrêter Séance
```bash
curl -X POST http://localhost:8080/api/seances/2/stop \
  -H "Authorization: Bearer {token}"
```

### 5. Consulter Statistiques
```bash
curl -X GET http://localhost:8080/api/presences/statistiques/5 \
  -H "Authorization: Bearer {token}"
```

---

**✅ API REST Complète et Sécurisée !**
