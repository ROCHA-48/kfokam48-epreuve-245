package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.Presence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PresenceRepository extends JpaRepository<Presence, Long> {

    Optional<Presence> findBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    List<Presence> findBySessionId(Long sessionId);

    boolean existsBySessionIdAndEtudiantId(Long sessionId, Long etudiantId);

    /** Nombre de presences d'un etudiant sur toutes les sessions de sa promotion (Q16). */
    @Query("""
            select count(p) from Presence p
            where p.etudiant.id = :etudiantId and p.session.promotion.id = :promotionId
            """)
    long compterPresences(@Param("etudiantId") Long etudiantId, @Param("promotionId") Long promotionId);
}
