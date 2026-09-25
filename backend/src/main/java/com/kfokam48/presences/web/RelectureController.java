package com.kfokam48.presences.web;

import com.kfokam48.presences.service.RelectureService;
import com.kfokam48.presences.web.dto.RelectureDetailResponse;
import com.kfokam48.presences.web.dto.RelectureRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/relectures")
public class RelectureController {

    private final RelectureService relectureService;

    public RelectureController(RelectureService relectureService) {
        this.relectureService = relectureService;
    }

    /** Operation imposee : POST /api/relectures/{id} -> 200, erreurs 400 / 403 / 409. */
    @PostMapping("/{id}")
    public RelectureDetailResponse rendre(@PathVariable Long id, @Valid @RequestBody RelectureRequest requete) {
        return RelectureDetailResponse.depuis(
                relectureService.rendre(id, requete.note(), requete.commentaire()));
    }

    @GetMapping("/{id}")
    public RelectureDetailResponse detail(@PathVariable Long id) {
        return RelectureDetailResponse.depuis(relectureService.detail(id));
    }
}
