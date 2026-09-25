package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Relecture;

import java.time.Instant;

/** Aucune information sur l'auteur de l'exercice : l'anonymat va dans les deux sens. */
public record RelectureAFaireResponse(Long relectureId, Long exerciceId, Long sessionId, String lien, String statut,
                                      Instant assigneeAt) {

    public static RelectureAFaireResponse depuis(Relecture relecture) {
        return new RelectureAFaireResponse(relecture.getId(), relecture.getExercice().getId(),
                relecture.getExercice().getSession().getId(), relecture.getExercice().getLien(),
                relecture.getExercice().getStatut().name(), relecture.getAssigneeAt());
    }
}
