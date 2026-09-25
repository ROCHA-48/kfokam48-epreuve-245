package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;

public record ExerciceRequest(
        @NotNull(message = "la session est obligatoire") @Positive Long sessionId,
        @NotNull(message = "l'etudiant est obligatoire") @Positive Long etudiantId,
        @NotBlank(message = "le lien est obligatoire")
        @Pattern(regexp = "^https?://.+", message = "le lien doit commencer par http:// ou https://") String lien) {
}
