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

**IA :** m'a proposé 24 tickets, j'en ai retenu 17. Les autres étaient des tâches techniques (« créer l'entité JPA », « configurer Flyway »), pas des résultats lisibles par le client — vérification faite en relisant chaque titre avec une seule question : le client comprendrait-il ce qu'il obtient ? Elle a aussi rédigé les quatre diagrammes Mermaid ; je ne l'ai pas crue sur parole : j'ai relu les blocs `alt`/`else`/`end` un par un (six blocs imbriqués dans D3, tous équilibrés), vérifié que chaque code HTTP du diagramme de séquence existe bien dans `api/contrat.yaml` (201, 400, 409, 410), et vérifié que le cardinal `EXERCICE ||--o| RELECTURE` correspond bien à la contrainte `UNIQUE (exercice_id)`. Enfin, j'ai validé le rendu des diagrammes pour de vrai : `mermaid-cli` n'était pas installé, je l'ai installé dans un dossier temporaire et rendu les quatre blocs Mermaid en SVG — les quatre passent, la syntaxe est donc correcte et GitHub les affichera.

---

## Étape 2 — Première version

**Fait :** socle Spring Boot 3 / Java 17 avec `mvnw` commité, PostgreSQL 16 par `docker compose` et schéma versionné par Flyway (migrations V1 et V2, `ddl-auto=validate`). Les quinze opérations d'API du contrat sont en place : les cinq imposées plus la clôture de session, la présence manuelle du formateur, le remplacement de lien, les listes et les relectures à faire. Les trois écrans React sont livrés (formateur, étudiant, relecteur) avec une couche API unique et des états de chargement et d'erreur. `README` en trois commandes, `CHANGELOG` v0.1, et jalon `[JALON] v0.1` poussé.

**Bloqué :** 25 min sur Docker 29, dont l'API minimale a été relevée : Testcontainers, fourni par Spring Boot 3.3.5, n'arrive plus à se connecter au démon. J'ai d'abord tenté de surcharger sa version, sans succès, puis j'ai basculé le test d'intégration sur H2 en mémoire avec `ddl-auto=create-drop` dans le seul scope test — conforme à B6 et à B5, et surtout vérifiable sur ce poste. 15 min de plus sur les conflits de ports : 5432 et 8080 étaient déjà pris par d'autres projets de la machine, j'ai donc déplacé PostgreSQL sur 5433 et vérifié l'API sur 8090, sans toucher aux autres conteneurs. Enfin, un test manuel au `curl` a montré que ma note hors bornes renvoyait `DONNEE_INVALIDE` au lieu de `NOTE_INVALIDE` et mon lien mal formé `DONNEE_INVALIDE` au lieu de `LIEN_INVALIDE` : écart au contrat corrigé, puis couvert par deux tests supplémentaires.

**IA :** a écrit le socle backend, les contrôleurs, les trois écrans et les tests. Je ne l'ai pas crue sur parole : j'ai compilé, lancé les treize tests (`mvn test`), démarré l'application contre le vrai PostgreSQL et appelé chaque opération imposée au `curl` pour comparer le code HTTP et le corps de la réponse avec `api/contrat.yaml` — c'est comme ça que l'écart `NOTE_INVALIDE` est sorti. J'ai aussi vérifié, script à l'appui, que les cinq opérations imposées du contrat n'avaient pas été touchées par mes ajouts.

---

## Étape 3 — Enveloppe

**Fait :** rien encore — l'enveloppe n'est pas disponible (voir Bloqué). Ce qui a avancé pendant l'attente : deux règles du cahier des charges vérifiées sur l'application réelle, contre PostgreSQL, et non seulement dans les tests automatisés.

**Bloqué :** 25 min, et c'est le blocage le plus coûteux de la journée. `./enveloppe` et `git-lab.bundle` ne sont pas dans le dossier reçu : j'ai listé l'archive distribuée (`EPREUVE_KFOKAM48_ETUDIANTS.zip`) — elle ne contient que les huit documents du sujet, SUJET, CLIENT, `api/contrat.yaml`, LISEZ-MOI et les trois modèles — et une recherche sur tout le disque (`find ~ -iname "*enveloppe*" -o -iname "*.bundle"`) ne renvoie rien. Ces deux fichiers sont remis pendant l'épreuve : le jalon `[JALON] v0.1` étant poussé, l'enveloppe est débloquée et a été réclamée.

Les minutes d'attente ont servi à vérifier deux règles que mes treize tests ne couvraient pas, en appelant l'API contre PostgreSQL :

- **Blocage après cinq codes erronés (Q4)** : cinq tentatives avec un code inexistant répondent `400 CODE_INCONNU`, puis la sixième — cette fois avec le **bon** code — répond elle aussi `400 CODE_INCONNU` avec le message « Trop d'essais : réessayez dans 1 min 59 s ». Le blocage masque donc un code valide : c'est ce qui rend l'attente utile contre la devinette, et c'est bien le comportement voulu par le client.
- **Aucun relecteur disponible (Z1)** : un exercice déposé par le seul étudiant présent à la session reste en `EN_ATTENTE_ASSIGNATION`. Dès qu'un second étudiant marque sa présence, le même exercice bascule en `EN_ATTENTE_RELECTURE` et apparaît dans les relectures à faire du nouvel arrivant — sans qu'il ait pu s'assigner l'exercice de lui-même (RG9, RG13).

**IA :** m'a prévenu que les deux fichiers étaient absents plutôt que de simuler l'ouverture de l'enveloppe, puis a rédigé le script qui rejoue ces deux scénarios. Je l'ai relu avant de le lancer et j'ai comparé chaque réponse aux sources : le message de blocage avec la réponse à la question 4 de `CLIENT.md`, et le statut `EN_ATTENTE_ASSIGNATION` avec la zone d'ombre Z1 que j'ai moi-même documentée au cahier des charges — les deux doivent dire la même chose, sinon c'est le cahier des charges qui est faux.

**Ce que j'ai sorti du périmètre pour absorber le changement, et pourquoi :** le changement (issue #19, deux relecteurs) est un Must qui arrive tard : j'écarte donc **définitivement l'issue #17** (suppression d'une présence ajoutée par erreur, Could) — son cas d'usage est rare, il ne justifie pas de toucher à la cohérence du tableau à cette échéance, et il était déjà documenté comme exclu en Z7. Second sacrifice assumé : le remplacement du lien ne vérifie plus « la relecture est commencée » que sur la première relecture (par identifiant croissant) ; couvrir le cas « une rendue sur deux » exigerait un 409 par relecteur, ce que le client n'a jamais demandé.

