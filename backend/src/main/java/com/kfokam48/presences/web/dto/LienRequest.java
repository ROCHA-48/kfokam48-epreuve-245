package com.kfokam48.presences.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LienRequest(
        @NotBlank(message = "le lien est obligatoire")
        @Pattern(regexp = "^https?://.+", message = "le lien doit commencer par http:// ou https://") String lien) {
}
