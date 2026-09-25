package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Etudiant;

public record EtudiantResponse(Long id, String nom, Long promotionId) {

    public static EtudiantResponse depuis(Etudiant etudiant) {
        return new EtudiantResponse(etudiant.getId(), etudiant.getNom(), etudiant.getPromotion().getId());
    }
}
