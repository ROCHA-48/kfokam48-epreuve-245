package com.kfokam48.presences.web.erreur;

/** Format d'erreur impose : exactement deux cles, code et message. */
public record ReponseErreur(String code, String message) {
}
