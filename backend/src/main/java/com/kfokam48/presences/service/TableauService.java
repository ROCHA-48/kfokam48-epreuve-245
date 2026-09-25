package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.PresenceRepository;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import com.kfokam48.presences.web.dto.LigneTableauResponse;
import com.kfokam48.presences.web.erreur.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * EF7 / Q16 : tableau du formateur. La moyenne vient d'ici, jamais du frontend (F3),
 * et relecturesEnAttente compte les relectures que l'etudiant doit encore faire (Z5).
 */
@Service
public class TableauService {

    private final PromotionRepository promotionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;
    private final ExerciceRepository exerciceRepository;
    private final RelectureRepository relectureRepository;

    public TableauService(PromotionRepository promotionRepository, EtudiantRepository etudiantRepository,
                          PresenceRepository presenceRepository, ExerciceRepository exerciceRepository,
                          RelectureRepository relectureRepository) {
        this.promotionRepository = promotionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
        this.exerciceRepository = exerciceRepository;
        this.relectureRepository = relectureRepository;
    }

    @Transactional(readOnly = true)
    public List<LigneTableauResponse> lignes(Long promotionId) {
        if (!promotionRepository.existsById(promotionId)) {
            throw ApiException.promotionInconnue(promotionId);
        }
        List<LigneTableauResponse> lignes = new ArrayList<>();
        for (Etudiant etudiant : etudiantRepository.findByPromotionIdOrderByNomAsc(promotionId)) {
            lignes.add(new LigneTableauResponse(
                    etudiant.getId(),
                    etudiant.getNom(),
                    presenceRepository.compterPresences(etudiant.getId(), promotionId),
                    exerciceRepository.compterExercices(etudiant.getId(), promotionId),
                    relectureRepository.moyenneRecue(etudiant.getId(), promotionId),
                    relectureRepository.compterRelecturesAFaire(etudiant.getId())));
        }
        return lignes;
    }
}
