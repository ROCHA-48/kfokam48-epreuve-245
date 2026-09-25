# Backlog — 17 tickets à porter en issues sur GitHub

Ce fichier est la copie locale du backlog. **La source de vérité reste les issues GitHub** du dépôt : chaque ticket ci-dessous est prêt à être copié-collé (titre, priorité, renvois, critères d'acceptation).
Chaque issue s'appuie sur le cahier des charges (`docs/CAHIER_DES_CHARGES.md`) : `EFx` pour les exigences fonctionnelles, `RGx` pour les règles de gestion, `Qx` pour les réponses du client, `Zx` / `Cx` pour les zones d'ombre et la contradiction tranchée.

**Definition of Done, commune à tous les tickets** (détail en CDC §10) : le comportement est démontrable à l'écran sur les données de démo ; un test échoue sans le changement ; les erreurs sortent au format `{ code, message }` via le `@RestControllerAdvice` ; aucune entité JPA exposée en JSON ; tout changement de schéma passe par une migration Flyway commitée ; `api/contrat.yaml` est mis à jour si l'API change ; branche dédiée, PR liée à l'issue, message de commit qui cite l'issue.

**Ordre de traitement :** les Must de l'étape 2, puis les Should, puis les Could si le temps le permet.

**État au 25/09 (v0.1) :** les issues #1 à #15 sont livrées et couvertes par les tests ; l'issue #16 (correction d'une note avant la clôture) est livrée avec son test d'intégration ; l'issue #17 (suppression d'une présence) reste hors périmètre, décision documentée en Z7. Le reste du backlog sera trié à l'étape 4.

| # | Titre | Priorité | Renvoi |
|---|---|---|---|
| 1 | Choisir son nom dans la liste des étudiants | Must | Q1, ENF4, Z6 |
| 2 | Ouvrir une session et obtenir un code de présence | Must | EF2, RG1, Q2 |
| 3 | Marquer sa présence avec le code | Must | EF1, RG1, RG12, RG15 |
| 4 | Refuser une présence invalide avec le bon code d'erreur | Must | EF1, RG10, RG15, B2, B4 |
| 5 | Déposer le lien de son exercice | Must | EF3, RG7, Z8, Q12 |
| 6 | Assigner automatiquement un relecteur à chaque exercice déposé | Must | EF4, RG4, RG13, Z1 |
| 7 | Rendre une note et un commentaire sur l'exercice d'un pair | Must | EF5, RG2, RG3, Q5, Q9 |
| 8 | Voir sa note et son commentaire sans savoir qui a relu | Must | EF6, RG5, Q8 |
| 9 | Voir le tableau récapitulatif de la promotion | Must | EF7, Q16, Z5, ENF2 |
| 10 | Voir et rendre ses relectures à faire (écran relecteur) | Must | EF11, Q6, Q7, Q16, F2 |
| 11 | Démarrage en trois commandes avec données de démonstration | Must | ENF5, F1, démarrage |
| 12 | Bloquer deux minutes après cinq codes erronés | Should | RG10, Q4, Z3 |
| 13 | Clôturer une session | Should | EF10, RG14, Q10, Q12 |
| 14 | Ajouter une présence à la main, marquée « ajouté par le formateur » | Should | EF8, RG6, Q14 |
| 15 | Remplacer le lien de son exercice | Should | EF9, RG8, Q13 |
| 16 | Corriger une note avant la clôture de la session | Could | EF12, RG11, C1 |
| 17 | Supprimer une présence ajoutée par erreur | Could | Z7 |

---
### Issue 1
**Titre :** Choisir son nom dans la liste des étudiants
**Priorité :** Must · **Renvoi :** Q1, ENF4, Z6 · **Labels :** frontend, api

**Résultat attendu**
Un étudiant ouvre son écran, choisit sa promotion puis son nom dans une liste. Aucun mot de passe, aucune inscription.

**Critères d'acceptation**
- [ ] Quand j'ouvre l'écran « Étudiant », alors je vois la liste des promotions.
- [ ] Quand je choisis une promotion, alors je vois la liste de ses étudiants.
- [ ] Quand je sélectionne un nom, alors mon identifiant est retenu pour la suite de la navigation et affiché en en-tête.
- [ ] Quand les listes ne chargent pas, alors un message d'erreur est affiché et l'écran reste utilisable (F3).
- [ ] Aucun mot de passe n'est demandé ni stocké (ENF4).

**Notes**
`GET /api/promotions` et `GET /api/promotions/{promotionId}/etudiants`. L'identité est déclarative, c'est un choix assumé (Z6).
---

### Issue 2
**Titre :** Ouvrir une session et obtenir un code de présence
**Priorité :** Must · **Renvoi :** EF2, RG1, Q2 · **Labels :** backend, frontend

**Résultat attendu**
Le formateur ouvre une session de cours pour une promotion et reçoit un code de présence, à annoncer à l'oral.

**Critères d'acceptation**
- [ ] Quand je remplis le titre et la promotion et que je valide, alors j'obtiens un code affiché avec son heure d'expiration.
- [ ] Quand la session est créée, alors son `expirationAt` vaut exactement `ouvertureAt + 15 minutes` (RG1).
- [ ] Quand je rouvre l'écran formateur, alors je vois la session en cours et son code, tant qu'elle n'est pas clôturée.
- [ ] Quand le titre est vide, alors le serveur répond `400` au format `{ code, message }` et rien n'est créé.

**Notes**
`POST /api/sessions` (imposé), `GET /api/sessions/{sessionId}` et `GET /api/sessions?promotionId=` (propres). Le code est unique en base.
---

### Issue 3
**Titre :** Marquer sa présence avec le code
**Priorité :** Must · **Renvoi :** EF1, RG1, RG12, RG15, Q2 · **Labels :** backend, frontend

**Résultat attendu**
Un étudiant saisit le code annoncé et sa présence est enregistrée pour la session, visible immédiatement par le formateur.

**Critères d'acceptation**
- [ ] Quand je saisis un code valide et non expiré et que je valide, alors j'obtiens une confirmation et ma présence apparaît dans le tableau du formateur (EF1).
- [ ] Quand ma présence est enregistrée, alors sa source vaut `ETUDIANT`.
- [ ] Quand je saisis le code une seconde fois, alors le serveur répond `409 { code: "DEJA_PRESENT" }` et aucune seconde présence n'est créée (RG15).
- [ ] Quand le code a plus de 15 minutes, alors le serveur répond `410 { code: "CODE_EXPIRE" }` (RG1, RG12).
- [ ] Quand le code ne correspond à aucune session, alors le serveur répond `400 { code: "CODE_INCONNU" }`.
- [ ] Quand l'étudiant n'est pas présent à la session, alors il ne peut pas déposer d'exercice pour cette session (voir issue 5).

**Notes**
`POST /api/presences` (imposé). La présence est unique par couple (session, étudiant) : contrainte `UNIQUE` en base, pas seulement dans le service (B3, RG15).
Test d'intégration obligatoire sur cet endpoint (B6), couvrant 201, 409 et 410 sur une base jetable.
---

### Issue 4
**Titre :** Refuser une présence invalide avec le bon code d'erreur
**Priorité :** Must · **Renvoi :** EF1, RG10, RG12, RG15, B2, B4 · **Labels :** backend

**Résultat attendu**
Toutes les erreurs du pointage sortent dans le format imposé, avec le bon code HTTP — aucune stack trace, aucune page d'erreur Spring.

**Critères d'acceptation**
- [ ] Quand le corps de la requête est incomplet, alors le serveur répond `400 { code: "DONNEE_INVALIDE", message }`.
- [ ] Quand le code est inconnu, alors `400 { code: "CODE_INCONNU" }`.
- [ ] Quand le code est expiré, alors `410 { code: "CODE_EXPIRE" }`.
- [ ] Quand la présence existe déjà, alors `409 { code: "DEJA_PRESENT" }`.
- [ ] Quand une erreur survient, alors la réponse ne contient ni stack trace ni trace SQL (B4).
- [ ] Quand une erreur est renvoyée, alors son corps contient exactement les clés `code` et `message` (ENF3).

**Notes**
Un test d'intégration par code de statut. Toutes les exceptions remontent au `@RestControllerAdvice` unique.
---

### Issue 5
**Titre :** Déposer le lien de son exercice
**Priorité :** Must · **Renvoi :** EF3, RG7, Z8, Q12 · **Labels :** backend, frontend

**Résultat attendu**
Un étudiant présent à la session dépose le lien de son exercice ; l'exercice entre dans le circuit de relecture.

**Critères d'acceptation**
- [ ] Quand je dépose un lien valide pour une session où ma présence est enregistrée, alors l'exercice est créé avec le statut « en attente de relecture » ou « en attente d'assignation » (voir issue 6).
- [ ] Quand la session est déjà clôturée, alors `409 { code: "SESSION_CLOTUREE" }` et rien n'est créé (RG7, RG14).
- [ ] Quand je n'ai pas de présence à cette session, alors `409 { code: "PRESENCE_REQUISE" }`.
- [ ] Quand le lien n'est pas une URI valide, alors `400 { code: "LIEN_INVALIDE" }`.
- [ ] Quand je dépose deux fois pour la même session, alors `409 { code: "EXERCICE_DEJA_DEPOSE" }`.
- [ ] Quand la session est ouverte, alors je peux déposer même après l'expiration du code de présence (RG7, Q12, Z4).

**Notes**
`POST /api/exercices` (imposé). Contrainte `UNIQUE (session_id, etudiant_id)` en base (Z8).
---

### Issue 6
**Titre :** Assigner automatiquement un relecteur à chaque exercice déposé
**Priorité :** Must · **Renvoi :** EF4, RG4, RG13, Z1 · **Labels :** backend

**Résultat attendu**
Dès qu'un exercice est déposé, le système désigne au hasard un étudiant présent à la session, différent de l'auteur, pour le relire. Personne n'a besoin de désigner quelqu'un à la main.

**Critères d'acceptation**
- [ ] Quand un exercice est déposé et qu'au moins un étudiant présent autre que l'auteur existe, alors une relecture lui est assignée immédiatement (Q7).
- [ ] Quand le relecteur est assigné, alors il est choisi au hasard parmi les présents, et jamais l'auteur de l'exercice (Q5, Q7).
- [ ] Quand aucun candidat n'est disponible (un seul étudiant présent, ou aucun autre que l'auteur), alors l'exercice reste au statut « en attente d'assignation » et n'est jamais perdu (Z1, RG13).
- [ ] Quand un nouvel étudiant marque sa présence alors qu'un exercice attend un relecteur, alors l'assignation est retentée (RG13).
- [ ] Un exercice n'a jamais deux relecteurs : contrainte `UNIQUE (exercice_id)` sur la relecture (Q6, RG4).

