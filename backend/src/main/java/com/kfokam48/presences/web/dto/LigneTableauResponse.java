package com.kfokam48.presences.web.dto;

/**
 * Une ligne du tableau du formateur (Q16). relecturesEnAttente = les relectures
 * que cet etudiant doit encore faire (Z5). moyenne vaut null si aucune note.
 */
public record LigneTableauResponse(Long etudiantId, String nom, long presences, long exercicesDeposes,
                                   Double moyenne, long relecturesEnAttente) {
}
