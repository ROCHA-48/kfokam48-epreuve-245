package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SessionRequest(
        @NotBlank(message = "le titre est obligatoire") String titre,
        @NotNull(message = "la promotion est obligatoire") @Positive(message = "identifiant de promotion invalide") Long promotionId) {
}
