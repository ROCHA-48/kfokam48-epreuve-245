package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.Relecture;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.PresenceRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import com.kfokam48.presences.repository.SessionCoursRepository;
import com.kfokam48.presences.web.erreur.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class ExerciceService {

    private final ExerciceRepository exerciceRepository;
    private final SessionCoursRepository sessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final PresenceRepository presenceRepository;
    private final RelectureRepository relectureRepository;
    private final AssignationService assignationService;

    public ExerciceService(ExerciceRepository exerciceRepository, SessionCoursRepository sessionRepository,
                           EtudiantRepository etudiantRepository, PresenceRepository presenceRepository,
                           RelectureRepository relectureRepository, AssignationService assignationService) {
        this.exerciceRepository = exerciceRepository;
        this.sessionRepository = sessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.presenceRepository = presenceRepository;
        this.relectureRepository = relectureRepository;
        this.assignationService = assignationService;
    }

    /** EF3 / RG7 / Z8 : depot possible jusqu'a la cloture, mais jamais sans presence. */
    @Transactional
    public Exercice deposer(Long sessionId, Long etudiantId, String lien) {
        SessionCours session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> ApiException.sessionInconnue(sessionId));
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> ApiException.etudiantInconnu(etudiantId));

        if (session.isCloturee()) {
            throw ApiException.sessionCloturee();
        }
        if (!presenceRepository.existsBySessionIdAndEtudiantId(sessionId, etudiantId)) {
            throw ApiException.presenceRequise();
        }
        if (exerciceRepository.findBySessionIdAndEtudiantId(sessionId, etudiantId).isPresent()) {
            throw ApiException.exerciceDejaDepose();
        }

        Exercice exercice = exerciceRepository.save(new Exercice(session, etudiant, lien.trim()));
        assignationService.assignerLesExercicesEnAttente(session);
        return exerciceRepository.findById(exercice.getId()).orElse(exercice);
    }

    /** EF9 / RG8 / Q13 : remplacement du lien tant que la relecture n'est pas rendue. */
    @Transactional
    public Exercice remplacerLien(Long exerciceId, String lien) {
        Exercice exercice = exerciceRepository.findById(exerciceId)
                .orElseThrow(() -> ApiException.exerciceInconnu(exerciceId));

        if (exercice.getSession().isCloturee()) {
            throw ApiException.sessionCloturee();
        }
        // Issue #19 : la relecture est « commencee » des qu'une des deux (premiere
        // par identifiant croissant) est rendue — le lien ne se remplace plus apres.
        Relecture relecture = relectureRepository.findFirstByExerciceIdOrderByIdAsc(exerciceId).orElse(null);
        if (relecture != null && relecture.isRendue()) {
            throw ApiException.relectureCommencee();
        }

        exercice.setLien(lien.trim());
        exercice.setMajLienAt(Instant.now());
        return exerciceRepository.save(exercice);
    }

    @Transactional(readOnly = true)
    public List<Exercice> listerParSession(Long sessionId) {
        if (!sessionRepository.existsById(sessionId)) {
            throw ApiException.sessionInconnue(sessionId);
        }
        return exerciceRepository.findBySessionId(sessionId);
    }
}
