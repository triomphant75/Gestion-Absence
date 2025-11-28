# Commandes SQL Utiles pour PostgreSQL

## 🔌 Connexion à la Base de Données

```sql
-- Se connecter à PostgreSQL
psql -U postgres

-- Lister les bases de données
\l

-- Se connecter à notre base
\c attendance_db

-- Lister les tables
\dt

-- Voir la structure d'une table
\d users
\d seances
\d presences
```

## 📊 Requêtes de Vérification

### 1. Vérifier les Utilisateurs

```sql
-- Voir tous les utilisateurs
SELECT id, nom, prenom, email, role
FROM users
ORDER BY role, nom;

-- Compter par rôle
SELECT role, COUNT(*) as nombre
FROM users
GROUP BY role;

-- Voir les étudiants avec leur formation
SELECT u.nom, u.prenom, u.numero_etudiant, f.nom as formation
FROM users u
LEFT JOIN formations f ON u.formation_id = f.id
WHERE u.role = 'ETUDIANT';
```

### 2. Vérifier les Groupes et Affectations

```sql
-- Voir les groupes avec leurs étudiants
SELECT
    g.nom as groupe,
    f.nom as formation,
    COUNT(ge.id) as nombre_etudiants
FROM groupes g
LEFT JOIN formations f ON g.formation_id = f.id
LEFT JOIN groupe_etudiants ge ON g.id = ge.groupe_id
GROUP BY g.nom, f.nom;

-- Détail des affectations
SELECT
    g.nom as groupe,
    u.nom,
    u.prenom,
    u.numero_etudiant
FROM groupe_etudiants ge
JOIN groupes g ON ge.groupe_id = g.id
JOIN users u ON ge.etudiant_id = u.id
ORDER BY g.nom, u.nom;
```

### 3. Vérifier les Séances

```sql
-- Liste des séances programmées
SELECT
    s.id,
    m.nom as matiere,
    u.nom as enseignant,
    s.type_seance,
    g.nom as groupe,
    s.date_debut,
    s.seance_active,
    s.terminee
FROM seances s
JOIN matieres m ON s.matiere_id = m.id
JOIN users u ON s.enseignant_id = u.id
LEFT JOIN groupes g ON s.groupe_id = g.id
ORDER BY s.date_debut;

-- Séances actives
SELECT * FROM seances
WHERE seance_active = true;

-- Séances avec code dynamique
SELECT
    id,
    code_dynamique,
    code_expiration,
    seance_active
FROM seances
WHERE code_dynamique IS NOT NULL;
```

### 4. Vérifier les Présences

```sql
-- Présences d'une séance
SELECT
    u.nom,
    u.prenom,
    p.statut,
    p.heure_validation,
    p.modification_manuelle
FROM presences p
JOIN users u ON p.etudiant_id = u.id
WHERE p.seance_id = 1
ORDER BY u.nom;

-- Statistiques d'un étudiant
SELECT
    u.nom,
    u.prenom,
    COUNT(*) as total_seances,
    SUM(CASE WHEN p.statut = 'PRESENT' THEN 1 ELSE 0 END) as presences,
    SUM(CASE WHEN p.statut = 'ABSENT' THEN 1 ELSE 0 END) as absences,
    SUM(CASE WHEN p.statut = 'RETARD' THEN 1 ELSE 0 END) as retards,
    ROUND(
        SUM(CASE WHEN p.statut = 'ABSENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(*),
        2
    ) as taux_absence
FROM presences p
JOIN users u ON p.etudiant_id = u.id
WHERE u.id = 5
GROUP BY u.id, u.nom, u.prenom;
```

### 5. Vérifier les Avertissements

```sql
-- Liste des avertissements
SELECT
    u.nom,
    u.prenom,
    m.nom as matiere,
    a.nombre_absences,
    a.automatique,
    a.date_avertissement
FROM avertissements a
JOIN users u ON a.etudiant_id = u.id
JOIN matieres m ON a.matiere_id = m.id
ORDER BY a.date_avertissement DESC;

-- Étudiants avec avertissements
SELECT
    u.nom,
    u.prenom,
    COUNT(a.id) as nombre_avertissements
FROM users u
LEFT JOIN avertissements a ON u.id = a.etudiant_id
WHERE u.role = 'ETUDIANT'
GROUP BY u.id, u.nom, u.prenom
HAVING COUNT(a.id) > 0;
```

## 🧪 Requêtes de Test

### Simuler un Scénario Complet

```sql
-- 1. Vérifier qu'une séance existe
SELECT * FROM seances WHERE id = 2;

-- 2. Lancer la séance (simulé - normalement fait via API)
UPDATE seances
SET seance_active = true,
    code_dynamique = 'TEST01',
    code_expiration = NOW() + INTERVAL '30 seconds'
WHERE id = 2;

-- 3. Créer une présence pour un étudiant
INSERT INTO presences (seance_id, etudiant_id, statut, heure_validation, modification_manuelle, created_at, updated_at)
VALUES (2, 5, 'PRESENT', NOW(), false, NOW(), NOW());

-- 4. Vérifier les présences
SELECT * FROM presences WHERE seance_id = 2;

-- 5. Arrêter la séance
UPDATE seances
SET seance_active = false,
    terminee = true,
    code_dynamique = NULL,
    code_expiration = NULL
WHERE id = 2;

-- 6. Créer les absences pour ceux qui n'ont pas pointé
INSERT INTO presences (seance_id, etudiant_id, statut, modification_manuelle, created_at, updated_at)
SELECT
    2 as seance_id,
    ge.etudiant_id,
    'ABSENT' as statut,
    false as modification_manuelle,
    NOW() as created_at,
    NOW() as updated_at
FROM groupe_etudiants ge
WHERE ge.groupe_id = (SELECT groupe_id FROM seances WHERE id = 2)
  AND ge.etudiant_id NOT IN (
      SELECT etudiant_id FROM presences WHERE seance_id = 2
  );
```

