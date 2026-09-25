package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.SessionCours;

import java.time.Instant;

public record ClotureResponse(Long id, Instant clotureAt, boolean cloturee) {

    public static ClotureResponse depuis(SessionCours session) {
        return new ClotureResponse(session.getId(), session.getClotureAt(), session.isCloturee());
    }
}
