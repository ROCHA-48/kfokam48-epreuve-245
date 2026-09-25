package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * Regle metier isolee et testable (Q5, Q6, Q7, RG4, RG13) : le relecteur d'un
 * exercice est tire au hasard parmi les etudiants presents a la session, jamais
 * l'auteur de l'exercice. Aucune dependance a Spring : c'est volontaire, cette
 * classe est couverte par un test unitaire pur (B6).
 */
public class AttributionRelecture {

    private final Random random;

    public AttributionRelecture(Random random) {
        this.random = random;
    }

    /**
     * @param presents etudiants presents a la session (peut contenir l'auteur)
     * @param auteur   auteur de l'exercice a faire relire
     * @return un candidat different de l'auteur, ou vide si aucun candidat
     *         n'est disponible : l'exercice reste alors en attente d'assignation (Z1).
     */
    public Optional<Etudiant> choisir(List<Etudiant> presents, Etudiant auteur) {
        List<Etudiant> candidats = new ArrayList<>(presents.stream()
                .filter(etudiant -> !etudiant.getId().equals(auteur.getId()))
                .distinct()
                .toList());
        if (candidats.isEmpty()) {
            return Optional.empty();
        }
        Collections.shuffle(candidats, random);
        return Optional.of(candidats.get(0));
    }
}
