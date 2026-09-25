# Cahier des charges — <nom de ton application>

**Auteur :** ROCHA Wilson · 245
**Version :** 1 · **Date :** 25/09/2026
**Frontend choisi :** React, parce que c'est le framework que je maîtrise le mieux et qui permet d'aller vite sur les trois écrans demandés.

> Ce squelette est à remplir, pas à recopier. Les dix sections sont imposées et dans cet ordre.
> Tout ce qui reste en `<...>` ou en italique à la remise compte pour zéro.

---

## 1. Contexte et objectif

Aujourd'hui, à KFOKAM48, la présence des étudiants est gérée manuellement : chacun émarge sur une liste papier, que le formateur doit ensuite recompter et ressaisir à la main pour savoir qui était présent. Il n'existe aucune trace fiable en cas de contestation, et impossible de croiser rapidement présence, exercices et notes pour avoir une vue d'ensemble sur un étudiant.

Du côté des exercices, les étudiants s'échangent leurs travaux et leurs retours entre eux de façon informelle, probablement par WhatsApp ou email. Rien ne garantit qu'un exercice est relu une seule fois, par une personne différente de son auteur, ni que le formateur puisse savoir qui a déposé, qui a été relu et qui attend encore une relecture.

Cette application s'adresse au formateur et à l'étudiant. Pour le formateur, elle offre une vue d'ensemble en un coup d'œil : présence, dépôts, moyennes et relectures en attente, sans recouper plusieurs sources. Pour l'étudiant, elle remplace un processus manuel par un geste simple et rapide — saisir un code, déposer un lien — avec la garantie que sa relecture reste anonyme et équitable.

## 2. Acteurs et rôles

| Acteur | Ce qu'il peut faire | Ce qu'il ne peut pas faire |
|---|---|---|
| **Formateur** | Ouvrir une session et obtenir un code de présence (Q2) · Voir le tableau récapitulatif par étudiant (Q16) · Ajouter une présence manuellement, marquée « ajouté par le formateur » (Q14) · Clôturer une session | Modifier une note envoyée par un relecteur (Q15) · Relire un exercice à la place d'un étudiant |
| **Étudiant** | Marquer sa présence avec un code, tant qu'il est valide (Q2, Q3) · Déposer le lien de son exercice, et le remplacer tant que personne ne l'a relu (Q13) · Voir sa note et le commentaire reçu, sans connaître l'identité du relecteur (Q8) | Se relire lui-même (Q5) · Choisir qui le relit (Q7) · Voir le nom de son relecteur (Q8) · Marquer sa présence après expiration du code (Q2) |
| **Relecteur** *(= un étudiant, désigné au hasard parmi les présents pour relire l'exercice d'un pair — Q7)* | Rendre une note (0–20, entière) et un commentaire (Q9) · Corriger sa note tant que la session n'est pas clôturée *(→ voir décision en section 7, contradiction Q10/Q15)* | Relire son propre exercice (Q5) · Relire plusieurs exercices pour le même exercice (un seul relecteur — Q6) |

Le relecteur n'est pas un acteur distinct : c'est un étudiant, désigné temporairement par le système pour relire l'exercice d'un pair (Q7). Il n'existe pas de compte "relecteur" séparé — une même personne est tour à tour étudiant qui dépose, et relecteur d'un autre exercice.

## 3. Périmètre

**Inclus dans cette version :**
- Ouverture d'une session de cours par le formateur, avec génération d'un code de présence (Q2)
- Marquage de la présence par l'étudiant via ce code, et ajout manuel par le formateur si besoin (Q14)
- Dépôt du lien d'un exercice par l'étudiant, avec possibilité de le remplacer tant que personne ne l'a relu (Q13)
- Assignation automatique et aléatoire d'un relecteur parmi les étudiants présents (Q7)
- Relecture : note sur 20 (entière) et commentaire, correction possible tant que la session n'est pas clôturée
- Tableau récapitulatif du formateur : présence, dépôts, moyenne, relectures en attente (Q16)

**Explicitement exclu :**
- L'authentification par mot de passe (Q1 — le client a demandé explicitement de ne pas s'en soucier)
- Le design/l'esthétique de l'interface (le sujet précise : « Le rendu visuel n'est pas noté. Aucun point pour le CSS »)
- La notification automatique (email/SMS) des étudiants ou du formateur
- La gestion de plusieurs formateurs ou de droits différenciés entre formateurs
- L'export du tableau (PDF, Excel...) — le formateur consulte les données uniquement à l'écran
- La modification ou suppression d'une session déjà ouverte, autre que sa clôture

## 4. Exigences fonctionnelles

| Réf | Exigence | Critère d'acceptation | Priorité |
|---|---|---|---|
| EF1 | L'étudiant marque sa présence à l'aide d'un code | Quand je saisis un code valide et non expiré, ma présence apparaît dans le tableau du formateur | Must |
| EF2 | | | |
| EF3 | | | |

*Un critère d'acceptation se formule « quand … alors … ». S'il n'est pas vérifiable par quelqu'un d'autre que toi, ce n'en est pas un.*

## 5. Exigences non fonctionnelles

| Réf | Exigence | Comment on la vérifie |
|---|---|---|
| ENF1 | L'interface de marquage de présence est utilisable sur un téléphone | |
| ENF2 | Le tableau du formateur répond en moins de 2 s pour une promotion de 60 étudiants | |

## 6. Règles de gestion

| Réf | Règle | Source |
|---|---|---|
| RG1 | Un code de présence expire 15 minutes après l'ouverture de la session | Q2 |
| RG2 | Un étudiant ne peut pas relire son propre exercice | Q5 |
| RG3 | Une note est un entier compris entre 0 et 20 | Q9 |
| RG4 | | |

*Numérote-les. Tu les citeras dans tes issues, tes messages de commit et tes tests. Une règle qu'on ne peut pas citer est une règle qu'on oublie.*

## 7. Zones d'ombre, hypothèses et contradictions

**Points que la demande ne tranche pas :**

| Point | Réponse client (Qx) ou hypothèse | Décision retenue | Conséquence |
|---|---|---|---|
| | | | |

**Contradictions relevées :**

| Réponses en conflit | Ce que j'ai choisi | Pourquoi |
|---|---|---|
| | | |

*Une hypothèse écrite est toujours acceptée. Une hypothèse silencieuse est une faute.*

## 8. Contraintes techniques

*Reprends les contraintes B1 à B6 et F1 à F3 du sujet, et ajoute celles que tu t'imposes toi-même (base de données choisie, gestion des migrations, stratégie de tests).*

## 9. Livrables

-

## 10. Démarche prévue

*Comment tu comptes mener les six étapes : dans quel ordre, ce que tu vises à chaque jalon, ce que tu feras si tu prends du retard.*

**Definition of Done — un ticket est terminé quand :**
-
-

---

## Journal des révisions

| Version | Quand | Ce qui a changé et pourquoi |
|---|---|---|
| 1 | | Version initiale |

*L'étape 3 rendra une partie de ce document faux. Reviens le corriger et note-le ici — un cahier des charges périmé est un cahier des charges mort.*
