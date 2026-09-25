package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/**
 * RG3 : la note est un entier de 0 a 20 (Q9). Le champ est un BigDecimal et non un
 * Integer pour refuser proprement une note non entiere (12.5) avec le code
 * NOTE_INVALIDE du contrat, au lieu d'une erreur de lecture du JSON.
 */
public record RelectureRequest(
        @NotNull(message = "la note est obligatoire") BigDecimal note,
        @NotNull(message = "le commentaire est obligatoire") String commentaire) {
}
