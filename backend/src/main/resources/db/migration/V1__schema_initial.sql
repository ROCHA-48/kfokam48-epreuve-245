-- Schema initial : correspond exactement a docs/diagrammes/D2-modele-de-donnees.md
-- Toute evolution du schema passe par une nouvelle migration versionnee (B5).

CREATE TABLE promotion (
    id      BIGSERIAL PRIMARY KEY,
    nom     VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE etudiant (
    id           BIGSERIAL PRIMARY KEY,
    nom          VARCHAR(160) NOT NULL,
    email        VARCHAR(200) UNIQUE,
    promotion_id BIGINT NOT NULL REFERENCES promotion (id)
);

CREATE TABLE session_cours (
    id            BIGSERIAL PRIMARY KEY,
    titre         VARCHAR(160) NOT NULL,
    code          VARCHAR(12) NOT NULL UNIQUE,
    promotion_id  BIGINT NOT NULL REFERENCES promotion (id),
    ouverture_at  TIMESTAMPTZ NOT NULL,
    expiration_at TIMESTAMPTZ NOT NULL,
    cloture_at    TIMESTAMPTZ
);

-- RG15 : une presence unique par couple (session, etudiant)
CREATE TABLE presence (
    id          BIGSERIAL PRIMARY KEY,
    session_id  BIGINT NOT NULL REFERENCES session_cours (id),
    etudiant_id BIGINT NOT NULL REFERENCES etudiant (id),
    source      VARCHAR(16) NOT NULL,
    ajoute_at   TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_presence_session_etudiant UNIQUE (session_id, etudiant_id)
);

-- Z8 : un seul exercice par couple (session, etudiant)
CREATE TABLE exercice (
    id          BIGSERIAL PRIMARY KEY,
    session_id  BIGINT NOT NULL REFERENCES session_cours (id),
    etudiant_id BIGINT NOT NULL REFERENCES etudiant (id),
    lien        VARCHAR(1000) NOT NULL,
    statut      VARCHAR(32) NOT NULL,
    depose_at   TIMESTAMPTZ NOT NULL,
    maj_lien_at TIMESTAMPTZ,
    CONSTRAINT uk_exercice_session_etudiant UNIQUE (session_id, etudiant_id)
);

-- Q6 : un seul relecteur par exercice ; RG3 : note entiere de 0 a 20
CREATE TABLE relecture (
    id           BIGSERIAL PRIMARY KEY,
    exercice_id  BIGINT NOT NULL REFERENCES exercice (id),
    relecteur_id BIGINT NOT NULL REFERENCES etudiant (id),
    note         INTEGER,
    commentaire  VARCHAR(4000),
    assignee_at  TIMESTAMPTZ NOT NULL,
    rendue_at    TIMESTAMPTZ,
    CONSTRAINT uk_relecture_exercice UNIQUE (exercice_id),
    CONSTRAINT ck_relecture_note CHECK (note IS NULL OR (note >= 0 AND note <= 20))
);

CREATE INDEX idx_presence_session ON presence (session_id);
CREATE INDEX idx_exercice_session ON exercice (session_id);
CREATE INDEX idx_relecture_relecteur ON relecture (relecteur_id);
