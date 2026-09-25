# Journal de bord — ROCHA Wilson · matricule 245

> Une entrée **par étape**, écrite **au moment où tu la termines**, pas à la fin de la journée.
> Trois lignes suffisent. Un journal rédigé d'un bloc juste avant de soumettre se repère
> immédiatement dans l'historique Git et ne compte pas.

Chaque entrée répond aux trois mêmes questions :

- **Fait** — ce que tu viens de terminer
- **Bloqué** — ce qui t'a coûté du temps, et combien
- **IA** — ce que tu lui as demandé, et **comment tu as vérifié sa réponse**

---

## Étape 1 — Analyse et conception

**Fait :** cahier des charges complet en 10 sections — EF1 à EF12 avec critères vérifiables, ENF1 à ENF7, RG1 à RG15, contradiction Q10/Q15 tranchée (C1) et huit zones d'ombre documentées (Z1 à Z8). Quatre diagrammes Mermaid dans `docs/diagrammes/` : cas d'utilisation, modèle de données, séquence « marquer sa présence », plus le bonus états-transitions d'un exercice. `api/contrat.yaml` complété de dix opérations propres (quinze au total, catalogue des codes d'erreur inclus). `.gitignore` Java et JS posé. Backlog rédigé en 17 tickets (textes prêts dans `docs/BACKLOG.md`, à porter en issues sur GitHub). Commit `[JALON] analyse` poussé, avant toute ligne de code.

**Bloqué :** 12 min sur la contradiction entre Q10 et Q15 — tranchée en faveur de Q10 (la note reste corrigeable jusqu'à la clôture), parce que Q15 est une intention générale alors que Q10 répond à la question opérationnelle posée, et parce que le relecteur doit pouvoir réparer une faute de saisie. 20 min sur le trou du sujet : aucune des 16 réponses ne dit ce qui se passe quand **aucun relecteur n'est disponible** (un seul étudiant présent, ou tous les autres présents sont l'auteur). Décision : statut « en attente d'assignation », distinct de « en attente de relecture », avec nouvelle tentative à chaque présence marquée (Z1, RG13). 12 min aussi pour vérifier, script Python à l'appui, que mon ajout au contrat ne touchait pas une seule ligne des cinq opérations imposées : c'est le point qui coûte 5 points si je le rate.

**IA :** m'a proposé 24 tickets, j'en ai retenu 17. Les autres étaient des tâches techniques (« créer l'entité JPA », « configurer Flyway »), pas des résultats lisibles par le client — vérification faite en relisant chaque titre avec une seule question : le client comprendrait-il ce qu'il obtient ? Elle a aussi rédigé les quatre diagrammes Mermaid ; je ne l'ai pas crue sur parole : j'ai relu les blocs `alt`/`else`/`end` un par un (six blocs imbriqués dans D3, tous équilibrés), vérifié que chaque code HTTP du diagramme de séquence existe bien dans `api/contrat.yaml` (201, 400, 409, 410), et vérifié que le cardinal `EXERCICE ||--o| RELECTURE` correspond bien à la contrainte `UNIQUE (exercice_id)`. Faute de `mermaid-cli` sur le poste, le rendu n'a pas pu être validé en local : il sera vérifié sur GitHub juste après le push, et corrigé si un diagramme ne s'affiche pas.

---

## Étape 2 — Première version

**Fait :** socle Spring Boot 3 / Java 17 avec `mvnw` commité, PostgreSQL 16 par `docker compose` et schéma versionné par Flyway (migrations V1 et V2, `ddl-auto=validate`). Les quinze opérations d'API du contrat sont en place : les cinq imposées plus la clôture de session, la présence manuelle du formateur, le remplacement de lien, les listes et les relectures à faire. Les trois écrans React sont livrés (formateur, étudiant, relecteur) avec une couche API unique et des états de chargement et d'erreur. `README` en trois commandes, `CHANGELOG` v0.1, et jalon `[JALON] v0.1` poussé.

**Bloqué :** 25 min sur Docker 29, dont l'API minimale a été relevée : Testcontainers, fourni par Spring Boot 3.3.5, n'arrive plus à se connecter au démon. J'ai d'abord tenté de surcharger sa version, sans succès, puis j'ai basculé le test d'intégration sur H2 en mémoire avec `ddl-auto=create-drop` dans le seul scope test — conforme à B6 et à B5, et surtout vérifiable sur ce poste. 15 min de plus sur les conflits de ports : 5432 et 8080 étaient déjà pris par d'autres projets de la machine, j'ai donc déplacé PostgreSQL sur 5433 et vérifié l'API sur 8090, sans toucher aux autres conteneurs. Enfin, un test manuel au `curl` a montré que ma note hors bornes renvoyait `DONNEE_INVALIDE` au lieu de `NOTE_INVALIDE` et mon lien mal formé `DONNEE_INVALIDE` au lieu de `LIEN_INVALIDE` : écart au contrat corrigé, puis couvert par deux tests supplémentaires.

**IA :** a écrit le socle backend, les contrôleurs, les trois écrans et les tests. Je ne l'ai pas crue sur parole : j'ai compilé, lancé les treize tests (`mvn test`), démarré l'application contre le vrai PostgreSQL et appelé chaque opération imposée au `curl` pour comparer le code HTTP et le corps de la réponse avec `api/contrat.yaml` — c'est comme ça que l'écart `NOTE_INVALIDE` est sorti. J'ai aussi vérifié, script à l'appui, que les cinq opérations imposées du contrat n'avaient pas été touchées par mes ajouts.

---

## Étape 3 — Enveloppe

**Fait :**

**Bloqué :**

**IA :**

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :**

---

## Étape 4 — Version finale

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 5 — Épreuve Git

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 6 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
