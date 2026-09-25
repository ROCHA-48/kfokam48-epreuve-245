package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.service.ExerciceService;
import com.kfokam48.presences.service.SessionService;
import com.kfokam48.presences.web.dto.ClotureResponse;
import com.kfokam48.presences.web.dto.ExerciceDetailResponse;
import com.kfokam48.presences.web.dto.SessionDetailResponse;
import com.kfokam48.presences.web.dto.SessionRequest;
import com.kfokam48.presences.web.dto.SessionResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;
    private final ExerciceService exerciceService;

    public SessionController(SessionService sessionService, ExerciceService exerciceService) {
        this.sessionService = sessionService;
        this.exerciceService = exerciceService;
    }

    /** Operation imposee : POST /api/sessions -> 201 { id, code, ouvertureAt, expirationAt }. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessionResponse ouvrir(@Valid @RequestBody SessionRequest requete) {
        SessionCours session = sessionService.ouvrir(requete.titre(), requete.promotionId());
        return SessionResponse.depuis(session);
    }

    @GetMapping
    public List<SessionDetailResponse> listerParPromotion(@RequestParam Long promotionId) {
        return sessionService.listerParPromotion(promotionId).stream().map(SessionDetailResponse::depuis).toList();
    }

    @GetMapping("/{sessionId}")
    public SessionDetailResponse detail(@PathVariable Long sessionId) {
        return SessionDetailResponse.depuis(sessionService.detail(sessionId));
    }

    /** RG14 : cloture definitive de la session. */
    @PostMapping("/{sessionId}/cloture")
    public ClotureResponse cloturer(@PathVariable Long sessionId) {
        return ClotureResponse.depuis(sessionService.cloturer(sessionId));
    }

    @GetMapping("/{sessionId}/exercices")
    public List<ExerciceDetailResponse> exercices(@PathVariable Long sessionId) {
        return exerciceService.listerParSession(sessionId).stream().map(ExerciceDetailResponse::depuis).toList();
    }
}
