package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PresenceFormateurRequest(
        @NotNull(message = "la session est obligatoire") @Positive Long sessionId,
        @NotNull(message = "l'etudiant est obligatoire") @Positive Long etudiantId) {
}
