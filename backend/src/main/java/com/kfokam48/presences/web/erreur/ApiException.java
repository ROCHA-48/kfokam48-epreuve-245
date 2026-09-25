package com.kfokam48.presences.web.erreur;

import org.springframework.http.HttpStatus;

/**
 * Toute erreur metier de l'API. Le couple (code, message) est celui impose par
 * api/contrat.yaml : meme format pour toutes les erreurs, sans exception (B4).
 */
public class ApiException extends RuntimeException {

    private final String code;
    private final HttpStatus status;

    private ApiException(String code, String message, HttpStatus status) {
        super(message);
        this.code = code;
        this.status = status;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public static ApiException codeInconnu(String message) {
        return new ApiException("CODE_INCONNU", message, HttpStatus.BAD_REQUEST);
    }

    public static ApiException codeExpire(String message) {
        return new ApiException("CODE_EXPIRE", message, HttpStatus.GONE);
    }

    public static ApiException dejaPresent() {
        return new ApiException("DEJA_PRESENT", "Votre presence est deja enregistree pour cette session.",
                HttpStatus.CONFLICT);
    }

    public static ApiException lienInvalide(String message) {
        return new ApiException("LIEN_INVALIDE", message, HttpStatus.BAD_REQUEST);
    }

    public static ApiException exerciceDejaDepose() {
        return new ApiException("EXERCICE_DEJA_DEPOSE", "Un exercice est deja depose pour cette session et cet etudiant.",
                HttpStatus.CONFLICT);
    }

    public static ApiException presenceRequise() {
        return new ApiException("PRESENCE_REQUISE", "Il faut avoir marque sa presence a cette session avant de deposer.",
                HttpStatus.CONFLICT);
    }

    public static ApiException noteInvalide(String message) {
        return new ApiException("NOTE_INVALIDE", message, HttpStatus.BAD_REQUEST);
    }

    public static ApiException autoRelecture() {
        return new ApiException("AUTO_RELECTURE", "Un etudiant ne peut pas relire son propre exercice.",
                HttpStatus.FORBIDDEN);
    }

    public static ApiException relectureDejaRendue() {
        return new ApiException("RELECTURE_DEJA_RENDUE", "Cette relecture est deja rendue et la session est cloturee.",
                HttpStatus.CONFLICT);
    }

    public static ApiException relectureCommencee() {
        return new ApiException("RELECTURE_COMMENCEE", "Le lien ne peut plus etre remplace : la relecture est commencee.",
                HttpStatus.CONFLICT);
    }

    public static ApiException relectureInconnue(Long id) {
        return new ApiException("RELECTURE_INCONNUE", "Aucune relecture ne porte l'identifiant " + id + ".",
                HttpStatus.NOT_FOUND);
    }

    public static ApiException sessionInconnue(Long id) {
        return new ApiException("SESSION_INCONNUE", "Aucune session ne porte l'identifiant " + id + ".",
                HttpStatus.NOT_FOUND);
    }

    public static ApiException sessionCloturee() {
        return new ApiException("SESSION_CLOTUREE", "La session est cloturee : plus aucune action n'y est possible.",
                HttpStatus.CONFLICT);
    }

    public static ApiException sessionDejaCloturee() {
        return new ApiException("SESSION_DEJA_CLOTUREE", "Cette session est deja cloturee.", HttpStatus.CONFLICT);
    }

    public static ApiException exerciceInconnu(Long id) {
        return new ApiException("EXERCICE_INCONNU", "Aucun exercice ne porte l'identifiant " + id + ".",
                HttpStatus.NOT_FOUND);
    }

    public static ApiException etudiantInconnu(Long id) {
        return new ApiException("ETUDIANT_INCONNU", "Aucun etudiant ne porte l'identifiant " + id + ".",
                HttpStatus.NOT_FOUND);
    }

    public static ApiException promotionInconnue(Long id) {
        return new ApiException("PROMOTION_INCONNUE", "Aucune promotion ne porte l'identifiant " + id + ".",
                HttpStatus.NOT_FOUND);
    }

    public static ApiException donneeInvalide(String message) {
        return new ApiException("DONNEE_INVALIDE", message, HttpStatus.BAD_REQUEST);
    }
}
