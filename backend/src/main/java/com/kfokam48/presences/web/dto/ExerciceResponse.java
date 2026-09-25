package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Exercice;

/** Reponse imposee par le contrat pour POST /api/exercices. */
public record ExerciceResponse(Long id, String statut) {

    public static ExerciceResponse depuis(Exercice exercice) {
        return new ExerciceResponse(exercice.getId(), exercice.getStatut().name());
    }
}
