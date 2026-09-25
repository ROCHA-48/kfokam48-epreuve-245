package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.Relecture;
import com.kfokam48.presences.domain.StatutExercice;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import com.kfokam48.presences.web.erreur.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class RelectureService {

    private final RelectureRepository relectureRepository;
    private final ExerciceRepository exerciceRepository;
    private final EtudiantRepository etudiantRepository;

    public RelectureService(RelectureRepository relectureRepository, ExerciceRepository exerciceRepository,
                            EtudiantRepository etudiantRepository) {
        this.relectureRepository = relectureRepository;
        this.exerciceRepository = exerciceRepository;
        this.etudiantRepository = etudiantRepository;
    }

    /**
     * EF5 / RG2 / RG3 / RG11 / EF12. La note reste corrigeable tant que la
     * session n'est pas cloturee : arbitrage C1 (Q10 retenu contre Q15).
     */
    @Transactional
    public Relecture rendre(Long relectureId, java.math.BigDecimal note, String commentaire) {
        // La note est validee avant tout : une requete mal formee est refusee
        // avec le code du contrat, que la relecture existe ou non (B2).
        int noteValidee = validerNote(note);
        Relecture relecture = relectureRepository.findById(relectureId)
                .orElseThrow(() -> ApiException.relectureInconnue(relectureId));

        Exercice exercice = relecture.getExercice();
        if (relecture.getRelecteur().getId().equals(exercice.getEtudiant().getId())) {
            throw ApiException.autoRelecture(); // RG2, defense en profondeur
        }
        if (exercice.getSession().isCloturee()) {
            throw ApiException.relectureDejaRendue(); // RG14, Z2 : la cloture fige tout
        }

        relecture.setNote(noteValidee);
        relecture.setCommentaire(commentaire);
        relecture.setRendueAt(Instant.now());
        exercice.setStatut(StatutExercice.RELU);
        exerciceRepository.save(exercice);
        return relectureRepository.save(relecture);
    }

    /** RG3 : entier de 0 a 20, sinon NOTE_INVALIDE (contrat). */
    private int validerNote(java.math.BigDecimal note) {
        if (note == null || note.stripTrailingZeros().scale() > 0
                || note.compareTo(java.math.BigDecimal.ZERO) < 0
                || note.compareTo(java.math.BigDecimal.valueOf(20)) > 0) {
            throw ApiException.noteInvalide("La note doit etre un nombre entier compris entre 0 et 20.");
        }
        return note.intValue();
    }

    @Transactional(readOnly = true)
    public Relecture detail(Long relectureId) {
        return relectureRepository.findById(relectureId)
                .orElseThrow(() -> ApiException.relectureInconnue(relectureId));
    }

    /** Q16 / EF11 / Z5 : les relectures que cet etudiant doit encore faire. */
    @Transactional(readOnly = true)
    public List<Relecture> relecturesAFaire(Long etudiantId) {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw ApiException.etudiantInconnu(etudiantId);
        }
        return relectureRepository.relecturesAFaire(etudiantId);
    }

    /** Q8 / RG5 : la note et le commentaire recus, sans le nom du relecteur. */
    @Transactional(readOnly = true)
    public List<Relecture> notesRecues(Long etudiantId) {
        if (!etudiantRepository.existsById(etudiantId)) {
            throw ApiException.etudiantInconnu(etudiantId);
        }
        return relectureRepository.notesRecues(etudiantId);
    }
}
