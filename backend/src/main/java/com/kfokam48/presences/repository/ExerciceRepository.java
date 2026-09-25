package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.StatutExercice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    Optional<Exercice> findBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Exercice> findBySessionId(Long sessionId);

    List<Exercice> findByStatutAndSessionId(StatutExercice statut, Long sessionId);

    @Query("""
            select count(e) from Exercice e
            where e.etudiant.id = :etudiantId and e.session.promotion.id = :promotionId
            """)
    long compterExercices(@Param("etudiantId") Long etudiantId, @Param("promotionId") Long promotionId);
}
