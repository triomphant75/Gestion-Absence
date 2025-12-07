-- Supprimer l'ancienne contrainte
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Ajouter la nouvelle contrainte avec le rôle SECRETARIAT
ALTER TABLE users ADD CONSTRAINT users_role_check
CHECK (role IN ('ETUDIANT', 'ENSEIGNANT', 'CHEF_DEPARTEMENT', 'SECRETARIAT', 'ADMIN', 'SUPER_ADMIN'));
