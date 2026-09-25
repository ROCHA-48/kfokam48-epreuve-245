package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.SessionCours;

import java.time.Instant;

/** Reponse imposee par le contrat pour POST /api/sessions. */
public record SessionResponse(Long id, String code, Instant ouvertureAt, Instant expirationAt) {

    public static SessionResponse depuis(SessionCours session) {
        return new SessionResponse(session.getId(), session.getCode(), session.getOuvertureAt(),
                session.getExpirationAt());
    }
}
