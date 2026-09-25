import { useState } from "react";
import { api } from "../api/api";
import type { RelectureDetail } from "../api/types";
import { ApiError } from "../api/client";
import { Chargement, Erreur, Vide } from "../components/Etat";
import { useAsync } from "../hooks/useAsync";

/**
 * Ecran relecteur (F2, EF11, EF5, EF12) : voir les relectures que je dois encore
 * faire, ouvrir l'exercice, rendre une note et un commentaire.
 */
export function RelecteurPage({ etudiantId }: { etudiantId: number | null }) {
  const [relectureId, setRelectureId] = useState<number | null>(null);
  const [note, setNote] = useState("15");
  const [commentaire, setCommentaire] = useState("");
  const [retour, setRetour] = useState<string | null>(null);
  const [enCours, setEnCours] = useState(false);

  const aFaire = useAsync(
    () => (etudiantId === null ? Promise.resolve([]) : api.relecturesAFaire(etudiantId)),
    [etudiantId]
  );
  const detail = useAsync<RelectureDetail | null>(
    () => (relectureId === null ? Promise.resolve(null) : api.detailRelecture(relectureId)),
    [relectureId]
  );

  if (etudiantId === null) {
    return <Vide libelle="Choisissez votre nom en haut de page : vous verrez les exercices à relire." />;
  }

  async function rendre() {
    if (relectureId === null) {
      return;
    }
    setEnCours(true);
    setRetour(null);
    try {
      await api.rendreRelecture(relectureId, Number(note), commentaire);
      setRetour("Relecture enregistrée. Vous pouvez encore la corriger tant que le formateur n'a pas clôturé.");
      setRelectureId(null);
      setCommentaire("");
      aFaire.recharger();
    } catch (cause) {
      const texte =
        cause instanceof ApiError
          ? cause.code === "NOTE_INVALIDE"
            ? "Note invalide : elle doit être un nombre entier entre 0 et 20 (RG3)."
            : cause.code === "AUTO_RELECTURE"
              ? "Vous ne pouvez pas relire votre propre exercice (RG2)."
              : `${cause.code} — ${cause.message}`
          : "Erreur inattendue.";
      setRetour(texte);
    } finally {
      setEnCours(false);
    }
  }

  return (
    <div>
      <section>
        <h2>Relectures que je dois encore faire (EF11)</h2>
        {aFaire.chargement && <Chargement libelle="Chargement de vos relectures…" />}
        {aFaire.erreur && <Erreur message={aFaire.erreur} onReessayer={aFaire.recharger} />}
        {aFaire.donnees && aFaire.donnees.length === 0 && (
          <Vide libelle="Aucune relecture à faire pour le moment." />
        )}
        {aFaire.donnees && aFaire.donnees.length > 0 && (
          <table>
            <thead>
              <tr>
                <th>Exercice</th>
                <th>Session</th>
                <th>Statut</th>
                <th />
              </tr>
            </thead>
            <tbody>
              {aFaire.donnees.map((relecture) => (
                <tr key={relecture.relectureId}>
                  <td>#{relecture.exerciceId}</td>
                  <td>{relecture.sessionId}</td>
                  <td>{relecture.statut}</td>
                  <td>
                    <button type="button" onClick={() => setRelectureId(relecture.relectureId)}>
                      Relire
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>

      {relectureId !== null && (
        <section>
          <h2>Rendre ma relecture (EF5)</h2>
          {detail.chargement && <Chargement libelle="Chargement du travail à relire…" />}
          {detail.erreur && <Erreur message={detail.erreur} onReessayer={detail.recharger} />}
          {detail.donnees && (
            <>
              <p className="etat vide">
                Travail à relire :{" "}
                <a href={detail.donnees.lien} target="_blank" rel="noreferrer">
                  {detail.donnees.lien}
                </a>
              </p>
              <div className="ligne-formulaire">
                <label className="champ">
                  Note sur 20 (nombre entier)
                  <input
                    type="number"
                    min={0}
                    max={20}
                    step={1}
                    value={note}
                    onChange={(evenement) => setNote(evenement.target.value)}
                  />
                </label>
                <label className="champ">
                  Commentaire
                  <textarea
                    rows={3}
                    value={commentaire}
                    onChange={(evenement) => setCommentaire(evenement.target.value)}
                  />
                </label>
                <button type="button" disabled={enCours} onClick={rendre}>
                  Envoyer ma relecture
                </button>
              </div>
            </>
          )}
        </section>
      )}

      {retour && <p className="etat chargement">{retour}</p>}
    </div>
  );
}
