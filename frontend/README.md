# Frontend - Système de Gestion des Absences

Application React avec Vite pour la gestion des absences universitaires.

## Technologies Utilisées

- **React 18** - Framework UI
- **Vite** - Build tool et dev server
- **React Router 6** - Navigation et routing
- **Axios** - Client HTTP pour les appels API
- **Context API** - Gestion de l'état d'authentification

## Structure du Projet

```
frontend/
├── src/
│   ├── components/
│   │   └── layout/
│   │       ├── DashboardLayout.jsx      # Layout réutilisable
│   │       └── DashboardLayout.css
│   ├── context/
│   │   └── AuthContext.jsx              # Context d'authentification
│   ├── pages/
│   │   ├── admin/
│   │   │   ├── DashboardAdmin.jsx       # Dashboard administrateur
│   │   │   └── DashboardAdmin.css
│   │   ├── chef/
│   │   │   ├── DashboardChefDepartement.jsx  # Dashboard chef
│   │   │   └── DashboardChefDepartement.css
│   │   ├── enseignant/
│   │   │   ├── DashboardEnseignant.jsx  # Dashboard enseignant
│   │   │   └── DashboardEnseignant.css
│   │   ├── etudiant/
│   │   │   ├── DashboardEtudiant.jsx    # Dashboard étudiant
│   │   │   └── DashboardEtudiant.css
│   │   ├── Login.jsx                    # Page de connexion
│   │   └── Login.css
│   ├── services/
│   │   └── api.js                       # Service API (93 endpoints)
│   ├── App.jsx                          # Composant principal + routing
│   ├── main.jsx                         # Point d'entrée
│   └── index.css                        # Styles globaux
├── index.html
├── package.json
└── vite.config.js
```

## Installation et Démarrage

### 1. Installer les dépendances
```bash
cd frontend
npm install
```

### 2. Lancer le serveur de développement
```bash
npm run dev
```

L'application sera accessible sur http://localhost:5173

### 3. Build de production
```bash
npm run build
```

## Fonctionnalités par Rôle

### Étudiant
- ✓ Valider sa présence avec un code dynamique
- 📊 Consulter ses statistiques (présences, absences, retards, taux d'absence)
- 📄 Voir la liste de ses absences
- 📎 Déposer des justificatifs d'absence
- 🔔 Voir le statut de ses justificatifs (EN_ATTENTE, VALIDÉ, REFUSÉ)

### Enseignant
- 📅 Créer de nouvelles séances (Cours Magistral ou TD/TP)
- ▶️ Lancer une séance et générer un code dynamique
- 🔄 Code qui se renouvelle automatiquement toutes les 30 secondes
- ⏱️ Affichage du compte à rebours du code
- ⏹️ Arrêter une séance (enregistre automatiquement les absences)
- ✓ Consulter la liste des présences par séance

### Chef de Département
- 📄 Valider ou refuser les justificatifs d'absence
- ⚠️ Consulter tous les avertissements
- 📊 Voir les statistiques du département
- 👥 Gérer la liste des étudiants
- 📈 Consulter les statistiques de chaque étudiant

### Administrateur
- 📊 Vue d'ensemble complète du système
- 👥 CRUD complet des utilisateurs
- 🏛️ CRUD des départements
- 🎓 CRUD des formations
- 📚 Gestion des matières
- 👨‍👩‍👧‍👦 Gestion des groupes
- 📈 Statistiques globales

## Système de Code Dynamique

Le système de code dynamique est au cœur de la validation des présences:

1. L'enseignant lance une séance
2. Un code de 6 caractères alphanumériques est généré (ex: A3X9K2)
3. Le code est affiché en grand sur l'écran de l'enseignant
4. Le code expire après 30 secondes et se renouvelle automatiquement
5. Un compte à rebours visuel montre le temps restant
6. Les étudiants saisissent le code pour valider leur présence
7. Quand l'enseignant arrête la séance, tous les étudiants qui n'ont pas validé sont marqués absents

## Comptes de Test

La page de connexion inclut des boutons de test rapide:

- **Admin**: admin@university.com / password123
- **Enseignant**: sophie.martin@university.com / password123
- **Chef Département**: pierre.dubois@university.com / password123
- **Étudiant**: marie.dubois@university.com / password123

## Configuration API

Le fichier [vite.config.js](vite.config.js#L8-L14) configure le proxy pour le backend:

```javascript
server: {
  proxy: {
    '/api': {
      target: 'http://localhost:8080',
      changeOrigin: true
    }
  }
}
```

Le service API ([src/services/api.js](src/services/api.js)) utilise Axios avec:
- Base URL: http://localhost:8080/api
- Intercepteur pour ajouter automatiquement le token JWT
- Gestion centralisée des erreurs

## Routes de l'Application

```javascript
/ → Redirect vers /login
/login → Page de connexion
/etudiant/dashboard → Dashboard étudiant (protégé)
/enseignant/dashboard → Dashboard enseignant (protégé)
/chef/dashboard → Dashboard chef département (protégé)
/admin/dashboard → Dashboard admin (protégé)
/unauthorized → Page d'accès refusé
```

## Scripts NPM

```json
{
  "dev": "vite",              // Serveur de développement
  "build": "vite build",       // Build de production
  "preview": "vite preview"    // Prévisualiser le build
}
```

## Gestion de l'Authentification

L'authentification est gérée par le Context API ([src/context/AuthContext.jsx](src/context/AuthContext.jsx)):

- Token JWT stocké dans localStorage
- Données utilisateur stockées dans localStorage
- Hooks personnalisés: `useAuth()`
- Méthodes: `login()`, `logout()`, `isAuthenticated()`, `hasRole()`
- Protection des routes avec composant `PrivateRoute`

## Thème et Styles

Variables CSS globales ([src/index.css](src/index.css)):

```css
--primary-color: #4c56ff
--secondary-color: #8b5cf6
--background-color: #f5f7fa
--text-color: #2d3748
```

## Prochaines Améliorations

- [ ] Graphiques pour les statistiques (Chart.js)
- [ ] Notifications en temps réel (WebSocket)
- [ ] Export des données en PDF/Excel
- [ ] Mode sombre
- [ ] Internationalisation (i18n)
- [ ] Tests unitaires (Vitest)
- [ ] Tests E2E (Playwright)

## Support

Pour toute question ou problème, consultez:
- Documentation API: [API_COMPLETE.md](../API_COMPLETE.md)
- Guide de démarrage: [GUIDE_DEMARRAGE.md](../GUIDE_DEMARRAGE.md)
- Structure projet: [STRUCTURE_PROJET.md](../STRUCTURE_PROJET.md)
