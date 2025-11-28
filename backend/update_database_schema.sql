-- Script pour ajouter les colonnes manquantes aux tables

-- ============================================
-- TABLE: matieres
-- ============================================
-- Ajoute la colonne type_seance
ALTER TABLE matieres
ADD COLUMN IF NOT EXISTS type_seance VARCHAR(10) NOT NULL DEFAULT 'CM';

-- Ajoute la colonne coefficient
ALTER TABLE matieres
ADD COLUMN IF NOT EXISTS coefficient DOUBLE PRECISION NOT NULL DEFAULT 1.0;

-- Ajoute la colonne heures_total
ALTER TABLE matieres
ADD COLUMN IF NOT EXISTS heures_total INTEGER NOT NULL DEFAULT 0;

-- ============================================
-- TABLE: groupes
-- ============================================
-- Ajoute la colonne capacite_max
ALTER TABLE groupes
ADD COLUMN IF NOT EXISTS capacite_max INTEGER DEFAULT 30;

-- ============================================
-- VÉRIFICATIONS
-- ============================================
-- Vérification des colonnes ajoutées à matieres
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'matieres'
ORDER BY ordinal_position;

-- Vérification des colonnes ajoutées à groupes
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'groupes'
ORDER BY ordinal_position;
