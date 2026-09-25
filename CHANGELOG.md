# Journal des versions

## v0.1 — 25/09/2026

Première version livrée : les stories **Must** et deux stories **Should**.

### Ajouté

- Socle Spring Boot 3 / Java 17 / Maven avec wrapper `mvnw` commité, PostgreSQL 16 par `docker compose`, schéma versionné par Flyway (B1, B5).
- Données de démonstration : promotion, 6 étudiants, session ouverte, présences, exercices et relectures (démarrage).
- `POST /api/sessions` — ouverture d'une session et code de présence valable 15 minutes (issue #2, EF2, RG1).
- `POST /api/presences` — pointage par code, avec 400 `CODE_INCONNU`, 409 `DEJA_PRESENT` et 410 `CODE_EXPIRE` (issues #3 et #4, EF1, RG12, RG15).
- Blocage deux minutes après cinq codes erronés (issue #12, RG10, Q4).
- `POST /api/exercices` — dépôt du lien, `409 SESSION_CLOTUREE`, `409 PRESENCE_REQUISE`, `400 LIEN_INVALIDE`, `409 EXERCICE_DEJA_DEPOSE` (issue #5, EF3, RG7).
- Attribution automatique d'un relecteur au hasard parmi les présents, jamais l'auteur, avec statut `EN_ATTENTE_ASSIGNATION` quand personne n'est disponible (issue #6, EF4, RG4, Z1).
- `POST /api/relectures/{id}` — note entière de 0 à 20 et commentaire, `400 NOTE_INVALIDE`, `403 AUTO_RELECTURE`, `409 RELECTURE_DEJA_RENDUE` (issue #7, EF5, RG2, RG3).
- Notes reçues sans l'identité du relecteur (issue #8, EF6, RG5).
- `GET /api/tableau` — tableau récapitulatif, moyenne calculée par l'API (issue #9, EF7, Q16).
- Écran relecteur : relectures à faire et rendu d'une relecture (issue #10, EF11).
- `POST /api/sessions/{id}/cloture` — clôture définitive (issue #13, EF10, RG14).
- `POST /api/presences/formateur` — présence manuelle marquée source `FORMATEUR` (issue #14, EF8, Q14).
- `PUT /api/exercices/{id}/lien` — remplacement du lien tant que la relecture n'est pas rendue (issue #15, EF9, RG8).
- Correction d'une note déjà envoyée tant que la session n'est pas clôturée, puis note définitive après la clôture (issue #16, EF12, RG11, arbitrage C1).
- Trois écrans React avec couche API dédiée et états de chargement et d'erreur (F2, F3).
- 14 tests : règle métier du tirage au sort (unitaire) et endpoint `POST /api/presences` (intégration), verts sans base locale (B6).

### Non livré dans cette version

- Suppression d'une présence ajoutée par erreur (issue #17) — hors périmètre assumé, documenté en Z7 du cahier des charges.
