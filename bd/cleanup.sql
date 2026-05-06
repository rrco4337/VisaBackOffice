-- Active: 1765874928894@@127.0.0.1@5432@visa@public
-- Active: 1776820835603@@127.0.0.1@1433
-- =========================================================
-- NETTOYAGE COMPLET DE LA BASE DE DONNÉES
-- VisaBackOffice - PostgreSQL
-- =========================================================



-- Désactiver les contraintes de clés étrangères temporairement
ALTER TABLE demande_piece DISABLE TRIGGER ALL;
ALTER TABLE demande DISABLE TRIGGER ALL;
ALTER TABLE visa_historique DISABLE TRIGGER ALL;
ALTER TABLE visa DISABLE TRIGGER ALL;
ALTER TABLE titre_sejour DISABLE TRIGGER ALL;
ALTER TABLE passeport DISABLE TRIGGER ALL;
ALTER TABLE piece_justificative DISABLE TRIGGER ALL;
ALTER TABLE famille DISABLE TRIGGER ALL;

-- Nettoyer les données (TRUNCATE est plus rapide que DELETE)
TRUNCATE TABLE demande_piece CASCADE;
TRUNCATE TABLE demande CASCADE;
TRUNCATE TABLE visa_historique CASCADE;
TRUNCATE TABLE visa CASCADE;
TRUNCATE TABLE titre_sejour CASCADE;
TRUNCATE TABLE passeport CASCADE;
TRUNCATE TABLE piece_justificative CASCADE;
TRUNCATE TABLE famille CASCADE;
TRUNCATE TABLE demandeur CASCADE;
TRUNCATE TABLE type_demande CASCADE;
TRUNCATE TABLE type_profil CASCADE;
TRUNCATE TABLE type_visa CASCADE;
TRUNCATE TABLE nationalite CASCADE;
TRUNCATE TABLE situation_familiale CASCADE;

-- Réactiver les contraintes
ALTER TABLE demande_piece ENABLE TRIGGER ALL;
ALTER TABLE demande ENABLE TRIGGER ALL;
ALTER TABLE visa_historique ENABLE TRIGGER ALL;
ALTER TABLE visa ENABLE TRIGGER ALL;
ALTER TABLE titre_sejour ENABLE TRIGGER ALL;
ALTER TABLE passeport ENABLE TRIGGER ALL;
ALTER TABLE piece_justificative ENABLE TRIGGER ALL;
ALTER TABLE famille ENABLE TRIGGER ALL;

-- Réinitialiser les séquences (auto_increment)
ALTER SEQUENCE type_visa_id_seq RESTART WITH 1;
ALTER SEQUENCE type_demande_id_seq RESTART WITH 1;
ALTER SEQUENCE type_profil_id_seq RESTART WITH 1;
ALTER SEQUENCE nationalite_id_seq RESTART WITH 1;
ALTER SEQUENCE situation_familiale_id_seq RESTART WITH 1;
ALTER SEQUENCE demandeur_id_seq RESTART WITH 1;
ALTER SEQUENCE passeport_id_seq RESTART WITH 1;
ALTER SEQUENCE visa_id_seq RESTART WITH 1;
ALTER SEQUENCE visa_historique_id_seq RESTART WITH 1;
ALTER SEQUENCE piece_justificative_id_seq RESTART WITH 1;
ALTER SEQUENCE demande_id_seq RESTART WITH 1;
ALTER SEQUENCE demande_piece_id_seq RESTART WITH 1;
ALTER SEQUENCE titre_sejour_id_seq RESTART WITH 1;
ALTER SEQUENCE famille_id_seq RESTART WITH 1;

-- =========================================================
-- ✅ Base de données nettoyée avec succès!
-- =========================================================
