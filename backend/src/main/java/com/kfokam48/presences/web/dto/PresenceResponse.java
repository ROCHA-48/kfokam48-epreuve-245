package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Presence;

/** Reponse imposee par le contrat pour POST /api/presences. */
public record PresenceResponse(Long id, Long sessionId, Long etudiantId, String source) {

    public static PresenceResponse depuis(Presence presence) {
        return new PresenceResponse(presence.getId(), presence.getSession().getId(),
                presence.getEtudiant().getId(), presence.getSource().name());
    }
}
