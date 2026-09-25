package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Relecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    /**
     * Issue #19 : un exercice peut avoir deux relectures. Les usages « y a-t-il
     * deja une relecture pour cet exercice ? » prennent la premiere (par identifiant
     * croissant) : suffit pour « relecture commencee ? » (remplacement de lien) et
     * pour le detail par defaut.
     */
    Optional<Relecture> findFirstByExerciceIdOrderByIdAsc(Long exerciceId);

    /** Issue #19 : les relectures deja posees sur cet exercice (au plus deux). */
    List<Relecture> findByExerciceIdOrderByIdAsc(Long exerciceId);

    /**
     * Issue #19 : la nouvelle tentative d'assignation ne vise que le premier
     * relecteur manquant — la contrainte UNIQUE (exercice_id, relecteur_id)
     * reste la garantie anti-doublon, et le second relecteur est toujours
     * distinct du premier et de l'auteur (choisi dans le service).
     */
    boolean existsByExerciceIdAndRelecteurId(Long exerciceId, Long relecteurId);

    /**
     * Detail d'une relecture : l'exercice est charge en meme temps (join fetch),
     * sinon le lien de l'exercice n'est plus lisible une fois la transaction fermee.
     */
    @Query("select r from Relecture r join fetch r.exercice e join fetch e.session s where r.id = :id")
    Optional<Relecture> trouverDetail(@Param("id") Long id);

    /** Les relectures que cet etudiant doit encore faire (Q16, EF11). */
    @Query("""
            select r from Relecture r
            join fetch r.exercice e
            where r.relecteur.id = :etudiantId and r.rendueAt is null
            order by r.assigneeAt asc
            """)
    List<Relecture> relecturesAFaire(@Param("etudiantId") Long etudiantId);

    /** Les notes recues par cet etudiant, sans jamais exposer le relecteur (Q8, RG5). */
    @Query("""
            select r from Relecture r
            join fetch r.exercice e
            where e.etudiant.id = :etudiantId and r.rendueAt is not null
            order by r.rendueAt desc
            """)
    List<Relecture> notesRecues(@Param("etudiantId") Long etudiantId);

    /** Issue #19 : moyenne des notes recues — chaque relecture rendue compte, donc deux relecteurs pèsent deux fois ; null si aucune note (contrat : nullable). */
    @Query("""
            select avg(r.note) from Relecture r
            where r.exercice.etudiant.id = :etudiantId
              and r.exercice.session.promotion.id = :promotionId
              and r.rendueAt is not null
            """)
    Double moyenneRecue(@Param("etudiantId") Long etudiantId, @Param("promotionId") Long promotionId);

    /** Nombre de relectures que l'etudiant doit encore faire (Q16, Z5). */
    @Query("""
            select count(r) from Relecture r
            where r.relecteur.id = :etudiantId and r.rendueAt is null
            """)
    long compterRelecturesAFaire(@Param("etudiantId") Long etudiantId);

    List<Relecture> findByExerciceSessionIdAndRendueAtIsNull(Long sessionId);

    List<Relecture> findByRelecteurId(Long relecteurId);

    /** Issue #19 : combien de relecteurs sont assignes a cet exercice (un ou deux). */
    long countByExerciceId(Long exerciceId);

    /** Issue #19 : combien ont deja rendu — la note est provisoire tant que ce compte est inferieur au precedent. */
    long countByExerciceIdAndRendueAtIsNotNull(Long exerciceId);
}
