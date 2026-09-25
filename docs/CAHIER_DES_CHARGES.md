# Cahier des charges — <nom de ton application>

**Auteur :** ROCHA Wilson · KF48-DLA-245
**Version :** 1 · **Date :** <date>
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
| Formateur | | |
| Étudiant | | |
| Relecteur | | |

*Le relecteur est-il un acteur distinct ou un étudiant dans un certain état ? Ta réponse a des conséquences sur ton modèle de données. Tranche-la ici.*

## 3. Périmètre

**Inclus dans cette version :**
-

**Explicitement exclu :**
-

*Ce que tu exclus compte autant que ce que tu inclus. Un périmètre sans exclusion n'est pas un périmètre.*

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