**Notes**
Le tirage au sort est testable : le test unitaire vérifie qu'un auteur n'est jamais son propre relecteur et qu'aucun relecteur n'est assigné hors des présents.
---

### Issue 7
**Titre :** Rendre une note et un commentaire sur l'exercice d'un pair
**Priorité :** Must · **Renvoi :** EF5, RG2, RG3, Q5, Q9 · **Labels :** backend, frontend

**Résultat attendu**
Le relecteur ouvre l'exercice qui lui est assigné, saisit une note entière entre 0 et 20 et un commentaire, puis valide.

**Critères d'acceptation**
- [ ] Quand je saisis une note entière entre 0 et 20 et un commentaire, alors la relecture est enregistrée et la note devient visible par l'étudiant relu (EF5).
- [ ] Quand la note est en dehors de 0 à 20 ou n'est pas entière, alors `400 { code: "NOTE_INVALIDE" }` (RG3).
- [ ] Quand je tente de relire mon propre exercice, alors `403 { code: "AUTO_RELECTURE" }` (Q5, RG2).
- [ ] Quand la relecture est déjà rendue et que la session est clôturée, alors `409 { code: "RELECTURE_DEJA_RENDUE" }`.
- [ ] Quand je ne suis pas le relecteur assigné à cette relecture, alors la modification est refusée.

