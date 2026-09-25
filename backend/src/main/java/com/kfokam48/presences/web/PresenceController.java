package com.kfokam48.presences.web;

import com.kfokam48.presences.service.PresenceService;
import com.kfokam48.presences.web.dto.PresenceFormateurRequest;
import com.kfokam48.presences.web.dto.PresenceRequest;
import com.kfokam48.presences.web.dto.PresenceResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/presences")
public class PresenceController {

    private final PresenceService presenceService;

    public PresenceController(PresenceService presenceService) {
        this.presenceService = presenceService;
    }

    /** Operation imposee : POST /api/presences -> 201, erreurs 400 / 409 / 410. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PresenceResponse pointer(@Valid @RequestBody PresenceRequest requete) {
        return PresenceResponse.depuis(presenceService.pointer(requete.code(), requete.etudiantId()));
    }

    /** Q14 / RG6 : presence ajoutee a la main par le formateur. */
    @PostMapping("/formateur")
    @ResponseStatus(HttpStatus.CREATED)
    public PresenceResponse ajouterParFormateur(@Valid @RequestBody PresenceFormateurRequest requete) {
        return PresenceResponse.depuis(
                presenceService.ajouterParFormateur(requete.sessionId(), requete.etudiantId()));
    }
}
