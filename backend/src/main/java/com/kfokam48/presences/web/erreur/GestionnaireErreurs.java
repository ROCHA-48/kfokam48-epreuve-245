package com.kfokam48.presences.web.erreur;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.validation.FieldError;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * Gestion centralisee des erreurs (B4) : aucune stack trace ne part vers le
 * client, tous les corps de reponse ont la forme { code, message }.
 */
@RestControllerAdvice
public class GestionnaireErreurs {

    private static final Logger log = LoggerFactory.getLogger(GestionnaireErreurs.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ReponseErreur> gererErreurMetier(ApiException ex) {
        return ResponseEntity.status(ex.getStatus()).body(new ReponseErreur(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ReponseErreur> gererValidation(MethodArgumentNotValidException ex) {
        // Le contrat impose un code precis par champ fautif (B2) :
        // note hors bornes -> NOTE_INVALIDE, lien mal forme -> LIEN_INVALIDE.
        String champ = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(FieldError::getField)
                .orElse("");
        String code = switch (champ) {
            case "note" -> "NOTE_INVALIDE";
            case "lien" -> "LIEN_INVALIDE";
            default -> "DONNEE_INVALIDE";
        };
        String message = ex.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(erreur -> erreur.getField() + " : " + erreur.getDefaultMessage())
                .orElse("Le corps de la requete est invalide.");
        return ResponseEntity.badRequest().body(new ReponseErreur(code, message));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class})
    public ResponseEntity<ReponseErreur> gererRequeteIllisible(Exception ex) {
        return ResponseEntity.badRequest()
                .body(new ReponseErreur("DONNEE_INVALIDE", "Le corps de la requete est invalide ou incomplet."));
    }

    /**
     * Issue #18 : une course entre deux requetes simultanees peut faire echouer
     * une insertion sur une contrainte UNIQUE (presence deja pointee, relecture
     * deja assignee) malgre la verification prealable. La base est l'arbitre
     * final : la requete perdante sort en 409 au format du contrat, jamais en 500,
     * et peut etre rejouee.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ReponseErreur> gererConflitDIntegrite(DataIntegrityViolationException ex) {
        log.warn("Conflit d'integrite : {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ReponseErreur("DEJA_PRESENT",
                        "Cette information est deja enregistree — un traitement simultane vient de la prendre."));
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ReponseErreur> gererRessourceAbsente(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ReponseErreur("RESSOURCE_INCONNUE", "Cette ressource n'existe pas."));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ReponseErreur> gererErreurImprevue(Exception ex) {
        log.error("Erreur imprevue", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ReponseErreur("ERREUR_INTERNE", "Une erreur interne est survenue."));
    }
}
