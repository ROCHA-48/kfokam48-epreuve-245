package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.StatutExercice;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExerciceRepository extends JpaRepository<Exercice, Long> {

    Optional<Exercice> findBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Exercice> findBySessionId(Long sessionId);

    List<Exercice> findByStatutAndSessionId(StatutExercice statut, Long sessionId);

    /**
     * Issue #18 : verrou pessimiste — les deux pointages simultanes qui declenchent
     * l'assignation du meme exercice en attente se serialisent ici. Le second
     * transaction relit le statut apres verrou : il ne tente plus une assignation
     * deja faite, donc la contrainte uk_relecture_exercice ne casse plus sa presence.
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select e from Exercice e where e.statut = :statut and e.session.id = :sessionId")
    List<Exercice> lockerLesExercicesEnAttente(@Param("statut") StatutExercice statut, @Param("sessionId") Long sessionId);

    @Query("""
            select count(e) from Exercice e
            where e.etudiant.id = :etudiantId and e.session.promotion.id = :promotionId
            """)
    long compterExercices(@Param("etudiantId") Long etudiantId, @Param("promotionId") Long promotionId);
}