**Notes**
`POST /api/relectures/{id}` (imposé), `GET /api/relectures/{relectureId}` (propre).
Test unitaire obligatoire sur la règle métier `note entière 0..20` et sur l'interdiction de l'auto-relecture (B6, RG2, RG3).
---

### Issue 8
**Titre :** Voir sa note et son commentaire sans savoir qui a relu
**Priorité :** Must · **Renvoi :** EF6, RG5, Q8 · **Labels :** backend, frontend

**Résultat attendu**
L'étudiant relu consulte sa note et le commentaire reçu, sans jamais pouvoir identifier son relecteur.

**Critères d'acceptation**
- [ ] Quand ma relecture est rendue, alors je vois la note et le commentaire depuis mon écran (EF6).
- [ ] Quand la réponse est inspectée, alors elle ne contient aucun identifiant ni nom de relecteur (RG5, Q8).
- [ ] Quand ma relecture n'est pas encore rendue, alors mon exercice apparaît comme « en attente », sans note (Q11).
- [ ] Quand je n'ai aucun exercice relu, alors l'écran affiche un message d'absence de note, pas une erreur (F3).

**Notes**
`GET /api/etudiants/{etudiantId}/notes-recues` : DTO dédié sans champ relecteur — l'anonymat est garanti par la forme de la réponse, pas par le frontend (B3, RG5).
Un test d'intégration vérifie l'absence du champ dans le JSON.
---

