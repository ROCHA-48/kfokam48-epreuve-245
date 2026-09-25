package com.kfokam48.presences.web;

import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.web.dto.EtudiantResponse;
import com.kfokam48.presences.web.dto.PromotionResponse;
import com.kfokam48.presences.web.erreur.ApiException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/** Q1 : l'etudiant choisit son nom dans une liste, il n'y a pas d'authentification. */
@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    private final PromotionRepository promotionRepository;
    private final EtudiantRepository etudiantRepository;

    public PromotionController(PromotionRepository promotionRepository, EtudiantRepository etudiantRepository) {
        this.promotionRepository = promotionRepository;
        this.etudiantRepository = etudiantRepository;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public List<PromotionResponse> lister() {
        return promotionRepository.findAll().stream().map(PromotionResponse::depuis).toList();
    }

    @GetMapping("/{promotionId}/etudiants")
    @Transactional(readOnly = true)
    public List<EtudiantResponse> etudiants(@PathVariable Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw ApiException.promotionInconnue(promotionId);
        }
        return etudiantRepository.findByPromotionIdOrderByNomAsc(promotionId).stream()
                .map(EtudiantResponse::depuis)
                .toList();
    }
}
