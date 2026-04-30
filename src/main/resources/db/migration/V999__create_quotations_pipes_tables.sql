-- Migration SQL pour créer les tables Quotations et Pipes
-- Exécutez ce script dans PostgreSQL

-- Table Quotations
CREATE TABLE IF NOT EXISTS quotations (
    id BIGSERIAL PRIMARY KEY,
    annee INTEGER,
    mois VARCHAR(50),
    date_emissions_devis DATE,
    status_projet VARCHAR(100),
    grossiste_operateur VARCHAR(255),
    revendeur VARCHAR(255),
    client_final VARCHAR(255),
    nom_solution VARCHAR(255),
    ref_solution VARCHAR(100),
    quantiter INTEGER,
    duree_annee INTEGER,
    pa_grossiste NUMERIC(10, 2),
    total_ca_potentiel NUMERIC(15, 2),
    date_fin_validite_cotation DATE,
    contact_knox VARCHAR(255),
    kam_end_customer VARCHAR(255),
    rdv_client DATE,
    commentaires VARCHAR(1000),
    created_at DATE,
    updated_at DATE,
    CONSTRAINT pk_quotations PRIMARY KEY (id)
);

-- Table Pipes
CREATE TABLE IF NOT EXISTS pipes (
    id BIGSERIAL PRIMARY KEY,
    annee INTEGER,
    mois VARCHAR(50),
    date_emission_devis DATE,
    statut_projet VARCHAR(100),
    grossiste_operateur VARCHAR(255),
    revendeur VARCHAR(255),
    client_final VARCHAR(255),
    nom_solution VARCHAR(255),
    ref_solution VARCHAR(100),
    qte INTEGER,
    duree_annee INTEGER,
    pa_grossiste NUMERIC(10, 2),
    total_ca_ht_potentiel NUMERIC(15, 2),
    date_fin_validite_cotation DATE,
    contact_knox VARCHAR(255),
    kam_end_customer VARCHAR(255),
    rdv_client DATE,
    commentaires VARCHAR(1000),
    created_at DATE,
    updated_at DATE,
    CONSTRAINT pk_pipes PRIMARY KEY (id)
);

-- Index pour améliorer les requêtes
CREATE INDEX IF NOT EXISTS idx_quotations_annee ON quotations(annee);
CREATE INDEX IF NOT EXISTS idx_quotations_status ON quotations(status_projet);
CREATE INDEX IF NOT EXISTS idx_quotations_client ON quotations(client_final);

CREATE INDEX IF NOT EXISTS idx_pipes_annee ON pipes(annee);
CREATE INDEX IF NOT EXISTS idx_pipes_status ON pipes(statut_projet);
CREATE INDEX IF NOT EXISTS idx_pipes_client ON pipes(client_final);
