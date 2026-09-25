# Soumission — Épreuve finale fullstack KFOKAM48

## Candidat

| | |
|---|---|
| Nom et prénom(s) | ROCHA Wilson |
| Matricule | 245 |
| Centre | Yaoundé |
| Compte GitHub | ROCHA-48 |

## Projet

| | |
|---|---|
| Dépôt (public) | `https://github.com/ROCHA-48/kfokam48-epreuve-245` |
| Commit final — hash complet, 40 caractères | 837541b2d3a920c766a7e33af9074b5c3a7d240a |
| Branche | `main` |

## Épreuve Git — étape 5

| | |
|---|---|
| Dépôt (public) | `https://github.com/ROCHA-48/kfokam48-gitlab-245` |
| Commit final — hash complet, 40 caractères | *(en attente — `git-lab.bundle` non remis au moment de la rédaction ; à compléter dès que l'étape 5 est passée)* |

## Technique

| | |
|---|---|
| Frontend utilisé | React 18 avec Vite et TypeScript |
| Base de données | PostgreSQL 16 (conteneur `docker compose`), schéma versionné par Flyway |
| Commandes de démarrage | `docker compose up -d db` · `cd backend && ./mvnw spring-boot:run` · `cd frontend && npm install && npm run dev` |

## Ce que j'ai livré

Fonctionne : ouverture d'une session avec code de présence valable 15 minutes (RG1), pointage par code avec les erreurs `400 CODE_INCONNU`, `409 DEJA_PRESENT` et `410 CODE_EXPIRE`, blocage deux minutes après cinq codes erronés (RG10), dépôt et remplacement du lien d'un exercice, attribution automatique d'un relecteur au hasard parmi les présents et jamais l'auteur, note entière de 0 à 20 corrigeable jusqu'à la clôture, tableau du formateur avec moyenne calculée par l'API, clôture de session et présence ajoutée à la main marquée `FORMATEUR` (Q14), trois écrans React, et 14 tests verts sans base locale.

Ne fonctionne pas / non livré : la suppression d'une présence ajoutée par erreur (issue #17) reste hors périmètre, c'est écrit en Z7 du cahier des charges ; il n'y a aucune authentification, décision assumée du client (Q1, zone d'ombre Z6).

Volontairement laissé de côté : le style visuel, non noté par le sujet, et toute notification automatique (email, SMS), hors périmètre.

---

## Avant de téléverser, vérifie

- [ ] Mes deux dépôts sont **publics** et s'ouvrent en navigation privée
- [ ] Les deux hash font bien **40 caractères** et existent sur GitHub
- [x] Tout mon travail est **poussé** — `git status` est propre sur les deux dépôts
- [x] Mon `README` a été testé depuis un clone vierge, dans un dossier vide — clone, base neuve, API démarrée, données de démonstration chargées, `GET /api/tableau` conforme
- [x] Mon `JOURNAL.md` et mon cahier des charges sont dans `docs/`
- [x] Les trois commits `[JALON]` sont poussés et dans le bon ordre

---

**Déclaration.** J'ai réalisé ce travail seul. Les outils d'IA étaient autorisés sans restriction et je les ai utilisés ; mon journal indique où et comment j'ai vérifié leurs réponses. Mes dépôts resteront publics et inchangés jusqu'à la publication des résultats.

Signature : ROCHA Wilson  Date : 25/09/2026
