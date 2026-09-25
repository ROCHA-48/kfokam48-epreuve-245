-- Issue #19 : changement de besoin du client — chaque exercice est relu par deux
-- pairs distincts, la note retenue est la moyenne des deux ; si un seul a rendu,
-- sa note s'affiche marquée provisoire. Cette migration s'AJOUTE, la V1 n'est
-- jamais modifiée (B5). Les données existantes survivent : les relectures déjà
-- rendues restent, elles deviennent « provisoires » tant que la seconde manque.

ALTER TABLE relecture DROP CONSTRAINT uk_relecture_exercice;
ALTER TABLE relecture ADD CONSTRAINT uk_relecture_exercice_relecteur UNIQUE (exercice_id, relecteur_id);
