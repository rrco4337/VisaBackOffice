-- TABLE situation_familiale
CREATE TABLE situation_familiale (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR
);

-- TABLE nationalite
CREATE TABLE nationalite (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR
);

-- TABLE demandeur
CREATE TABLE demandeur (
    id SERIAL PRIMARY KEY,
    nom VARCHAR,
    prenom VARCHAR,
    nom_jeune_fille VARCHAR,
    nom_pere VARCHAR,
    date_naissance DATE,
    situation_familiale_id INTEGER REFERENCES situation_familiale(id),
    nationalite_id INTEGER REFERENCES nationalite(id),
    profession VARCHAR,
    adresse TEXT,
    email VARCHAR,
    telephone VARCHAR
);

-- TABLE passeport
CREATE TABLE passeport (
    id SERIAL PRIMARY KEY,
    personne_id INTEGER REFERENCES demandeur(id),
    passeport_precedent_id INTEGER REFERENCES passeport(id),
    numero VARCHAR,
    date_delivrance DATE,
    date_expiration DATE,
    est_actif BOOLEAN
);

-- TABLE type_visa
CREATE TABLE type_visa (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR,
    duree_validite_jours INTEGER
);

-- TABLE visa
CREATE TABLE visa (
    id SERIAL PRIMARY KEY,
    passeport_id INTEGER REFERENCES passeport(id),
    type_visa_id INTEGER REFERENCES type_visa(id),
    numero_visa VARCHAR,
    date_entree DATE,
    lieu_entree VARCHAR,
    date_expiration DATE,
    statut VARCHAR,
    transformable BOOLEAN
);

-- TABLE visa_historique
CREATE TABLE visa_historique (
    id SERIAL PRIMARY KEY,
    visa_id INTEGER REFERENCES visa(id),
    date_changement DATE,
    ancien_statut VARCHAR,
    nouveau_statut VARCHAR,
    motif TEXT
);

-- TABLE type_demande
CREATE TABLE type_demande (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR,
    necessite_sans_donnees BOOLEAN
);

-- TABLE type_profil
CREATE TABLE type_profil (
    id SERIAL PRIMARY KEY,
    libelle VARCHAR
);

-- TABLE demande
CREATE TABLE demande (
    id SERIAL PRIMARY KEY,
    personne_id INTEGER REFERENCES demandeur(id),
    type_demande_id INTEGER REFERENCES type_demande(id),
    type_profil_id INTEGER REFERENCES type_profil(id),
    visa_id INTEGER REFERENCES visa(id),
    demande_originale_id INTEGER REFERENCES demande(id),
    statut VARCHAR,
    sans_donnees BOOLEAN,
    date_demande TIMESTAMP
);

-- TABLE piece_justificative
CREATE TABLE piece_justificative (
    id SERIAL PRIMARY KEY,
    type_demande_id INTEGER REFERENCES type_demande(id),
    type_profil_id INTEGER REFERENCES type_profil(id),
    libelle VARCHAR,
    obligatoire BOOLEAN
);

-- TABLE demande_piece
CREATE TABLE demande_piece (
    id SERIAL PRIMARY KEY,
    demande_id INTEGER REFERENCES demande(id),
    piece_id INTEGER REFERENCES piece_justificative(id),
    fournie BOOLEAN,
    date_fourniture DATE,
    scanned BOOLEAN DEFAULT FALSE,
    date_scan TIMESTAMP
);

-- TABLE titre_sejour
CREATE TABLE titre_sejour (
    id SERIAL PRIMARY KEY,
    personne_id INTEGER REFERENCES demandeur(id),
    demande_id INTEGER REFERENCES demande(id),
    numero VARCHAR,
    type_titre VARCHAR,
    date_delivrance DATE,
    date_expiration DATE
);

-- TABLE fichier_dossier
CREATE TABLE fichier_dossier (
    id SERIAL PRIMARY KEY,
    demande_id INTEGER NOT NULL REFERENCES demande(id) ON DELETE CASCADE,
    piece_id INTEGER REFERENCES piece_justificative(id) ON DELETE CASCADE,
    nom_fichier VARCHAR NOT NULL,
    chemin_fichier VARCHAR NOT NULL,
    taille_fichier BIGINT,
    type_contenu VARCHAR,
    date_upload TIMESTAMP NOT NULL,
    date_modification TIMESTAMP,
    utilisateur_upload VARCHAR
);

CREATE INDEX idx_fichier_demande ON fichier_dossier(demande_id);
CREATE INDEX idx_fichier_piece ON fichier_dossier(piece_id);
CREATE UNIQUE INDEX idx_fichier_demande_piece ON fichier_dossier(demande_id, piece_id);

-- TABLE famille
CREATE TABLE famille (
    id SERIAL PRIMARY KEY,
    personne_id INTEGER REFERENCES demandeur(id),
    nom VARCHAR,
    prenom VARCHAR,
    lien VARCHAR,
    date_naissance DATE
);

COMMENT ON COLUMN passeport.est_actif IS 'Un seul passeport actif par personne';