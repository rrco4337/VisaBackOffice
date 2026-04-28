-- Initialisation des données pour VisaBackOffice
-- PostgreSQL version

-- TYPE VISA
INSERT INTO type_visa (libelle, duree_validite_jours)
VALUES ('VISA_TRANSFORMABLE', 90)
ON CONFLICT DO NOTHING;

-- TYPE DEMANDE
INSERT INTO type_demande (libelle, necessite_sans_donnees) VALUES
('NOUVEAU_TITRE', false),
('DUPLICATA', true),
('TRANSFERT_VISA', true),
('TRANSFERT_VISA_CARTE_RESIDENT', true)
ON CONFLICT DO NOTHING;

-- TYPE PROFIL
INSERT INTO type_profil (libelle) VALUES
('ETUDIANT'),
('TRAVAILLEUR'),
('TOURISTE')
ON CONFLICT DO NOTHING;

-- NATIONALITE
INSERT INTO nationalite (libelle) VALUES
('Malagasy'),
('Française'),
('Américaine'),
('Canadienne'),
('Italienne')
ON CONFLICT DO NOTHING;

-- SITUATION FAMILIALE
INSERT INTO situation_familiale (libelle) VALUES
('Célibataire'),
('Marié(e)'),
('Divorcé(e)'),
('Veuf/Veuve')
ON CONFLICT DO NOTHING;

-- PIECES JUSTIFICATIVES
-- Pour chaque combinaison (type_demande, type_profil)

-- Helper: Créer les pièces de base pour toutes les combinaisons
WITH demands AS (SELECT id, libelle FROM type_demande),
     profiles AS (SELECT id, libelle FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, p.id, piece, oblig
FROM demands d, profiles p,
(VALUES
    ('Copie passeport', true),
    ('Copie visa transformable', true),
    ('Acte de naissance', true),
    ('Lettre de motivation', false),
    ('Copie diplomes', false),
    ('Certificat de bonne conduite', false),
    ('Assurance voyage', false)
) AS pieces(piece, oblig)
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE pj.type_demande_id = d.id
    AND pj.type_profil_id = p.id
    AND pj.libelle = piece
);

-- Pièces spécifiques aux étudiants (obligatoires)
WITH demands AS (SELECT id FROM type_demande),
     etudiant_profile AS (SELECT id FROM type_profil WHERE libelle = 'ETUDIANT')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, ep.id, piece, true
FROM demands d, etudiant_profile ep,
(VALUES
    ('Certificat de scolarite'),
    ('Attestation de prise en charge')
) AS pieces(piece)
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE pj.type_demande_id = d.id
    AND pj.type_profil_id = ep.id
    AND pj.libelle = piece
);

-- Pièces spécifiques aux travailleurs (obligatoires)
WITH demands AS (SELECT id FROM type_demande),
     travailleur_profile AS (SELECT id FROM type_profil WHERE libelle = 'TRAVAILLEUR')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, tp.id, piece, true
FROM demands d, travailleur_profile tp,
(VALUES
    ('Contrat de travail'),
    ('Attestation employeur')
) AS pieces(piece)
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE pj.type_demande_id = d.id
    AND pj.type_profil_id = tp.id
    AND pj.libelle = piece
);

-- Pièces optionnelles spécifiques ETUDIANT
WITH demands AS (SELECT id FROM type_demande),
     etudiant_profile AS (SELECT id FROM type_profil WHERE libelle = 'ETUDIANT')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, ep.id, piece, false
FROM demands d, etudiant_profile ep,
(VALUES
    ('Relevé de notes'),
    ('Lettre de recommandation'),
    ('Plan d''etudes')
) AS pieces(piece)
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE pj.type_demande_id = d.id
    AND pj.type_profil_id = ep.id
    AND pj.libelle = piece
);

-- Pièces optionnelles spécifiques TRAVAILLEUR
WITH demands AS (SELECT id FROM type_demande),
     travailleur_profile AS (SELECT id FROM type_profil WHERE libelle = 'TRAVAILLEUR')
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT d.id, tp.id, piece, false
FROM demands d, travailleur_profile tp,
(VALUES
    ('Bulletin de salaire recent'),
    ('Bilan comptable entreprise')
) AS pieces(piece)
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE pj.type_demande_id = d.id
    AND pj.type_profil_id = tp.id
    AND pj.libelle = piece
);

-- Pièces spécifiques DUPLICATA
WITH duplicata AS (SELECT id FROM type_demande WHERE libelle = 'DUPLICATA'),
     profiles AS (SELECT id FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT dup.id, p.id, 'Declaration de perte', true
FROM duplicata dup, profiles p
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE pj.type_demande_id = dup.id
    AND pj.type_profil_id = p.id
    AND pj.libelle = 'Declaration de perte'
);

-- Pièces spécifiques TRANSFERT_VISA et TRANSFERT_VISA_CARTE_RESIDENT
WITH transferts AS (SELECT id FROM type_demande WHERE libelle IN ('TRANSFERT_VISA', 'TRANSFERT_VISA_CARTE_RESIDENT')),
     profiles AS (SELECT id FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT t.id, p.id, 'Justificatif de transfert', true
FROM transferts t, profiles p
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE pj.type_demande_id = t.id
    AND pj.type_profil_id = p.id
    AND pj.libelle = 'Justificatif de transfert'
);

-- Pièces optionnelles spécifiques NOUVEAU_TITRE
WITH nouveau_titre AS (SELECT id FROM type_demande WHERE libelle = 'NOUVEAU_TITRE'),
     profiles AS (SELECT id FROM type_profil)
INSERT INTO piece_justificative (type_demande_id, type_profil_id, libelle, obligatoire)
SELECT nt.id, p.id, piece, false
FROM nouveau_titre nt, profiles p,
(VALUES
    ('Justificatif de domicile'),
    ('Photo d''identite couleur')
) AS pieces(piece)
WHERE NOT EXISTS (
    SELECT 1 FROM piece_justificative pj
    WHERE pj.type_demande_id = nt.id
    AND pj.type_profil_id = p.id
    AND pj.libelle = piece
);
