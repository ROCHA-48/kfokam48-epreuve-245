package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Relecture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RelectureRepository extends JpaRepository<Relecture, Long> {

    Optional<Relecture> findByExerciceId(Long exerciceId);

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

    /** Moyenne des notes recues sur la promotion, null si aucune note (contrat : nullable). */
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
}
