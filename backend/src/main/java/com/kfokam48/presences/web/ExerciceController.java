package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.service.ExerciceService;
import com.kfokam48.presences.web.dto.ExerciceDetailResponse;
import com.kfokam48.presences.web.dto.ExerciceRequest;
import com.kfokam48.presences.web.dto.ExerciceResponse;
import com.kfokam48.presences.web.dto.LienRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/exercices")
public class ExerciceController {

    private final ExerciceService exerciceService;

    public ExerciceController(ExerciceService exerciceService) {
        this.exerciceService = exerciceService;
    }

    /** Operation imposee : POST /api/exercices -> 201 { id, statut }. */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExerciceResponse deposer(@Valid @RequestBody ExerciceRequest requete) {
        Exercice exercice = exerciceService.deposer(requete.sessionId(), requete.etudiantId(), requete.lien());
        return ExerciceResponse.depuis(exercice);
    }

    /** Q13 / RG8 : remplacer le lien tant que la relecture n'a pas commence. */
    @PutMapping("/{exerciceId}/lien")
    public ExerciceDetailResponse remplacerLien(@PathVariable Long exerciceId, @Valid @RequestBody LienRequest requete) {
        return ExerciceDetailResponse.depuis(exerciceService.remplacerLien(exerciceId, requete.lien()));
    }
}
