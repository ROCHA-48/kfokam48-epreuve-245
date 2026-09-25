package com.kfokam48.presences.web.dto;

import com.kfokam48.presences.domain.Promotion;

public record PromotionResponse(Long id, String nom) {

    public static PromotionResponse depuis(Promotion promotion) {
        return new PromotionResponse(promotion.getId(), promotion.getNom());
    }
}
