# D2 — Modèle de données

Ce modèle est celui des migrations Flyway `V1__schema_initial.sql` et suivantes : chaque entité ci-dessous correspond à une table, chaque attribut à une colonne, avec le même nom en `snake_case`.

```mermaid
erDiagram
    PROMOTION ||--o{ ETUDIANT : "regroupe"
    PROMOTION ||--o{ SESSION : "concerne"
    SESSION ||--o{ PRESENCE : "enregistre"
    ETUDIANT ||--o{ PRESENCE : "marque"
    SESSION ||--o{ EXERCICE : "porte"
    ETUDIANT ||--o{ EXERCICE : "dépose"
    EXERCICE ||--o| RELECTURE : "reçoit au plus une"
    ETUDIANT ||--o{ RELECTURE : "rédige (relecteur)"

    PROMOTION {
        bigint id PK
        varchar nom UK "ex. KF48-2026"
    }

    ETUDIANT {
        bigint id PK
        varchar nom
        bigint promotion_id FK
        varchar email UK "nullable, non sensible"
    }

    SESSION {
        bigint id PK
        varchar titre
        varchar code UK "code de présence, Q2"
        bigint promotion_id FK
        timestamptz ouverture_at
        timestamptz expiration_at "ouverture_at + 15 min (RG1)"
        timestamptz cloture_at "nullable : null tant que la session est ouverte (RG14)"
    }

    PRESENCE {
        bigint id PK
        bigint session_id FK "UK avec etudiant_id"
        bigint etudiant_id FK "UK avec session_id"
        varchar source "ETUDIANT | FORMATEUR (Q14)"
        timestamptz ajoute_at
    }

    EXERCICE {
        bigint id PK
        bigint session_id FK "UK avec etudiant_id"
        bigint etudiant_id FK "UK avec session_id"
        varchar lien "URI du travail déposé"
        varchar statut "EN_ATTENTE_ASSIGNATION | EN_ATTENTE_RELECTURE | RELU"
        timestamptz depose_at
        timestamptz maj_lien_at "nullable, dernier remplacement (RG8)"
    }

    RELECTURE {
        bigint id PK
        bigint exercice_id FK "UK : un seul relecteur par exercice (Q6)"
        bigint relecteur_id FK "étudiant présent, différent de l'auteur (Q5, Q7)"
        integer note "nullable, entier 0..20 (RG3)"
        text commentaire "nullable"
        timestamptz assignee_at
        timestamptz rendue_at "nullable : null tant que la relecture n'est pas rendue"
    }
```

## Contraintes portées par la base, et où elles se voient dans le diagramme

| Contrainte | Colonnes | Règle |
|---|---|---|
| Un étudiant ne pointe qu'une fois par session | `UNIQUE (session_id, etudiant_id)` sur `PRESENCE` | RG15, `409 DEJA_PRESENT` |
| Un étudiant ne dépose qu'un exercice par session | `UNIQUE (session_id, etudiant_id)` sur `EXERCICE` | contrat, `409 EXERCICE_DEJA_DEPOSE`, Z8 |
| Un exercice n'a qu'un seul relecteur | `UNIQUE (exercice_id)` sur `RELECTURE`, cardinalité `EXERCICE ||--o| RELECTURE` | Q6, RG4 |
| Jamais d'auto-relecture | `CHECK (relecteur_id <> auteur de l'exercice)`, vérifié en service | Q5, RG2 |
| Une note est un entier de 0 à 20 | `CHECK (note BETWEEN 0 AND 20)` sur `RELECTURE` | Q9, RG3, `400 NOTE_INVALIDE` |
| Le code d'une session est unique et retrouvable | `UNIQUE (code)` sur `SESSION` | Q2 |
| Aucun mot de passe stocké | aucune colonne de mot de passe | Q1, ENF4 |

## Cycle de vie du statut d'un exercice

`EN_ATTENTE_ASSIGNATION` : déposé, mais aucun relecteur candidat disponible (Z1, RG13).
`EN_ATTENTE_RELECTURE` : un relecteur est assigné, il n'a pas encore rendu (RG9, Q11).
`RELU` : la relecture est rendue, la note est corrigeable jusqu'à la clôture de la session (RG11, C1).
