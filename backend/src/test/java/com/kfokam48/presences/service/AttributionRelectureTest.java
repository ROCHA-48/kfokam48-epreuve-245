package com.kfokam48.presences.service;

import com.kfokam48.presences.domain.Etudiant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test unitaire sur une regle metier reelle (B6) : le relecteur d'un exercice est
 * tire au hasard parmi les presents, mais jamais l'auteur (Q5, Q7, RG4).
 * Aucune base de donnees, aucun contexte Spring : il tourne sur un poste vierge.
 */
class AttributionRelectureTest {

    private final AttributionRelecture attribution = new AttributionRelecture(new Random(48));

    private static Etudiant etudiant(Long id, String nom) {
        Etudiant etudiant = new Etudiant(nom, null, null);
        etudiant.setId(id);
        return etudiant;
    }

    @Test
    @DisplayName("Le relecteur n'est jamais l'auteur de l'exercice (Q5, RG2)")
    void neChoisitJamaisLAuteur() {
        Etudiant auteur = etudiant(1L, "Awa Nkolo");
        List<Etudiant> presents = List.of(auteur, etudiant(2L, "Brice Fotso"), etudiant(3L, "Cedric Mbarga"));

        for (int tirage = 0; tirage < 200; tirage++) {
            Optional<Etudiant> relecteur = attribution.choisir(presents, auteur);

            assertThat(relecteur).isPresent();
            assertThat(relecteur.get().getId()).isNotEqualTo(auteur.getId());
        }
    }

    @Test
    @DisplayName("Sans autre present, l'exercice reste sans relecteur (Z1, RG13)")
    void aucunCandidatQuandLAuteurEstSeulPresent() {
        Etudiant auteur = etudiant(1L, "Awa Nkolo");

        Optional<Etudiant> relecteur = attribution.choisir(List.of(auteur), auteur);

        assertThat(relecteur).isEmpty();
    }

    @Test
    @DisplayName("Le relecteur est choisi parmi les presents a la session (Q7)")
    void choisitUniquementParmiLesPresents() {
        Etudiant auteur = etudiant(1L, "Awa Nkolo");
        Etudiant present = etudiant(2L, "Brice Fotso");
        Etudiant present2 = etudiant(3L, "Diane Kamga");
        List<Etudiant> presents = List.of(auteur, present, present2);

        for (int tirage = 0; tirage < 100; tirage++) {
            Optional<Etudiant> relecteur = attribution.choisir(presents, auteur);

            assertThat(relecteur).isPresent();
            assertThat(List.of(present.getId(), present2.getId())).contains(relecteur.get().getId());
        }
    }

    @Test
    @DisplayName("Deux etudiants presents ne produisent pas deux relectures du meme exercice (Q6)")
    void unSeulRelecteurParExercice() {
        Etudiant auteur = etudiant(1L, "Awa Nkolo");
        List<Etudiant> presents = List.of(auteur, etudiant(2L, "Brice Fotso"));

        Optional<Etudiant> premier = attribution.choisir(presents, auteur);
        Optional<Etudiant> second = attribution.choisir(presents, auteur);

        assertThat(premier).isPresent();
        assertThat(second).isPresent();
        assertThat(premier.get().getId()).isEqualTo(second.get().getId());
    }
}
