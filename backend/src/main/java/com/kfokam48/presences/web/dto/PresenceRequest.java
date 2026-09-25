package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PresenceRequest(
        @NotBlank(message = "le code est obligatoire") String code,
        @NotNull(message = "l'etudiant est obligatoire") @Positive(message = "identifiant d'etudiant invalide") Long etudiantId) {
}
