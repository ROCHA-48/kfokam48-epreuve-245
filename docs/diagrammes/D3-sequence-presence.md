# D3 — Séquence : « marquer sa présence »

Cas nominal et cas d'erreur. Les codes HTTP et les corps de réponse sont ceux du contrat `api/contrat.yaml`.

```mermaid
sequenceDiagram
    autonumber
    actor E as Étudiant
    participant F as Frontend (React)
    participant C as PresenceController
    participant S as PresenceService
    participant R as SessionRepository
    participant DB as PostgreSQL

    E->>F: saisit le code de présence
    F->>C: POST /api/presences { code, etudiantId }
    C->>C: valide le DTO (code non vide, etudiantId non nul)

    alt corps invalide
        C-->>F: 400 { code: "VALIDATION_ECHEC", message: "Le code est obligatoire." }
    else corps valide
        C->>S: enregistrerPresence(code, etudiantId)
        S->>S: lit le compteur d'échecs du couple (session, étudiant) : Q4, RG10

        alt étudiant bloqué (5 erreurs, moins de 2 minutes écoulées)
            S-->>C: BlocageException(secondesRestantes)
            C-->>F: 400 { code: "CODE_INCONNU", message: "Trop d'essais, réessayez dans 1 min 20 s." }
        else étudiant non bloqué
            S->>R: findByCode(code)

            alt code inconnu
                S->>S: incrémente le compteur d'échecs
                S-->>C: CodeInconnuException
                C-->>F: 400 { code: "CODE_INCONNU", message: "Aucune session ne correspond à ce code." }
            else code connu
                R-->>S: session
                S->>S: session clôturée ? (RG14)

                alt session déjà clôturée
                    S-->>C: CodeExpireException
                    C-->>F: 410 { code: "CODE_EXPIRE", message: "La session est clôturée." }
                else session ouverte
                    S->>S: maintenant > expirationAt ? (RG1, RG12)

                    alt code expiré
                        S-->>C: CodeExpireException
                        C-->>F: 410 { code: "CODE_EXPIRE", message: "Le code de présence a expiré." }
                    else code encore valide
                        S->>DB: SELECT ... FROM presence WHERE session_id = ? AND etudiant_id = ?
                        DB-->>S: résultat
                        alt présence déjà enregistrée (RG15)
                            S-->>C: DejaPresentException
                            C-->>F: 409 { code: "DEJA_PRESENT", message: "Votre présence est déjà enregistrée pour cette session." }
                        else aucune présence
                            S->>DB: INSERT INTO presence (session_id, etudiant_id, source = 'ETUDIANT')
                            DB-->>S: présence créée
                            S->>DB: retente l'assignation des exercices EN_ATTENTE_ASSIGNATION sur cette session (RG13, Z1)
                            S->>S: remet le compteur d'échecs à zéro
                            S-->>C: Presence
                            C-->>F: 201 { id, sessionId, etudiantId, source: "ETUDIANT" }
                        end
                    end
                end
            end
        end
    end

    F-->>E: affiche la confirmation ou le message d'erreur
```

## Correspondance avec le contrat

| Branche | Code HTTP | Corps d'erreur | Règle |
|---|---|---|---|
| Corps de requête invalide | `400` | `{ code: "VALIDATION_ECHEC", message }` | B4 |
| Étudiant bloqué | `400` | `{ code: "CODE_INCONNU", message }` | Q4, RG10, Z3 |
| Code inconnu | `400` | `{ code: "CODE_INCONNU", message }` | contrat |
| Session clôturée | `410` | `{ code: "CODE_EXPIRE", message }` | RG14 |
| Code expiré | `410` | `{ code: "CODE_EXPIRE", message }` | RG1, RG12 |
| Présence déjà enregistrée | `409` | `{ code: "DEJA_PRESENT", message }` | RG15 |
| Cas nominal | `201` | `{ id, sessionId, etudiantId, source }` | EF1 |

Deux choix explicites :

- Pendant un blocage, la réponse reste `CODE_INCONNU`, même si le code saisi était valide. C'est le but de Q4 : ne pas laisser deviner les codes. Le contrat ne prévoyant pas d'autre code d'erreur pour cette opération, je n'en invente pas et je mets l'information utile dans le `message`.
- L'insertion de la présence déclenche une nouvelle tentative d'assignation des exercices restés en attente d'assignation (Z1, RG13), ce qui évite qu'un exercice reste orphelin après l'arrivée d'un nouvel étudiant présent.