## 🔧 Requêtes de Maintenance

### Nettoyer les Données de Test

```sql
-- Supprimer toutes les présences
TRUNCATE TABLE presences CASCADE;

-- Supprimer tous les avertissements
TRUNCATE TABLE avertissements CASCADE;

-- Supprimer toutes les séances
TRUNCATE TABLE seances CASCADE;

-- Réinitialiser les séquences
ALTER SEQUENCE presences_id_seq RESTART WITH 1;
ALTER SEQUENCE avertissements_id_seq RESTART WITH 1;
ALTER SEQUENCE seances_id_seq RESTART WITH 1;
```

### Réinitialiser Complètement

```sql
-- ATTENTION : Supprime TOUTES les données
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO public;

-- Redémarrer l'application pour recréer les tables
```

## 📈 Requêtes de Statistiques Avancées

### Taux de Présence par Matière

```sql
SELECT
    m.nom as matiere,
    COUNT(DISTINCT s.id) as nombre_seances,
    COUNT(p.id) as total_pointages,
    SUM(CASE WHEN p.statut = 'PRESENT' THEN 1 ELSE 0 END) as presents,
    SUM(CASE WHEN p.statut = 'ABSENT' THEN 1 ELSE 0 END) as absents,
    ROUND(
        SUM(CASE WHEN p.statut = 'PRESENT' THEN 1 ELSE 0 END) * 100.0 / NULLIF(COUNT(p.id), 0),
        2
    ) as taux_presence
FROM matieres m
LEFT JOIN seances s ON m.id = s.matiere_id
LEFT JOIN presences p ON s.id = p.seance_id
GROUP BY m.id, m.nom;
```

### Top 5 Étudiants Assidus

```sql
SELECT
    u.nom,
    u.prenom,
    COUNT(*) as total_seances,
    SUM(CASE WHEN p.statut = 'PRESENT' THEN 1 ELSE 0 END) as presences,
    ROUND(
        SUM(CASE WHEN p.statut = 'PRESENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(*),
        2
    ) as taux_presence
FROM users u
JOIN presences p ON u.id = p.etudiant_id
WHERE u.role = 'ETUDIANT'
GROUP BY u.id, u.nom, u.prenom
HAVING COUNT(*) >= 5
ORDER BY taux_presence DESC
LIMIT 5;
```

### Étudiants à Risque (Taux d'absence > 20%)

```sql
SELECT
    u.nom,
    u.prenom,
    u.numero_etudiant,
    f.nom as formation,
    COUNT(*) as total_seances,
    SUM(CASE WHEN p.statut = 'ABSENT' THEN 1 ELSE 0 END) as absences,
    ROUND(
        SUM(CASE WHEN p.statut = 'ABSENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(*),
        2
    ) as taux_absence
FROM users u
JOIN presences p ON u.id = p.etudiant_id
LEFT JOIN formations f ON u.formation_id = f.id
WHERE u.role = 'ETUDIANT'
GROUP BY u.id, u.nom, u.prenom, u.numero_etudiant, f.nom
HAVING SUM(CASE WHEN p.statut = 'ABSENT' THEN 1 ELSE 0 END) * 100.0 / COUNT(*) > 20
ORDER BY taux_absence DESC;
```

## 🔍 Requêtes de Débogage

### Vérifier l'Intégrité des Données

```sql
-- Étudiants sans groupe
SELECT u.id, u.nom, u.prenom
FROM users u
WHERE u.role = 'ETUDIANT'
  AND NOT EXISTS (
      SELECT 1 FROM groupe_etudiants ge WHERE ge.etudiant_id = u.id
  );

-- Séances sans présences
SELECT s.id, m.nom, s.date_debut
FROM seances s
JOIN matieres m ON s.matiere_id = m.id
WHERE s.terminee = true
  AND NOT EXISTS (
      SELECT 1 FROM presences p WHERE p.seance_id = s.id
  );

-- Présences sans séance (orphelines)
SELECT p.* FROM presences p
WHERE NOT EXISTS (
    SELECT 1 FROM seances s WHERE s.id = p.seance_id
);
```

## 💾 Export de Données

### Exporter en CSV

```sql
-- Exporter la liste des présences
\copy (SELECT u.nom, u.prenom, s.date_debut, p.statut FROM presences p JOIN users u ON p.etudiant_id = u.id JOIN seances s ON p.seance_id = s.id) TO '/tmp/presences.csv' CSV HEADER;

-- Exporter les statistiques par étudiant
\copy (SELECT u.nom, u.prenom, COUNT(*) as seances, SUM(CASE WHEN p.statut = 'PRESENT' THEN 1 ELSE 0 END) as presences FROM presences p JOIN users u ON p.etudiant_id = u.id GROUP BY u.id, u.nom, u.prenom) TO '/tmp/statistiques.csv' CSV HEADER;
```

## 🔐 Gestion des Permissions

```sql
-- Créer un utilisateur en lecture seule
CREATE USER readonly_user WITH PASSWORD 'password';
GRANT CONNECT ON DATABASE attendance_db TO readonly_user;
GRANT USAGE ON SCHEMA public TO readonly_user;
GRANT SELECT ON ALL TABLES IN SCHEMA public TO readonly_user;

-- Révoquer les permissions
REVOKE ALL ON ALL TABLES IN SCHEMA public FROM readonly_user;
```

---

**Note** : Ces requêtes sont destinées au développement et au débogage. En production, utilisez toujours l'API REST pour manipuler les données.
