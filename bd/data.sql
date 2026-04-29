-- =========================================================
-- INITIALISATION COMPLÈTE DE LA BASE DE DONNÉES
-- VisaBackOffice - PostgreSQL
-- =========================================================

-- =========================================================
-- 1. TYPES DE VISA
-- =========================================================
INSERT INTO type_visa (libelle, duree_validite_jours)
VALUES ('VISA_TRANSFORMABLE', 90)
ON CONFLICT (libelle) DO NOTHING;

-- =========================================================
-- 2. TYPES DE DEMANDE
-- =========================================================
INSERT INTO type_demande (libelle, necessite_sans_donnees) VALUES
('NOUVEAU_TITRE', false),
('DUPLICATA', true),
('TRANSFERT_VISA', true),
('TRANSFERT_VISA_CARTE_RESIDENT', true)
ON CONFLICT (libelle) DO NOTHING;

-- =========================================================
-- 3. TYPES DE PROFIL
-- =========================================================
INSERT INTO type_profil (libelle) VALUES
('ETUDIANT'),
('TRAVAILLEUR'),
('TOURISTE')
ON CONFLICT (libelle) DO NOTHING;

-- =========================================================
-- 4. NATIONALITÉS
-- =========================================================
INSERT INTO nationalite (libelle) VALUES
('Malagasy'),
('Française'),
('Américaine'),
('Canadienne'),
('Italienne'),
('Suisse'),
('Allemande'),
('Espagnole'),
('Belgique'),
('Britannique')
ON CONFLICT DO NOTHING;

-- =========================================================
-- 5. SITUATIONS FAMILIALES
-- =========================================================
INSERT INTO situation_familiale (libelle) VALUES
('Célibataire'),
('Marié(e)'),
('Divorcé(e)'),
('Veuf/Veuve'),
('Pacsé(e)')
ON CONFLICT DO NOTHING;

-- =========================================================
-- 6. PIÈCES JUSTIFICATIVES - GENERATION AUTOMATIQUE
-- =========================================================

-- Pièces de BASE pour TOUTES les combinaisons (obligatoires)
WITH demands AS (SELECT id, libelle FROM type_demande),
     profiles AS (SELECT id, libelle FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, p.id, piece, true
FROM demands d, profiles p,
(VALUES
    ('Copie passeport'),
    ('Copie visa transformable'),
    ('Acte de naissance')
) AS pieces(piece)
ON CONFLICT DO NOTHING;

-- Pièces OPTIONNELLES pour TOUTES les combinaisons
WITH demands AS (SELECT id FROM type_demande),
     profiles AS (SELECT id FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, p.id, piece, false
FROM demands d, profiles p,
(VALUES
    ('Lettre de motivation'),
    ('Copie diplomes'),
    ('Certificat de bonne conduite'),
    ('Assurance voyage')
) AS pieces(piece)
ON CONFLICT DO NOTHING;

-- Pièces obligatoires ETUDIANT (pour tous types de demande)
WITH demands AS (SELECT id FROM type_demande),
     students AS (SELECT id FROM type_profil WHERE libelle = 'ETUDIANT')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, s.id, piece, true
FROM demands d, students s,
(VALUES
    ('Certificat de scolarite'),
    ('Attestation de prise en charge')
) AS pieces(piece)
ON CONFLICT DO NOTHING;

-- Pièces obligatoires TRAVAILLEUR (pour tous types de demande)
WITH demands AS (SELECT id FROM type_demande),
     workers AS (SELECT id FROM type_profil WHERE libelle = 'TRAVAILLEUR')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, w.id, piece, true
FROM demands d, workers w,
(VALUES
    ('Contrat de travail'),
    ('Attestation employeur')
) AS pieces(piece)
ON CONFLICT DO NOTHING;

-- Pièces optionnelles spécifiques ETUDIANT
WITH demands AS (SELECT id FROM type_demande),
     students AS (SELECT id FROM type_profil WHERE libelle = 'ETUDIANT')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, s.id, piece, false
FROM demands d, students s,
(VALUES
    ('Relevé de notes'),
    ('Lettre de recommandation'),
    ('Plan d''etudes')
) AS pieces(piece)
ON CONFLICT DO NOTHING;

-- Pièces optionnelles spécifiques TRAVAILLEUR
WITH demands AS (SELECT id FROM type_demande),
     workers AS (SELECT id FROM type_profil WHERE libelle = 'TRAVAILLEUR')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, w.id, piece, false
FROM demands d, workers w,
(VALUES
    ('Bulletin de salaire recent'),
    ('Bilan comptable entreprise')
) AS pieces(piece)
ON CONFLICT DO NOTHING;

-- Pièces obligatoires DUPLICATA (pour tous profils)
WITH duplicata AS (SELECT id FROM type_demande WHERE libelle = 'DUPLICATA'),
     profiles AS (SELECT id FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT dup.id, p.id, 'Declaration de perte', true
FROM duplicata dup, profiles p
ON CONFLICT DO NOTHING;

-- Pièces obligatoires TRANSFERT_VISA et TRANSFERT_VISA_CARTE_RESIDENT (pour tous profils)
WITH transferts AS (SELECT id FROM type_demande WHERE libelle IN ('TRANSFERT_VISA', 'TRANSFERT_VISA_CARTE_RESIDENT')),
     profiles AS (SELECT id FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT t.id, p.id, 'Justificatif de transfert', true
FROM transferts t, profiles p
ON CONFLICT DO NOTHING;

-- Pièces optionnelles pour NOUVEAU_TITRE uniquement (tous profils)
WITH nouveau_titre AS (SELECT id FROM type_demande WHERE libelle = 'NOUVEAU_TITRE'),
     profiles AS (SELECT id FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT nt.id, p.id, piece, false
FROM nouveau_titre nt, profiles p,
(VALUES
    ('Justificatif de domicile'),
    ('Photo d''identite couleur')
) AS pieces(piece)
ON CONFLICT DO NOTHING;

-- Pièces optionnelles pour TOURISTE (tous types de demande)
WITH demands AS (SELECT id FROM type_demande),
     tourists AS (SELECT id FROM type_profil WHERE libelle = 'TOURISTE')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, t.id, piece, false
FROM demands d, tourists t,
(VALUES
    ('Itinéraire de voyage'),
    ('Réservation hotel'),
    ('Billet de retour'),
    ('Historique bancaire')
) AS pieces(piece)
ON CONFLICT DO NOTHING;

-- =========================================================
-- RÉSUMÉ DES DONNÉES INSÉRÉES
-- =========================================================
-- ✅ 1 type de visa
-- ✅ 4 types de demande
-- ✅ 3 types de profil
-- ✅ 10 nationalités
-- ✅ 5 situations familiales
-- ✅ ~80 pièces justificatives (combinaisons de types x profils x pièces)
-- =========================================================
