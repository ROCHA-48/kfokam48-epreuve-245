import { useState } from "react";
import { api } from "../api/api";
import type { LigneTableau, Session } from "../api/types";
import { ApiError } from "../api/client";
import { Chargement, Erreur, Vide } from "../components/Etat";
import { useAsync } from "../hooks/useAsync";

/**
 * Ecran formateur (F2) : ouvrir une session et obtenir le code, voir le tableau
 * recapitulatif, cloturer la session, ajouter une presence a la main (Q14).
 */
export function FormateurPage({
  promotionId,
  etudiantId,
}: {
  promotionId: number | null;
  etudiantId: number | null;
}) {
  const [titre, setTitre] = useState("Cours de Java");
  const [message, setMessage] = useState<string | null>(null);
  const [action, setAction] = useState<"repos" | "en_cours">("repos");
  const [sessionPresenceId, setSessionPresenceId] = useState<string>("");

  const sessions = useAsync(
    () => (promotionId === null ? Promise.resolve([] as Session[]) : api.listerSessions(promotionId)),
    [promotionId]
  );
  const tableau = useAsync(
    () => (promotionId === null ? Promise.resolve([] as LigneTableau[]) : api.tableau(promotionId)),
    [promotionId]
  );
  const etudiants = useAsync(
    () => (promotionId === null ? Promise.resolve([]) : api.listerEtudiants(promotionId)),
    [promotionId]
  );

  if (promotionId === null) {
    return <Vide libelle="Choisissez une promotion en haut de page." />;
  }

  async function executer(actionAsync: () => Promise<unknown>, succes: string) {
    setAction("en_cours");
    setMessage(null);
    try {
      await actionAsync();
      setMessage(succes);
      sessions.recharger();
      tableau.recharger();
    } catch (cause) {
      setMessage(cause instanceof ApiError ? `${cause.code} — ${cause.message}` : "Erreur inattendue.");
    } finally {
      setAction("repos");
    }
  }

  return (
    <div>
      <section>
        <h2>Ouvrir une session (EF2)</h2>
        <div className="ligne-formulaire">
          <label className="champ">
            Titre de la séance
            <input value={titre} onChange={(evenement) => setTitre(evenement.target.value)} />
          </label>
          <button
            type="button"
            disabled={action === "en_cours" || titre.trim() === ""}
            onClick={() =>
              executer(() => api.ouvrirSession(titre, promotionId), "Session ouverte : annoncez le code à l'oral.")
            }
          >
            Ouvrir la session
          </button>
        </div>
        {message && <p className="etat chargement">{message}</p>}

        {sessions.chargement && <Chargement libelle="Chargement des sessions…" />}
        {sessions.erreur && <Erreur message={sessions.erreur} onReessayer={sessions.recharger} />}
        {sessions.donnees && sessions.donnees.length === 0 && <Vide libelle="Aucune session pour cette promotion." />}
        {sessions.donnees && sessions.donnees.length > 0 && (
          <table>
            <thead>
              <tr>
                <th>Titre</th>
                <th>Code</th>
                <th>Expire à</th>
                <th>État</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {sessions.donnees.map((session) => (
                <tr key={session.id}>
                  <td>{session.titre}</td>
                  <td className="code">{session.code}</td>
                  <td>{new Date(session.expirationAt).toLocaleTimeString("fr-FR")}</td>
                  <td>{session.cloturee ? "clôturée" : "ouverte"}</td>
                  <td>
                    <button
                      type="button"
                      disabled={session.cloturee || action === "en_cours"}
                      onClick={() =>
                        executer(
                          () => api.cloturerSession(session.id),
                          "Session clôturée : présences, dépôts et notes sont figés (RG14)."
                        )
                      }
                    >
                      Clôturer
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      <section>
        <h2>Tableau récapitulatif (EF7)</h2>
        {tableau.chargement && <Chargement libelle="Chargement du tableau…" />}
        {tableau.erreur && <Erreur message={tableau.erreur} onReessayer={tableau.recharger} />}
        {tableau.donnees && (
          <table>
            <thead>
              <tr>
                <th>Étudiant</th>
                <th>Présences</th>
                <th>Exercices déposés</th>
                <th>Moyenne</th>
                <th>Relectures à faire</th>
              </tr>
            </thead>
            <tbody>
              {tableau.donnees.map((ligne) => (
                <tr key={ligne.etudiantId}>
                  <td>{ligne.nom}</td>
                  <td>{ligne.presences}</td>
                  <td>{ligne.exercicesDeposes}</td>
                  {/* La moyenne vient de l'API, elle n'est jamais recalculee ici (F3) */}
                  <td>{ligne.moyenne === null ? "—" : ligne.moyenne.toFixed(2)}</td>
                  <td>{ligne.relecturesEnAttente}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      <section>
        <h2>Ajouter une présence à la main (EF8, Q14)</h2>
        <p className="etat vide">
          La présence sera marquée « ajouté par le formateur » pour la distinguer d'un pointage par l'étudiant.
        </p>
        <div className="ligne-formulaire">
          <label className="champ">
            Session
            <select value={sessionPresenceId} onChange={(evenement) => setSessionPresenceId(evenement.target.value)}>
              <option value="">— choisir —</option>
              {(sessions.donnees ?? [])
                .filter((session) => !session.cloturee)
                .map((session) => (
                  <option key={session.id} value={session.id}>
                    {session.titre} ({session.code})
                  </option>
                ))}
            </select>
          </label>
          <p className="champ">
            Étudiant concerné
            <strong>
              {etudiantId === null
                ? "choisissez d'abord un étudiant dans la liste en haut de page"
                : (etudiants.donnees ?? []).find((etudiant) => etudiant.id === etudiantId)?.nom ?? `#${etudiantId}`}
            </strong>
          </p>
          <button
            type="button"
            disabled={action === "en_cours" || sessionPresenceId === "" || etudiantId === null}
            onClick={() =>
              executer(
                () => api.ajouterPresenceFormateur(Number(sessionPresenceId), etudiantId as number),
                "Présence ajoutée, marquée comme ajoutée par le formateur."
              )
            }
          >
            Ajouter la présence
          </button>
        </div>
        {(etudiants.erreur || etudiants.chargement) && <Chargement libelle="Chargement des étudiants…" />}
      </section>
    </div>
  );
}
