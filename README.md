# Présences & Relectures — KFOKAM48

Épreuve finale fullstack — ROCHA Wilson, matricule 245.

Application de pointage de présence par code, de dépôt d'exercices et de relecture anonyme entre pairs.

**Frontend choisi : React (Vite + TypeScript)** — je le maîtrise, sa mise en route est immédiate et il suffit à couvrir les trois écrans demandés (F1).

## Démarrage en trois commandes

```bash
docker compose up -d db                                    # 1) PostgreSQL 16 (port 5433)
cd backend && ./mvnw spring-boot:run                       # 2) API Spring Boot sur http://localhost:8080
cd frontend && npm install && npm run dev                  # 3) interface sur http://localhost:5173
```

Au premier démarrage, Flyway crée le schéma et charge les **données de démonstration**.
Aucune base locale n'est nécessaire : tout passe par le conteneur.

Si le port 8080 est déjà pris sur votre poste :

```bash
cd backend && ./mvnw spring-boot:run -Dspring-boot.run.arguments="--server.port=8090"
VITE_API_URL=http://localhost:8090 npm run dev --prefix frontend
```

## Données de démonstration

| Élément | Contenu |
|---|---|
| Promotion | `KF48-2026` |
| Étudiants | 6 (Awa Nkolo, Brice Fotso, Cedric Mbarga, Diane Kamga, Elsa Ngo Bell, Franck Oumarou) |
| Session ouverte | code `K7M2QD`, valide 15 minutes après l'ouverture de la session |
| Présences | 3 pointées par les étudiants, 1 ajoutée par le formateur (source `FORMATEUR`, Q14) |
| Exercices | 1 relu (note 16), 1 en attente de relecture, 1 en attente d'assignation |

Le code de démonstration expire 15 minutes après le démarrage (RG1) : c'est la règle, pas un bug.
Pour un nouveau code, ouvrez une session depuis l'écran formateur.

## Les trois écrans (F2)

| Écran | Ce qu'on y fait |
|---|---|
| `/formateur` | ouvrir une session et lire le code, voir le tableau récapitulatif, clôturer la session, ajouter une présence à la main |
| `/etudiant` | choisir son nom dans la liste, marquer sa présence avec le code, déposer ou remplacer le lien de son exercice, lire sa note et son commentaire |
| `/relecteur` | voir les relectures à faire, ouvrir le travail à relire, rendre une note entière de 0 à 20 et un commentaire |

Aucun mot de passe : l'identité est déclarative (Q1). Les trois écrans partagent le nom choisi en en-tête de page.

## Tests

```bash
cd backend && ./mvnw test
```

13 tests, verts sur un poste vierge, **sans base locale et sans Docker** (base H2 en mémoire) :

- `AttributionRelectureTest` — règle métier du tirage au sort : le relecteur n'est jamais l'auteur, il est choisi parmi les présents, et un exercice sans candidat reste sans relecteur (Q5, Q6, Q7, Z1).
- `PresenceControllerIntegrationTest` — endpoint imposé `POST /api/presences` : 201, 400 `CODE_INCONNU`, 400 `DONNEE_INVALIDE`, 409 `DEJA_PRESENT`, 410 `CODE_EXPIRE`, plus `LIEN_INVALIDE`, `NOTE_INVALIDE`, le format du tableau et `404 PROMOTION_INCONNUE`.

## Documentation

| Fichier | Contenu |
|---|---|
| `docs/CAHIER_DES_CHARGES.md` | besoin, acteurs, périmètre, EF1–EF12, ENF1–ENF7, RG1–RG15, zones d'ombre Z1–Z8 et contradiction tranchée C1 |
| `docs/diagrammes/` | D1 cas d'utilisation, D2 modèle de données, D3 séquence « marquer sa présence », D4 états-transitions (bonus) |
| `docs/BACKLOG.md` | les 17 tickets, repris en issues sur le dépôt |
| `docs/JOURNAL.md` | journal de bord, une entrée par étape |
| `api/contrat.yaml` | les 5 opérations imposées + les opérations propres, avec le catalogue des codes d'erreur |
| `docs/SOUMISSION.md` | formulaire de soumission |

## Vocabulaire utile

- `relecturesEnAttente`, dans le tableau du formateur : les relectures que **cet étudiant doit encore faire**, et non celles qui attendent sur son propre exercice (Q16, décision Z5).
- `EN_ATTENTE_ASSIGNATION` : l'exercice est déposé mais aucun étudiant présent autre que son auteur n'est disponible pour le relire (Z1). Le système retente l'attribution à chaque nouvelle présence.
- Clôture d'une session (RG14) : elle fige pointage, dépôt et relecture. Une relecture jamais rendue reste visible « en attente » (Q11).

## Structure

```
/api          contrat d'API (OpenAPI 3)
/backend      Spring Boot 3, Java 17, Maven, Flyway, PostgreSQL
/frontend     React 18 + Vite + TypeScript
/docs         cahier des charges, diagrammes, backlog, journal, soumission
docker-compose.yml
```