### Issue 9
**Titre :** Voir le tableau récapitulatif de la promotion
**Priorité :** Must · **Renvoi :** EF7, Q16, Z5, ENF2, ENF3 · **Labels :** backend, frontend

**Résultat attendu**
Le formateur voit, en une ligne par étudiant : sa présence, le nombre d'exercices déposés, sa moyenne et les relectures qu'il lui reste à faire.

**Critères d'acceptation**
- [ ] Quand j'affiche le tableau d'une promotion, alors chaque étudiant a une ligne avec `presences`, `exercicesDeposes`, `moyenne` et `relecturesEnAttente` (EF7, Q16).
- [ ] Quand l'étudiant n'a reçu aucune note, alors `moyenne` vaut `null` et le tableau affiche « — » (contrat, nullable).
- [ ] Quand `relecturesEnAttente` est affiché, alors il s'agit des relectures que l'étudiant doit encore faire (Z5).
- [ ] Quand la promotion n'existe pas, alors `404 { code: "PROMOTION_INCONNUE" }`.
- [ ] Quand la promotion compte 60 étudiants, alors le tableau se charge en moins de 2 secondes (ENF2).
- [ ] Quand le chargement échoue, alors l'écran affiche l'erreur et un bouton pour réessayer (F3).

**Notes**
`GET /api/tableau?promotionId=` (imposé). La moyenne est calculée par l'API et affichée telle quelle : aucun recalcul côté frontend (F3).
---

### Issue 10
**Titre :** Voir et rendre ses relectures à faire (écran relecteur)
**Priorité :** Must · **Renvoi :** EF11, Q6, Q7, Q16, F2 · **Labels :** backend, frontend

**Résultat attendu**
Le relecteur ouvre son écran, voit la liste des exercices qu'il doit relire, ouvre l'un d'eux et rend sa note.

**Critères d'acceptation**
- [ ] Quand j'ouvre l'écran « Relecteur » après avoir choisi mon nom, alors je vois les relectures qui me sont assignées et non rendues (EF11, Q16).
- [ ] Quand je clique sur une relecture, alors je vois le lien de l'exercice à relire et le formulaire note + commentaire.
- [ ] Quand je rends ma relecture, alors elle disparaît de la liste des relectures à faire.
- [ ] Quand je n'ai aucune relecture en cours, alors l'écran affiche « aucune relecture à faire » sans erreur (F3).
- [ ] Quand la liste charge, alors un indicateur de chargement est affiché (F3).

**Notes**
`GET /api/etudiants/{etudiantId}/relectures-a-faire` et `GET /api/relectures/{relectureId}` (propres). Ticket Must : le sujet impose trois écrans (F2).
---

