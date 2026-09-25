package com.kfokam48.presences.service;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Code de presence (Q2). Alphabet sans caracteres ambigus (pas de 0/O ni de 1/I) :
 * le code est lu a voix haute en cours.
 */
@Component
public class GenerationCode {

    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private static final int LONGUEUR = 6;

    private final SecureRandom random = new SecureRandom();

    public String generer() {
        StringBuilder code = new StringBuilder(LONGUEUR);
        for (int i = 0; i < LONGUEUR; i++) {
            code.append(ALPHABET.charAt(random.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }
}
