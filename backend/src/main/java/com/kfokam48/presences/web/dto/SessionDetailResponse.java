package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.SessionCours;

import java.time.Instant;

public record SessionDetailResponse(Long id, String titre, String code, Long promotionId, Instant ouvertureAt,
                                    Instant expirationAt, Instant clotureAt, boolean cloturee) {

    public static SessionDetailResponse depuis(SessionCours session) {
        return new SessionDetailResponse(session.getId(), session.getTitre(), session.getCode(),
                session.getPromotion().getId(), session.getOuvertureAt(), session.getExpirationAt(),
                session.getClotureAt(), session.isCloturee());
    }
}
