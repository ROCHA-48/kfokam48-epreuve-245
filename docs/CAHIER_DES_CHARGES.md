# Cahier des charges — Présences & Relectures KFOKAM48

**Auteur :** ROCHA Wilson · matricule 245
**Version :** 1 · **Date :** 25/09/2026
**Frontend choisi :** React, parce que c'est le framework que je maîtrise le mieux et qui permet d'aller vite sur les trois écrans demandés.

---

## 1. Contexte et objectif

Aujourd'hui, à KFOKAM48, la présence des étudiants est gérée manuellement : chacun émarge sur une liste papier, que le formateur doit ensuite recompter et ressaisir à la main pour savoir qui était présent. Il n'existe aucune trace fiable en cas de contestation, et impossible de croiser rapidement présence, exercices et notes pour avoir une vue d'ensemble sur un étudiant.

Du côté des exercices, les étudiants s'échangent leurs travaux et leurs retours entre eux de façon informelle, probablement par WhatsApp ou email. Rien ne garantit qu'un exercice est relu une seule fois, par une personne différente de son auteur, ni que le formateur puisse savoir qui a déposé, qui a été relu et qui attend encore une relecture.

Cette application s'adresse au formateur et à l'étudiant. Pour le formateur, elle offre une vue d'ensemble en un coup d'œil : présence, dépôts, moyennes et relectures en attente, sans recouper plusieurs sources. Pour l'étudiant, elle remplace un processus manuel par un geste simple et rapide — saisir un code, déposer un lien — avec la garantie que sa relecture reste anonyme et équitable.

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire | Ce qu'il ne peut pas faire |
|---|---|---|
| **Formateur** | Ouvrir une session et obtenir un code de présence (Q2) · Voir le tableau récapitulatif par étudiant (Q16) · Ajouter une présence manuellement, marquée « ajouté par le formateur » (Q14) · Clôturer une session | Modifier une note envoyée par un relecteur (Q15) · Relire un exercice à la place d'un étudiant |
| **Étudiant** | Marquer sa présence avec un code, tant qu'il est valide (Q2, Q3) · Déposer le lien de son exercice, et le remplacer tant que personne ne l'a relu (Q13) · Voir sa note et le commentaire reçu, sans connaître l'identité du relecteur (Q8) · Voir les relectures qu'il doit encore faire (Q16) | Se relire lui-même (Q5) · Choisir qui le relit (Q7) · Voir le nom de son relecteur (Q8) · Marquer sa présence après expiration du code (Q2) |
| **Relecteur** (un étudiant, désigné au hasard parmi les présents pour relire l'exercice d'un pair — Q7) | Rendre une note (0–20, entière) et un commentaire (Q9) · Corriger sa note tant que la session n'est pas clôturée (Q10, voir la contradiction C1 en section 7) | Relire son propre exercice (Q5) · Relire plusieurs fois le même exercice (deux relecteurs distincts maximum — issue #19) · Rendre ou corriger une relecture après la clôture de la session (RG14) |
| **Système** (acteur non humain) | Assigner automatiquement un relecteur à un exercice déposé, au hasard parmi les étudiants présents et différents de l'auteur (EF4, Q6, Q7) · Expirer le code de présence au bout de 15 minutes (RG1) · Bloquer un étudiant après cinq codes erronés (RG10, Q4) | Choisir un relecteur qui n'était pas présent à la session (Q7) |

Le relecteur n'est pas un acteur distinct : c'est un étudiant, désigné temporairement par le système pour relire l'exercice d'un pair (Q7). Il n'existe pas de compte « relecteur » séparé — une même personne est tour à tour étudiant qui dépose, et relecteur d'un autre exercice.

## 3. Périmètre

**Inclus dans cette version :**
- Ouverture d'une session de cours par le formateur, avec génération d'un code de présence (Q2)
- Marquage de la présence par l'étudiant via ce code, et ajout manuel par le formateur si besoin (Q14)
- Clôture d'une session par le formateur, qui ferme la présence, le dépôt et la relecture (Q10, Q12, RG14)
- Dépôt du lien d'un exercice par l'étudiant, avec possibilité de le remplacer tant que personne ne l'a relu (Q13)
- Assignation automatique et aléatoire d'un relecteur parmi les étudiants présents (Q7)
- Relecture : note sur 20 (entière) et commentaire, correction possible tant que la session n'est pas clôturée (Q10)
- Tableau récapitulatif du formateur : présence, dépôts, moyenne, relectures en attente (Q16)

