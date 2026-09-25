package com.kfokam48.presences.repository;

import com.kfokam48.presences.domain.SessionCours;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SessionCoursRepository extends JpaRepository<SessionCours, Long> {

    Optional<SessionCours> findByCode(String code);

    boolean existsByCode(String code);

    List<SessionCours> findByPromotionIdOrderByOuvertureAtDesc(Long promotionId);
}
