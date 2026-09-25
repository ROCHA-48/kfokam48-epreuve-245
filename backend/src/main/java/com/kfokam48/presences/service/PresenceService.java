package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.Presence;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.domain.SourcePresence;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.PresenceRepository;
import com.kfokam48.presences.repository.SessionCoursRepository;
import com.kfokam48.presences.web.erreur.ApiException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class PresenceService {

    private final PresenceRepository presenceRepository;
    private final SessionCoursRepository sessionRepository;
    private final EtudiantRepository etudiantRepository;
    private final TentativeService tentativeService;
    private final AssignationService assignationService;

    public PresenceService(PresenceRepository presenceRepository, SessionCoursRepository sessionRepository,
                           EtudiantRepository etudiantRepository, TentativeService tentativeService,
                           AssignationService assignationService) {
        this.presenceRepository = presenceRepository;
        this.sessionRepository = sessionRepository;
        this.etudiantRepository = etudiantRepository;
        this.tentativeService = tentativeService;
        this.assignationService = assignationService;
    }

    /**
     * EF1 / RG1 / RG12 / RG15. L'ordre des controles suit exactement le diagramme
     * de sequence D3 (docs/diagrammes/D3-sequence-presence.md).
     */
    @Transactional
    public Presence pointer(String code, Long etudiantId) {
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> ApiException.etudiantInconnu(etudiantId));

        // Q4 / RG10 : blocage anti-devinette, avant meme de regarder le code
        tentativeService.verifierBlocage(etudiantId);

        SessionCours session = sessionRepository.findByCode(code.trim().toUpperCase())
                .orElse(null);
        if (session == null) {
            tentativeService.enregistrerEchec(etudiantId);
            throw ApiException.codeInconnu("Aucune session ne correspond à ce code de présence.");
        }

        Instant maintenant = Instant.now();
        if (session.isCloturee()) {
            throw ApiException.codeExpire("La session est clôturée : le pointage est fermé.");
        }
        if (session.isCodeExpire(maintenant)) {
            throw ApiException.codeExpire("Le code de présence a expiré.");
        }
        if (presenceRepository.existsBySessionIdAndEtudiantId(session.getId(), etudiantId)) {
            throw ApiException.dejaPresent();
        }

        Presence presence = presenceRepository.save(
                new Presence(session, etudiant, SourcePresence.ETUDIANT, maintenant));
        tentativeService.reinitialiser(etudiantId);

        // RG13 / Z1 : l'arrivee d'un present peut debloquer un exercice sans relecteur
        assignationService.assignerLesExercicesEnAttente(session);
        return presence;
    }

    /** EF8 / RG6 / Q14 : presence ajoutee a la main, source = FORMATEUR. */
    @Transactional
    public Presence ajouterParFormateur(Long sessionId, Long etudiantId) {
        SessionCours session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> ApiException.sessionInconnue(sessionId));
        Etudiant etudiant = etudiantRepository.findById(etudiantId)
                .orElseThrow(() -> ApiException.etudiantInconnu(etudiantId));

        if (session.isCloturee()) {
            throw ApiException.sessionCloturee();
        }
        if (presenceRepository.existsBySessionIdAndEtudiantId(sessionId, etudiantId)) {
            throw ApiException.dejaPresent();
        }

        Presence presence = presenceRepository.save(
                new Presence(session, etudiant, SourcePresence.FORMATEUR, Instant.now()));
        assignationService.assignerLesExercicesEnAttente(session);
        return presence;
    }
}