**Explicitement exclu :**
- L'authentification par mot de passe (Q1 — le client a demandé explicitement de ne pas s'en soucier)
- Le design et l'esthétique de l'interface (le sujet précise : « Le rendu visuel n'est pas noté. Aucun point pour le CSS »)
- La notification automatique (email/SMS) des étudiants ou du formateur
- La gestion de plusieurs formateurs ou de droits différenciés entre formateurs
- L'export du tableau (PDF, Excel...) — le formateur consulte les données uniquement à l'écran
- La modification ou la suppression d'une session déjà ouverte, autre que sa clôture
- La suppression ou la correction d'une présence (voir Z7 en section 7)

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | L'étudiant marque sa présence à l'aide d'un code | Quand je saisis un code valide et non expiré, ma présence apparaît dans le tableau du formateur | Must |
| EF2 | Le formateur ouvre une session et obtient un code de présence | Quand je remplis le titre et la promotion et que je valide, j'obtiens un code affiché avec sa date d'expiration | Must |
| EF3 | L'étudiant dépose le lien de son exercice | Quand je saisis un lien valide pour une session où j'étais présent, l'exercice apparaît avec le statut « en attente de relecture » | Must |
| EF4 | Le système assigne un relecteur à un exercice déposé | Quand un exercice est déposé, un étudiant présent, différent de l'auteur, est désigné automatiquement comme relecteur ; si aucun candidat n'est disponible, l'exercice passe au statut « en attente d'assignation » (Z1) | Must |
| EF5 | Le relecteur rend une note et un commentaire | Quand je saisis une note entière entre 0 et 20 et un commentaire, la relecture est enregistrée et visible par l'étudiant relu | Must |
| EF6 | L'étudiant voit sa note et son commentaire sans connaître le relecteur | Quand ma relecture est rendue, je vois la note et le commentaire, mais aucune information sur qui m'a relu | Must |
| EF7 | Le formateur voit un tableau récapitulatif par étudiant | Quand j'affiche le tableau pour ma promotion, je vois pour chaque étudiant sa présence, ses dépôts, sa moyenne et ses relectures en attente | Must |
| EF8 | Le formateur ajoute une présence manuellement | Quand j'ajoute une présence pour un étudiant absent du pointage automatique, elle apparaît marquée « ajouté par le formateur » | Should |
| EF9 | L'étudiant remplace le lien de son exercice | Quand je modifie mon lien avant que la relecture n'ait commencé, le nouveau lien remplace l'ancien | Should |
| EF10 | Le formateur clôture une session | Quand je clôture la session, plus aucune présence ne peut être marquée, aucun exercice déposé, aucune relecture rendue ou corrigée sur cette session | Should |
| EF11 | L'étudiant voit les relectures qu'il doit encore faire | Quand j'affiche mes relectures à faire, je vois chaque exercice assigné qui n'est pas encore relu, sans voir le nom de son auteur avant de l'avoir relu | Should |
| EF12 | Le relecteur corrige sa note avant la clôture | Quand je corrige une note déjà envoyée et que la session n'est pas clôturée, la nouvelle note remplace l'ancienne | Could |

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | L'interface est utilisable sur tout support (mobile, tablette, desktop) | Test manuel sur un écran de 375px de large (mobile) et un écran desktop classique, sans scroll horizontal ni élément coupé |
| ENF2 | Le tableau du formateur répond en moins de 2 s pour une promotion de 60 étudiants | Mesure du temps de réponse de l'endpoint /api/tableau avec un jeu de données de démonstration de 60 étudiants |
| ENF3 | Le format d'erreur JSON est identique pour toutes les erreurs de l'API | Vérification manuelle : chaque code d'erreur (400, 403, 404, 409, 410) renvoie bien { code, message } |
| ENF4 | Aucune donnée sensible n'est stockée, puisque l'authentification se fait par choix de nom | Revue du modèle de données : pas de champ mot de passe dans l'entité Étudiant |
| ENF5 | Le projet démarre chez un tiers en trois commandes maximum, avec des données de démonstration | Test de l'installation depuis un clone vierge, en suivant uniquement le README, sur un poste sans base locale |
| ENF6 | Le code est séparé en couches contrôleur / service / repository et n'expose jamais d'entité JPA | Revue de code : aucun @Entity dans un corps de réponse, uniquement des DTO |
| ENF7 | Chaque règle de gestion RGx citée par un ticket a au moins un test qui la prouve | Inventaire des tests : RG2, RG3, RG5 et RG10 sont couvertes au minimum |

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Un code de présence expire 15 minutes après l'ouverture de la session | Q2 |
| RG2 | Un étudiant ne peut pas relire son propre exercice | Q5 |
| RG3 | Une note est un entier compris entre 0 et 20 | Q9 |
| RG4 | Un exercice a **deux relecteurs distincts**, désignés au hasard parmi les étudiants présents à la session, jamais l'auteur, jamais deux fois le même. Si la session compte moins de trois présents (l'auteur + deux), un seul relecteur est assigné. La note retenue est la **moyenne des notes rendues** ; si un seul a rendu, sa note s'affiche marquée **provisoire** | Issue #19 (changement de besoin de l'enveloppe, remplaçant Q6), Q7 |
| RG5 | L'étudiant relu voit sa note et son commentaire, mais jamais l'identité du relecteur | Q8 |
| RG6 | Le formateur peut ajouter une présence manuellement, marquée « ajouté par le formateur » pour la distinguer d'une présence auto-déclarée | Q14 |
| RG7 | Un étudiant peut déposer son exercice jusqu'à la clôture de la session par le formateur, même après la fin théorique de la session | Q12 |
| RG8 | Un étudiant peut remplacer le lien de son exercice tant que personne n'a commencé à le relire | Q13 |
| RG9 | Si le relecteur désigné ne rend jamais sa relecture, l'exercice reste au statut « en attente », visible comme tel dans le tableau du formateur | Q11 |
| RG10 | Après 5 erreurs de code de présence, l'étudiant est bloqué pendant 2 minutes | Q4 |
| RG11 | Un relecteur peut corriger sa note tant que le formateur n'a pas clôturé la session | Q10 (contradiction C1 tranchée en section 7) |
| RG12 | Une présence ne peut pas être marquée après l'expiration du code | Q2, Q3 |
| RG13 | Un exercice déposé alors qu'aucun relecteur candidat n'est disponible reste au statut « en attente d'assignation » ; le système retente l'assignation à chaque nouvelle présence marquée sur la session | Hypothèse Z1 |
| RG14 | La clôture d'une session est définitive : elle ferme le pointage, le dépôt et la relecture pour cette session | Q10, Q12, Q11 |
| RG15 | Une présence est unique par couple (session, étudiant) : le même étudiant ne peut pas pointer deux fois | Q14, contrat (409 DEJA_PRESENT) |

