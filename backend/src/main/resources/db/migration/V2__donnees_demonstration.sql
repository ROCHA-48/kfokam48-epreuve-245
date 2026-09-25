-- Donnees de demonstration : le correcteur ouvre une application utilisable,
-- avec une session en cours, des presences, des exercices et une relecture rendue.

INSERT INTO promotion (nom) VALUES ('KF48-2026');

INSERT INTO etudiant (nom, email, promotion_id)
SELECT v.nom, v.email, p.id
FROM (VALUES
        ('Awa Nkolo', 'awa.nkolo@kfokam48.cm'),
        ('Brice Fotso', 'brice.fotso@kfokam48.cm'),
        ('Cedric Mbarga', 'cedric.mbarga@kfokam48.cm'),
        ('Diane Kamga', 'diane.kamga@kfokam48.cm'),
        ('Elsa Ngo Bell', 'elsa.ngobell@kfokam48.cm'),
        ('Franck Oumarou', 'franck.oumarou@kfokam48.cm')
     ) AS v (nom, email)
CROSS JOIN promotion p
WHERE p.nom = 'KF48-2026';

-- Session ouverte il y a 2 minutes : le code est encore valide 13 minutes (RG1)
INSERT INTO session_cours (titre, code, promotion_id, ouverture_at, expiration_at)
SELECT 'Cours de Java - seance du jour', 'K7M2QD', p.id, now() - interval '2 minutes', now() + interval '13 minutes'
FROM promotion p
WHERE p.nom = 'KF48-2026';

-- Presences auto-declarees (Q2) et une presence ajoutee par le formateur (Q14, RG6)
INSERT INTO presence (session_id, etudiant_id, source, ajoute_at)
SELECT s.id, e.id, 'ETUDIANT', now() - interval '1 minute'
FROM session_cours s
JOIN etudiant e ON e.nom IN ('Awa Nkolo', 'Brice Fotso', 'Cedric Mbarga')
WHERE s.code = 'K7M2QD';

INSERT INTO presence (session_id, etudiant_id, source, ajoute_at)
SELECT s.id, e.id, 'FORMATEUR', now() - interval '1 minute'
FROM session_cours s
JOIN etudiant e ON e.nom = 'Diane Kamga'
WHERE s.code = 'K7M2QD';

-- Trois exercices : un relu, un en attente de relecture, un sans relecteur (Z1)
INSERT INTO exercice (session_id, etudiant_id, lien, statut, depose_at)
SELECT s.id, e.id, 'https://github.com/ROCHA-48/exercice-awa', 'RELU', now() - interval '50 seconds'
FROM session_cours s JOIN etudiant e ON e.nom = 'Awa Nkolo' WHERE s.code = 'K7M2QD';

INSERT INTO exercice (session_id, etudiant_id, lien, statut, depose_at)
SELECT s.id, e.id, 'https://github.com/ROCHA-48/exercice-brice', 'EN_ATTENTE_RELECTURE', now() - interval '40 seconds'
FROM session_cours s JOIN etudiant e ON e.nom = 'Brice Fotso' WHERE s.code = 'K7M2QD';

INSERT INTO exercice (session_id, etudiant_id, lien, statut, depose_at)
SELECT s.id, e.id, 'https://github.com/ROCHA-48/exercice-cedric', 'EN_ATTENTE_ASSIGNATION', now() - interval '30 seconds'
FROM session_cours s JOIN etudiant e ON e.nom = 'Cedric Mbarga' WHERE s.code = 'K7M2QD';

-- Une relecture rendue (note + commentaire) et une relecture a faire
INSERT INTO relecture (exercice_id, relecteur_id, note, commentaire, assignee_at, rendue_at)
SELECT x.id, r.id, 16, 'Bon decoupage, les cas d erreur sont traites. Il manque juste les tests.',
       now() - interval '35 seconds', now() - interval '20 seconds'
FROM exercice x
JOIN etudiant a ON a.id = x.etudiant_id
JOIN etudiant r ON r.nom = 'Brice Fotso'
WHERE a.nom = 'Awa Nkolo';

INSERT INTO relecture (exercice_id, relecteur_id, assignee_at)
SELECT x.id, r.id, now() - interval '25 seconds'
FROM exercice x
JOIN etudiant a ON a.id = x.etudiant_id
JOIN etudiant r ON r.nom = 'Diane Kamga'
WHERE a.nom = 'Brice Fotso';
