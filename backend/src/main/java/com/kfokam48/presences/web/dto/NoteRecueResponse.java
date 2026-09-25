package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Relecture;

import java.time.Instant;

/**
 * RG5 / Q8 : la note et le commentaire, jamais l'identite du relecteur.
 * Issue #19 : provisoire vaut vrai tant que l'autre relecteur de l'exercice
 * n'a pas rendu sa note — la note retenue sera la moyenne des deux.
 */
public record NoteRecueResponse(Long relectureId, Long exerciceId, Integer note, String commentaire,
                                Instant rendueAt, boolean provisoire) {

    public static NoteRecueResponse depuis(Relecture relecture, boolean provisoire) {
        return new NoteRecueResponse(relecture.getId(), relecture.getExercice().getId(), relecture.getNote(),
                relecture.getCommentaire(), relecture.getRendueAt(), provisoire);
    }
}
