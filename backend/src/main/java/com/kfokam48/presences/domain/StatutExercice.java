package com.kfokam48.presences.domain;

/**
 * Cycle de vie d'un exercice (voir docs/diagrammes/D4-etats-exercice.md).
 * EN_ATTENTE_ASSIGNATION couvre le trou du sujet (Z1) : aucun relecteur candidat
 * disponible au moment du depot (RG13).
 */
public enum StatutExercice {
    EN_ATTENTE_ASSIGNATION,
    EN_ATTENTE_RELECTURE,
    RELU
}