## 7. Zones d'ombre, hypothèses et contradictions

**Contradiction relevée entre deux réponses du client :**

| # | Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|---|
| C1 | Q10 : « Un relecteur peut-il corriger sa note après l'avoir envoyée ? » → Oui, tant que le formateur n'a pas clôturé la session. Q15 : « La note est-elle définitive une fois envoyée ? » → Oui, une fois validée, c'est fini. | Je retiens Q10 : la note reste corrigeable jusqu'à la clôture de la session (RG11, EF12). | Q10 répond précisément à la question opérationnelle posée, avec une condition claire et actionnable (la clôture de session) ; Q15 énonce un principe plus général et arrive après. En cas de conflit, je retiens la réponse la plus spécifique, et celle qui laisse au relecteur le droit de réparer une erreur de saisie — plus juste pour l'étudiant noté. La clôture (RG14) donne au formateur le moyen de figer définitivement les notes. |

**Points que la demande ne tranche pas :**

| # | Point | Réponse client (Qx) ou hypothèse | Décision retenue | Conséquence |
|---|---|---|---|---|
| Z1 | Que se passe-t-il si aucun relecteur n'est disponible : un seul étudiant présent à la session, ou tous les autres présents sont l'auteur de l'exercice ? | Aucune des 16 questions ne couvre ce cas (c'est le trou du sujet). Issue #19 : avec deux relecteurs requis, le cas s'étend — « moins de trois présents » laisse l'exercice à un seul relecteur, note unique affichée provisoire | L'exercice passe au statut « en attente d'assignation » s'il n'a aucun relecteur, et reçoit un second relecteur dès qu'un étudiant de plus devient présent ; le système retente l'assignation à chaque nouvelle présence (RG13) | L'exercice n'est jamais bloqué ni supprimé, le formateur le voit comme non assigné (Q11). Une issue dédiée, priorité Must. |
| Z2 | Une relecture peut-elle encore être rendue après la clôture de la session, alors qu'aucune des 16 réponses ne le dit explicitement ? | Q10 autorise la correction « tant que le formateur n'a pas clôturé » ; Q11 veut que l'exercice non relu reste « en attente » | La clôture est définitive (RG14) : une relecture non rendue au moment de la clôture reste au statut « en attente » et reste visible dans le tableau du formateur, mais ne peut plus être rendue ni corrigée | Le formateur est le seul maître du calendrier : il peut clôturer pour figer les notes (Q15) ou laisser la session ouverte pour laisser le temps de relire (Q10). |
| Z3 | Q4 (« au bout de cinq erreurs, bloquez-le deux minutes ») n'a aucun code d'erreur prévu dans le contrat imposé | Q4 | Compteur d'échecs par couple (session, étudiant), stocké uniquement le temps de la session ; pendant le blocage, `POST /api/presences` répond `400` avec le code imposé `CODE_INCONNU` et un message qui indique le temps de blocage restant — même si le code saisi était valide | Je n'invente pas de code HTTP supplémentaire, pour rester conforme à B2, et je réponds `CODE_INCONNU` même sur un bon code pendant le blocage : c'est exactement l'intention de Q4, empêcher de deviner les codes. Blocage non persistant au-delà de la session. |
| Z4 | La « fin de session » de Q3 existe-t-elle comme état, alors que Q12 autorise le dépôt jusqu'à la clôture ? | Q3 et Q12 | Il n'y a que trois moments : le code valide 15 minutes (RG1), le code expiré, la session clôturée. La présence s'arrête avec le code (RG12), le dépôt s'arrête avec la clôture (RG7) | EF3 vérifie « une présence existe pour ce couple (session, étudiant) », et non « le code est encore valide » : un étudiant qui a pointé à 10h00 peut déposer à 16h00. |
| Z5 | `relecturesEnAttente` : s'agit-il des relectures en attente sur l'exercice de l'étudiant, ou des relectures que l'étudiant doit lui-même encore faire ? | Q16 : « les relectures qu'il doit encore faire » | Relectures que l'étudiant doit encore faire : relectures qui lui sont assignées et qu'il n'a pas rendues | Le nom du champ est fixé par le contrat (B2) et ne change pas ; sa définition est documentée dans le README et rappelée à l'écran formateur. Un étudiant peut donc avoir 0 dépôt et 3 relectures en attente. |
| Z6 | Q1 supprime l'authentification : n'importe qui peut appeler l'API avec l'identifiant d'un autre étudiant | Q1 | Hypothèse assumée : l'identité est déclarative, l'API ne vérifie pas l'identité de l'appelant, le frontend se contente d'un choix dans la liste des étudiants de la promotion | Aucune donnée sensible n'est stockée (ENF4). Point de sécurité connu et exclu du périmètre, à traiter dans une version ultérieure. |
| Z7 | Une présence ajoutée à tort par le formateur peut-elle être supprimée ou corrigée ? | Hypothèse | Aucune opération de suppression ou de modification de présence n'existe dans cette version : le formateur ajoute, il ne retire pas | Limite assumée et documentée ; reportée en priorité Could du backlog. |
| Z8 | Que se passe-t-il si deux étudiants déposent un exercice pour la même session, ou si un étudiant dépose pour une session où il n'a pas pointé ? | Hypothèse | Un seul exercice par couple (session, étudiant) — le second dépôt répond `409 EXERCICE_DEJA_DEPOSE`, conformément au contrat ; le dépôt exige une présence préexistante (`400` avec `DONNEE_INVALIDE`) | La contrainte est portée par un index unique en base, pas seulement par le code applicatif. |

