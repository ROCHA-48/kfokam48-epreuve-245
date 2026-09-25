package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Relecture;

import java.time.Instant;

/** RG5 / Q8 : la note et le commentaire, jamais l'identite du relecteur. */
public record NoteRecueResponse(Long relectureId, Long exerciceId, Integer note, String commentaire, Instant rendueAt) {

    public static NoteRecueResponse depuis(Relecture relecture) {
        return new NoteRecueResponse(relecture.getId(), relecture.getExercice().getId(), relecture.getNote(),
                relecture.getCommentaire(), relecture.getRendueAt());
    }
}