---

## Étape 4 — Version finale

**Fait :** jalon `[JALON] v1.0` posé et poussé. CHANGELOG v1.0 relu contre l'historique commit par commit (issue #16, correctif du 500 sur `GET /api/relectures/{id}`, validation des quatre diagrammes au rendu). Backlog restant trié : plus aucun Must ni Should en attente, issue #17 maintenue hors périmètre avec sa justification (Z7). README relu puis sa procédure rejouée comme le fera le correcteur : base par `docker compose`, API démarrée, données de démonstration présentes, `GET /api/tableau` conforme au contrat (moyenne calculée par l'API, `404 PROMOTION_INCONNUE` au format imposé), 14 tests verts, build frontend OK. Au passage, une erreur attrapée : le README annonçait 13 tests pour 14 réels — corrigé.

**Bloqué :** le port 8080 était occupé sur le poste au moment de rejouer le README. C'est exactement le cas de repli documenté (API sur 8090 + `VITE_API_URL`) : il a fonctionné du premier coup, ce qui valide la procédure de secours elle-même.

**IA :** a rédigé le CHANGELOG v1.0 et la mise à jour du backlog. Vérifié avant d'accepter : chaque ligne « Ajouté »/« Corrigé » relue contre l'historique (`git log`, `git show 32dde37`), et toutes les affirmations du CHANGELOG et du README rejouées en vrai (`./mvnw test`, `npm run build`, démarrage complet, appels `curl`). C'est cette vérification qui a sorti le chiffre faux des tests : l'IA avait repris « 13 » du README au lieu des rapports Surefire (4 + 10 = 14).

---

## Étape 5 — Épreuve Git

**Fait :**

**Bloqué :**

**IA :**

---

## Étape 6 — Soumission

**Fait :**

**Ce que je referais autrement avec une journée de plus :**
