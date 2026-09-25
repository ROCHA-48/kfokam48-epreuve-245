package com.kfokam48.presences.web;

import com.kfokam48.presences.service.RelectureService;
import com.kfokam48.presences.web.dto.NoteRecueResponse;
import com.kfokam48.presences.web.dto.RelectureAFaireResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/etudiants")
public class EtudiantController {

    private final RelectureService relectureService;

    public EtudiantController(RelectureService relectureService) {
        this.relectureService = relectureService;
    }

    /** Q16 / EF11 : relectures que l'etudiant doit encore faire. */
    @GetMapping("/{etudiantId}/relectures-a-faire")
    public List<RelectureAFaireResponse> relecturesAFaire(@PathVariable Long etudiantId) {
        return relectureService.relecturesAFaire(etudiantId).stream()
                .map(RelectureAFaireResponse::depuis)
                .toList();
    }

    /** Q8 / RG5 : notes recues, sans aucune information sur le relecteur. */
    @GetMapping("/{etudiantId}/notes-recues")
    public List<NoteRecueResponse> notesRecues(@PathVariable Long etudiantId) {
        return relectureService.notesRecues(etudiantId).stream()
                .map(NoteRecueResponse::depuis)
                .toList();
    }
}
