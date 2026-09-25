package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Relecture;

import java.time.Instant;

public record RelectureDetailResponse(Long id, Long exerciceId, String lien, String statut, Integer note,
                                      String commentaire, Instant assigneeAt, Instant rendueAt) {

    public static RelectureDetailResponse depuis(Relecture relecture) {
        return new RelectureDetailResponse(relecture.getId(), relecture.getExercice().getId(),
                relecture.getExercice().getLien(), relecture.getExercice().getStatut().name(), relecture.getNote(),
                relecture.getCommentaire(), relecture.getAssigneeAt(), relecture.getRendueAt());
    }
}
