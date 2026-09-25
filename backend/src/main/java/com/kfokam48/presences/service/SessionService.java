package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Promotion;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.repository.SessionCoursRepository;
import com.kfokam48.presences.web.erreur.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class SessionService {

    private final SessionCoursRepository sessionRepository;
    private final PromotionRepository promotionRepository;
    private final GenerationCode generationCode;
    private final Duration validiteCode;

    public SessionService(SessionCoursRepository sessionRepository, PromotionRepository promotionRepository,
                          GenerationCode generationCode,
                          @Value("${app.presence.validite-code-minutes:15}") long validiteMinutes) {
        this.sessionRepository = sessionRepository;
        this.promotionRepository = promotionRepository;
        this.generationCode = generationCode;
        this.validiteCode = Duration.ofMinutes(validiteMinutes);
    }

    /** EF2 + RG1 : le code expire exactement 15 minutes apres l'ouverture. */
    @Transactional
    public SessionCours ouvrir(String titre, Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> ApiException.promotionInconnue(promotionId));

        Instant ouverture = Instant.now();
        SessionCours session = new SessionCours();
        session.setTitre(titre.trim());
        session.setPromotion(promotion);
        session.setOuvertureAt(ouverture);
        session.setExpirationAt(ouverture.plus(validiteCode));
        session.setCode(genererCodeUnique());
        return sessionRepository.save(session);
    }

    @Transactional(readOnly = true)
    public SessionCours detail(Long sessionId) {
        return sessionRepository.findById(sessionId).orElseThrow(() -> ApiException.sessionInconnue(sessionId));
    }

    @Transactional(readOnly = true)
    public List<SessionCours> listerParPromotion(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw ApiException.promotionInconnue(promotionId);
        }
        return sessionRepository.findByPromotionIdOrderByOuvertureAtDesc(promotionId);
    }

    /** EF10 + RG14 : la cloture est definitive, elle ferme pointage, depot et relecture. */
    @Transactional
    public SessionCours cloturer(Long sessionId) {
        SessionCours session = detail(sessionId);
        if (session.isCloturee()) {
            throw ApiException.sessionDejaCloturee();
        }
        session.setClotureAt(Instant.now());
        return sessionRepository.save(session);
    }

    private String genererCodeUnique() {
        for (int essai = 0; essai < 50; essai++) {
            String code = generationCode.generer();
            if (!sessionRepository.existsByCode(code)) {
                return code;
            }
        }
        throw new IllegalStateException("Impossible de generer un code de presence unique");
    }
}