### Issue 11
**Titre :** Démarrage en trois commandes avec données de démonstration
**Priorité :** Must · **Renvoi :** ENF5, F1, contrainte « Démarrage » · **Labels :** documentation, infra

**Résultat attendu**
Une personne qui clone le dépôt lance au maximum trois commandes et obtient une application utilisable, avec des données de démonstration, sans avoir de base de données installée.

**Critères d'acceptation**
- [ ] Quand je clone le dépôt sur un poste vierge et que je suis le README, alors trois commandes suffisent à démarrer l'application et la base (ENF5).
- [ ] Quand l'application démarre, alors une promotion, six étudiants et une session ouverte sont déjà présents, avec des exercices déposés et une relecture rendue (démarrage, ENF5).
- [ ] Quand j'ouvre le README, alors le framework frontend est justifié en une ligne (F1).
- [ ] Quand je lance `npm run build`, alors le build frontend passe (F1).
- [ ] Quand je lance `mvn verify`, alors les deux tests passent sans base locale (B6).

**Notes**
`docker compose up` comme commande principale. Données de démo chargées par une migration Flyway dédiée. README testé depuis un clone vierge à l'étape 4.
---

### Issue 12
**Titre :** Bloquer deux minutes après cinq codes erronés
**Priorité :** Should · **Renvoi :** RG10, Q4, Z3 · **Labels :** backend

**Résultat attendu**
Un étudiant qui essaie de deviner les codes est bloqué deux minutes après cinq erreurs, sans pouvoir distinguer un bon code d'un mauvais pendant le blocage.

**Critères d'acceptation**
- [ ] Quand je me trompe cinq fois de code sur une même session, alors toute tentative supplémentaire est refusée pendant deux minutes (RG10).
- [ ] Quand je suis bloqué, alors la réponse est `400 { code: "CODE_INCONNU" }` avec un message qui indique le temps restant : même si le code saisi était le bon, le blocage ne se voit pas (Q4, Z3).
- [ ] Quand les deux minutes sont écoulées, alors je peux réessayer normalement.
- [ ] Quand je saisis un code valide, alors mon compteur d'erreurs est remis à zéro.
- [ ] Le compteur d'erreurs ne survit pas à la session concernée (Z3).

**Notes**
Aucun code HTTP supplémentaire n'est créé, pour rester conforme au contrat imposé (B2). Le compteur est stocké côté serveur, pas dans le navigateur.
---

### Issue 13
**Titre :** Clôturer une session
**Priorité :** Should · **Renvoi :** EF10, RG14, Q10, Q12 · **Labels :** backend, frontend

**Résultat attendu**
Le formateur clôture la session quand le temps de relecture est écoulé : les présences, les dépôts et les notes sont alors figés.

**Critères d'acceptation**
- [ ] Quand je clôture la session, alors plus aucune présence ne peut y être marquée (RG14).
- [ ] Quand je clôture la session, alors plus aucun exercice ne peut y être déposé ni remplacé (RG14).
- [ ] Quand je clôture la session, alors plus aucune relecture ne peut être rendue ni corrigée (RG14, C1).
- [ ] Quand une relecture n'a jamais été rendue à la clôture, alors l'exercice reste visible comme « en attente » dans mon tableau (Q11, Z2).
- [ ] Quand je clôture une session déjà clôturée, alors `409 { code: "SESSION_DEJA_CLOTUREE" }`.

**Notes**
`POST /api/sessions/{sessionId}/cloture`. La clôture est aussi ce qui rend une note définitive : c'est mon arbitrage entre Q10 et Q15 (C1, RG11).
---

### Issue 14
**Titre :** Ajouter une présence à la main, marquée « ajouté par le formateur »
**Priorité :** Should · **Renvoi :** EF8, RG6, Q14 · **Labels :** backend, frontend

**Résultat attendu**
Le formateur peut rattraper un étudiant qui n'a pas pu pointer (téléphone en panne) ; la présence ajoutée est distinguable d'un pointage par l'étudiant.

