package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Exercice;

import java.time.Instant;

public record ExerciceDetailResponse(Long id, Long sessionId, Long etudiantId, String lien, String statut,
                                     Instant deposeAt, Instant majLienAt) {

    public static ExerciceDetailResponse depuis(Exercice exercice) {
        return new ExerciceDetailResponse(exercice.getId(), exercice.getSession().getId(),
                exercice.getEtudiant().getId(), exercice.getLien(), exercice.getStatut().name(),
                exercice.getDeposeAt(), exercice.getMajLienAt());
    }
}
