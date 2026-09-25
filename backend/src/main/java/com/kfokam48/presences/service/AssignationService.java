package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.Presence;
import com.kfokam48.presences.domain.Relecture;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.domain.StatutExercice;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.PresenceRepository;
import com.kfokam48.presences.repository.RelectureRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * EF4 / RG4 / RG13 : assignation automatique du relecteur. Appelee apres chaque
 * depot et apres chaque nouvelle presence, pour qu'un exercice en attente
 * d'assignation (Z1) trouve un relecteur des qu'un etudiant devient disponible.
 */
@Service
public class AssignationService {

    private final ExerciceRepository exerciceRepository;
    private final PresenceRepository presenceRepository;
    private final RelectureRepository relectureRepository;
    private final AttributionRelecture attribution = new AttributionRelecture(new Random());

    public AssignationService(ExerciceRepository exerciceRepository, PresenceRepository presenceRepository,
                              RelectureRepository relectureRepository) {
        this.exerciceRepository = exerciceRepository;
        this.presenceRepository = presenceRepository;
        this.relectureRepository = relectureRepository;
    }

    /** Assigne un relecteur aux exercices de la session qui attendent encore. */
    @Transactional
    public void assignerLesExercicesEnAttente(SessionCours session) {
        List<Exercice> enAttente = exerciceRepository
                .findByStatutAndSessionId(StatutExercice.EN_ATTENTE_ASSIGNATION, session.getId());
        if (enAttente.isEmpty()) {
            return;
        }
        List<Etudiant> presents = new ArrayList<>(presenceRepository.findBySessionId(session.getId()).stream()
                .map(Presence::getEtudiant)
                .toList());

        for (Exercice exercice : enAttente) {
            Optional<Etudiant> relecteur = attribution.choisir(presents, exercice.getEtudiant());
            if (relecteur.isEmpty()) {
                continue; // Z1 : reste EN_ATTENTE_ASSIGNATION, jamais perdu
            }
            Etudiant choisi = relecteur.get();
            relectureRepository.save(new Relecture(exercice, choisi));
            exercice.setStatut(StatutExercice.EN_ATTENTE_RELECTURE);
            exerciceRepository.save(exercice);
            presents.remove(choisi); // repartition plus juste quand plusieurs exercices attendent
        }
    }
}