## 8. Contraintes techniques

**Contraintes imposées par le sujet, et comment je les respecte :**

| Réf | Contrainte | Comment je la respecte |
|---|---|---|
| B1 | Java 17 ou plus, Maven, wrapper `mvnw` commité | Spring Boot 3 avec Java 17 ; `mvnw`, `mvnw.cmd` et `.mvn/wrapper/` commités dès le premier commit de code |
| B2 | Le contrat `api/contrat.yaml` respecté à la lettre | Les cinq opérations imposées sont conservées telles quelles (chemins, verbes, codes, corps) ; un test d'intégration vérifie au moins un code de statut par opération, erreurs comprises |
| B3 | Séparation des couches contrôleur / service / repository, aucune entité JPA en JSON | Un paquet par couche, DTO en entrée et en sortie, conversion par des mappers ; aucun `@Entity` ne sort d'un contrôleur |
| B4 | Validation des entrées et gestion centralisée des erreurs | `jakarta.validation` sur les DTO (`@NotNull`, `@Min(0)`, `@Max(20)`, `@Positive`) + un `@RestControllerAdvice` qui traduit chaque exception en `{ code, message }` ; aucune stack trace renvoyée |
| B5 | Schéma versionné par Flyway ou Liquibase, `ddl-auto=update` interdit hors tests | Flyway sur PostgreSQL, migrations `V1__schema_initial.sql` (et suivantes) commitées ; `spring.jpa.hibernate.ddl-auto=validate` |
| B6 | Deux tests qui prouvent quelque chose, tournant sur un poste vierge | Test unitaire de règle métier (note entière 0–20 et interdiction de l'auto-relecture, RG2/RG3) avec JUnit 5 + Mockito ; test d'intégration sur `POST /api/presences` avec MockMvc et une base PostgreSQL jetable (Testcontainers), qui couvre 201, 409 et 410 |
| F1 | Framework déclaré et justifié en une ligne, build qui passe | React 18 avec Vite et TypeScript ; la justification tient en une ligne dans le README ; `npm run build` vérifié |
| F2 | Trois écrans : formateur, étudiant, relecteur | Trois routes : `/formateur` (ouvrir une session, clôturer, voir le tableau, ajouter une présence), `/etudiant` (choisir son nom, marquer sa présence, déposer son lien, voir sa note), `/relecteur` (voir ses relectures à faire, rendre une note) |
| F3 | Couche API dédiée, états de chargement et d'erreur gérés, aucune règle métier dupliquée | Tous les appels dans `frontend/src/api/` ; chaque écran gère explicitement le chargement, l'erreur et l'absence de données ; la moyenne affichée provient du champ `moyenne` de `GET /api/tableau`, jamais recalculée côté client |

**Contraintes que je m'impose :**
- Base de données relationnelle PostgreSQL 16, lancée par `docker compose` : contraintes d'unicité portées par la base (une présence par session et par étudiant, un exercice par session et par étudiant, un seul relecteur par exercice).
- Migrations Flyway uniquement : toute évolution du schéma passe par une migration versionnée et commitée, jamais par du SQL modifié à la main.
- Tests : JUnit 5 et Mockito pour l'unitaire, `@SpringBootTest` et MockMvc pour l'intégration sur une base jetable (Testcontainers), sans dépendre d'une base locale.
- Aucun secret dans le dépôt : la configuration passe par des variables d'environnement, avec un `.env.example` commité et un `.env` ignoré.
- Données de démonstration chargées au démarrage (une promotion, six étudiants, une session ouverte et des exercices déposés) par une migration dédiée.
- Trois commandes maximum pour démarrer, documentées et testées depuis un clone vierge.

## 9. Livrables

- `docs/CAHIER_DES_CHARGES.md` — ce document, maintenu à jour après chaque changement de besoin
- `docs/diagrammes/D1-cas-utilisation.md`, `D2-modele-de-donnees.md`, `D3-sequence-presence.md` et `D4-etats-exercice.md`, en Mermaid, versionnés
- `docs/BACKLOG.md` — copie locale du backlog ; la source de vérité reste les issues GitHub du dépôt
- `docs/JOURNAL.md` — une entrée par étape
- `api/contrat.yaml` — les cinq opérations imposées plus les opérations propres, figé avant le premier commit de code
- `backend/` — application Spring Boot (contrôleur, service, repository, DTO, migrations Flyway)
- `frontend/` — application React avec les trois écrans
- `docker-compose.yml` — base de données, backend, frontend
- `README.md` — framework frontend justifié, trois commandes de démarrage, vérifiées depuis un clone vierge
- `CHANGELOG.md` — cohérent avec l'historique Git
- `SOUMISSION.md` — le formulaire de soumission, téléversé sur la plateforme avant 18h00

## 10. Démarche prévue

Je travaille dans l'ordre des six étapes du sujet, et je pousse après chaque unité de travail.

1. **Étape 1 — Analyse et conception.** Cahier des charges, quatre diagrammes, backlog en issues, contrat d'API complété, `.gitignore` posé, puis commit dédié `[JALON] analyse`. Aucune ligne de code avant ce jalon.
2. **Étape 2 — Première version.** Uniquement les stories Must : les issues sont découpées par branche, une branche par ticket, une PR par branche, la PR ferme l'issue. Je pousse `[JALON] v0.1` dès que les Must passent sur une base vide recréée par les migrations.
3. **Étape 3 — Enveloppe.** J'ouvre l'enveloppe, je reproduis le bug et je qualifie le changement de besoin. La conduite du changement est séparée en deux moments : l'issue de bug est ouverte avant le correctif, l'évolution est traitée à part. Comme l'enveloppe touche la base, le contrat et le frontend, je commence par la migration, puis le contrat, puis le frontend. Ensuite je reviens corriger ce cahier des charges et les diagrammes dans un commit qui dit explicitement ce qui est devenu faux.
4. **Étape 4 — Version finale.** `[JALON] v1.0`, `CHANGELOG.md`, README testé depuis un clone vierge, backlog restant trié.
5. **Étape 5 — Épreuve Git.** Dépôt `git-lab.bundle` cloné dans `kfokam48-gitlab-245`, les cinq situations résolues, puis poussé sur un second dépôt public séparé du projet.
6. **Étape 6 — Soumission.** `SOUMISSION.md` rempli avec les deux liens publics et les deux hash complets, vérifiés depuis une fenêtre de navigation privée, et téléversé avant 18h00.

**En cas de retard, l'ordre de sacrifice est :** les Could, puis les Should non critiques ; les Must et les trois jalons ne sont jamais sacrifiés. Si je dois choisir, je préfère livrer cinq Must impeccables avec un historique lisible que dix fonctionnalités à moitié testées : l'application ne pèse que 15 points, la démarche 70.

**Definition of Done — un ticket est terminé quand :**
- le comportement annoncé dans le titre est démontrable à l'écran, sur les données de démonstration ;
- les critères d'acceptation du ticket sont vérifiés un par un, et aucun n'est laissé « à tester plus tard » ;
- `mvn verify` passe côté backend et `npm run build` passe côté frontend ;
- une règle de gestion ou une exigence citée par le ticket est couverte par au moins un test (unitaire ou d'intégration) qui échoue sans le changement ;
- les entrées sont validées et chaque erreur sort au format `{ code, message }` via le `@RestControllerAdvice`, sans stack trace ;
- aucune entité JPA n'est exposée : les réponses sont des DTO ;
- tout changement de schéma passe par une migration Flyway commitée, et `api/contrat.yaml` est mis à jour si l'API change ;
- le ticket est commité sur sa branche, cité dans le message de commit, puis fermé par la PR mergée dans `main` ;
- le cahier des charges, les diagrammes et le README sont corrigés si le ticket a changé le besoin ou l'usage.

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | 25/09/2026, étape 1 | Version initiale : besoin, acteurs, périmètre, EF1–EF12, ENF1–ENF7, RG1–RG15, contradiction Q10/Q15 tranchée, zones d'ombre Z1–Z8 documentées, contraintes B1–B6 et F1–F3 reprises, démarche des six étapes. Le trou du sujet (aucun relecteur disponible, Z1) est traité par le statut « en attente d'assignation ». |
| 2 | 25/09/2026, étape 3 (enveloppe) | **Changement de besoin (issue #19) : ce qui était vrai devient faux.** RG4 réécrite : deux relecteurs distincts par exercice, note retenue = moyenne des deux, provisoire si un seul a rendu — l'ancienne règle issue de Q6 est abandonnée. Z1 étendue (moins de trois présents = un seul relecteur). Acteurs : la limite « un seul relecteur » disparaît. D2 corrigé : cardinalité `EXERCICE \|\|--o{ RELECTURE`, contrainte `UNIQUE (exercice_id, relecteur_id)`. Migration V2 ajoutée, la V1 inchangée. La correction de note (C1/RG11) ne change pas : chaque relecteur corrige la sienne jusqu'à la clôture. |