**Critères d'acceptation**
- [ ] Quand j'ajoute une présence pour un étudiant absent du pointage, alors elle apparaît dans le tableau avec la mention « ajouté par le formateur » (EF8, RG6).
- [ ] Quand la présence est créée par le formateur, alors sa source vaut `FORMATEUR` dans la réponse de l'API (contrat).
- [ ] Quand une présence existe déjà pour ce couple (session, étudiant), alors `409 { code: "DEJA_PRESENT" }` (RG15).
- [ ] Quand la session est clôturée, alors `409 { code: "SESSION_CLOTUREE" }`.
- [ ] Quand l'étudiant ou la session n'existe pas, alors `404 { code: "ETUDIANT_INCONNU" }` ou `404 { code: "SESSION_INCONNUE" }`.

**Notes**
`POST /api/presences/formateur` (propre). Le champ `source` du contrat imposé existe exactement pour ce besoin : relire Q14.
---

### Issue 15
**Titre :** Remplacer le lien de son exercice
**Priorité :** Should · **Renvoi :** EF9, RG8, Q13 · **Labels :** backend, frontend

**Résultat attendu**
Un étudiant qui s'est trompé de lien peut le corriger tant que la relecture n'a pas commencé.

**Critères d'acceptation**
- [ ] Quand je remplace le lien avant que le relecteur n'ait rendu sa relecture, alors le nouveau lien remplace l'ancien et l'assignation du relecteur est conservée (EF9, RG8).
- [ ] Quand la relecture est déjà rendue, alors `409 { code: "RELECTURE_COMMENCEE" }` (Q13).
- [ ] Quand la session est clôturée, alors `409 { code: "SESSION_CLOTUREE" }`.
- [ ] Quand le nouveau lien n'est pas une URI valide, alors `400 { code: "LIEN_INVALIDE" }`.
- [ ] Quand le lien est remplacé, alors le relecteur continue de relire le même exercice : il n'est pas réassigné.

**Notes**
`PUT /api/exercices/{exerciceId}/lien` (propre). L'ancien lien n'est pas conservé dans cette version : décision documentée dans D4 (états-transitions).
---

### Issue 16
**Titre :** Corriger une note avant la clôture de la session
**Priorité :** Could · **Renvoi :** EF12, RG11, C1 (Q10 retenu contre Q15) · **Labels :** backend

**Résultat attendu**
Un relecteur qui s'est trompé de note peut la corriger tant que le formateur n'a pas clôturé la session.

**Critères d'acceptation**
- [ ] Quand je renvoie une note pour une relecture que j'ai déjà rendue et que la session n'est pas clôturée, alors la nouvelle note remplace l'ancienne (EF12, RG11).
- [ ] Quand la session est clôturée, alors `409 { code: "RELECTURE_DEJA_RENDUE" }` : la note est définitive (Q15, RG14).
- [ ] Quand je corrige la note, alors la moyenne du tableau du formateur change en conséquence au prochain chargement.
- [ ] Quand la note corrigée est hors de 0 à 20, alors `400 { code: "NOTE_INVALIDE" }`.

**Notes**
C'est la conséquence directe de mon arbitrage C1 en section 7 du cahier des charges : Q10 (correction possible jusqu'à la clôture) l'emporte sur Q15 (note définitive), et la clôture redevient le seul point de figement.
---

### Issue 17
**Titre :** Supprimer une présence ajoutée par erreur
**Priorité :** Could · **Renvoi :** Z7 · **Labels :** backend, frontend

**Résultat attendu**
Le formateur qui s'est trompé d'étudiant en ajoutant une présence à la main peut la retirer.

**Critères d'acceptation**
- [ ] Quand je supprime une présence que j'ai ajoutée moi-même, alors elle disparaît du tableau et du pointage de la session.
- [ ] Quand je supprime une présence auto-déclarée par un étudiant, alors l'opération est refusée : seul l'ajout manuel peut être retiré.
- [ ] Quand la session est clôturée, alors toute suppression est refusée (RG14).
- [ ] Quand je supprime une présence alors qu'un exercice a déjà été déposé pour cette session, alors le dépôt est conservé et signalé comme « déposé sans présence » dans le tableau.

**Notes**
Cette limite est documentée comme exclue du périmètre dans le cahier des charges (Z7) : elle n'est traitée que si les Must et les Should sont terminés.
