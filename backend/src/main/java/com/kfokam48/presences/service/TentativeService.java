package com.kfokam48.presences.service;

import com.kfokam48.presences.web.erreur.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Q4 / RG10 : au bout de cinq codes errones, l'etudiant est bloque deux minutes.
 * Le compteur est en memoire, par etudiant : un code inconnu ne permet pas de
 * rattacher l'echec a une session (voir Z3). Il ne survit pas au redemarrage, ce
 * qui est acceptable pour une protection anti-devinette.
 */
@Service
public class TentativeService {

    private final int tentativesMax;
    private final Duration dureeBlocage;
    private final Map<Long, Compteur> compteurs = new ConcurrentHashMap<>();

    public TentativeService(@Value("${app.presence.tentatives-max:5}") int tentativesMax,
                            @Value("${app.presence.blocage-minutes:2}") long blocageMinutes) {
        this.tentativesMax = tentativesMax;
        this.dureeBlocage = Duration.ofMinutes(blocageMinutes);
    }

    /** Refuse la tentative si l'etudiant est encore bloque. */
    public void verifierBlocage(Long etudiantId) {
        Compteur compteur = compteurs.get(etudiantId);
        if (compteur == null || compteur.bloqueJusqua == null) {
            return;
        }
        Instant maintenant = Instant.now();
        if (maintenant.isBefore(compteur.bloqueJusqua)) {
            Duration restant = Duration.between(maintenant, compteur.bloqueJusqua);
            throw ApiException.codeInconnu("Trop d'essais : reessayez dans "
                    + format(restant) + ".");
        }
        compteurs.remove(etudiantId);
    }

    /** Enregistre un code errone et bloque l'etudiant au dela du seuil. */
    public void enregistrerEchec(Long etudiantId) {
        compteurs.compute(etudiantId, (id, existant) -> {
            int echecs = (existant == null ? 0 : existant.echecs) + 1;
            if (echecs >= tentativesMax) {
                return new Compteur(echecs, Instant.now().plus(dureeBlocage));
            }
            return new Compteur(echecs, null);
        });
    }

    /** Un code valide remet le compteur a zero. */
    public void reinitialiser(Long etudiantId) {
        compteurs.remove(etudiantId);
    }

    private static String format(Duration duree) {
        long secondes = Math.max(1, duree.getSeconds());
        if (secondes < 60) {
            return secondes + " s";
        }
        long minutes = secondes / 60;
        long reste = secondes % 60;
        return minutes + " min " + (reste < 10 ? "0" : "") + reste + " s";
    }

    private record Compteur(int echecs, Instant bloqueJusqua) {
    }
}
