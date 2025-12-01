-- Migration: Ajout de la colonne statut dans la table seances
-- Date: 2025-12-01
-- Description: Ajoute la colonne statut pour gérer les différents états d'une séance

-- Ajouter la colonne statut avec une valeur par défaut
ALTER TABLE seances
ADD COLUMN IF NOT EXISTS statut VARCHAR(255) NOT NULL DEFAULT 'PREVUE';

-- Mettre à jour les séances existantes en fonction de leur état actuel
UPDATE seances
SET statut = CASE
    WHEN annulee = true THEN 'ANNULEE'
    WHEN terminee = true THEN 'TERMINEE'
    WHEN seance_active = true THEN 'EN_COURS'
    ELSE 'PREVUE'
END
WHERE statut = 'PREVUE';

-- Créer un commentaire sur la colonne pour la documentation
COMMENT ON COLUMN seances.statut IS 'Statut de la séance: PREVUE, REPORTEE, EN_COURS, TERMINEE, ANNULEE';
