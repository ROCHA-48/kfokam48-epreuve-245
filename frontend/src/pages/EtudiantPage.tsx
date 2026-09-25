import { useState } from "react";
import { api } from "../api/api";
import type { Exercice, NoteRecue, Session } from "../api/types";
import { ApiError } from "../api/client";
import { Chargement, Erreur, Vide } from "../components/Etat";
import { useAsync } from "../hooks/useAsync";

/**
 * Ecran etudiant (F2) : marquer sa presence avec le code (EF1), deposer ou
 * remplacer le lien de son exercice (EF3, EF9), consulter sa note et son
 * commentaire sans connaitre le relecteur (EF6).
 */
export function EtudiantPage({
  promotionId,
  etudiantId,
}: {
  promotionId: number | null;
  etudiantId: number | null;
}) {
  const [code, setCode] = useState("");
  const [lien, setLien] = useState("");
  const [sessionId, setSessionId] = useState<string>("");
  const [retour, setRetour] = useState<string | null>(null);
  const [enCours, setEnCours] = useState(false);

  const sessions = useAsync(
    () => (promotionId === null ? Promise.resolve([] as Session[]) : api.listerSessions(promotionId)),
    [promotionId]
  );
  const notes = useAsync(
    () => (etudiantId === null ? Promise.resolve([] as NoteRecue[]) : api.notesRecues(etudiantId)),
    [etudiantId]
  );
  const exercices = useAsync(
    () => (sessionId === "" ? Promise.resolve([] as Exercice[]) : api.listerExercices(Number(sessionId))),
    [sessionId]
  );

  if (etudiantId === null) {
    return <Vide libelle="Choisissez votre nom en haut de page (aucun mot de passe, Q1)." />;
  }

  const monExercice = (exercices.donnees ?? []).find((exercice) => exercice.etudiantId === etudiantId) ?? null;

  async function executer(actionAsync: () => Promise<unknown>, succes: string) {
    setEnCours(true);
    setRetour(null);
    try {
      await actionAsync();
      setRetour(succes);
      notes.recharger();
      exercices.recharger();
    } catch (cause) {
      const texte =
        cause instanceof ApiError
          ? cause.code === "CODE_EXPIRE"
            ? "Code expiré : redemandez le code au formateur (RG1)."
            : cause.code === "DEJA_PRESENT"
              ? "Votre présence est déjà enregistrée pour cette session (RG15)."
              : cause.code === "CODE_INCONNU"
                ? "Code inconnu : vérifiez le code annoncé par le formateur."
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
        <h2>Marquer ma présence (EF1)</h2>
        <div className="ligne-formulaire">
          <label className="champ">
            Code annoncé par le formateur
            <input value={code} onChange={(evenement) => setCode(evenement.target.value.toUpperCase())} />
          </label>
          <button
            type="button"
            disabled={enCours || code.trim() === ""}
            onClick={() => executer(() => api.marquerPresence(code, etudiantId), "Présence enregistrée, votre nom apparaît dans le tableau.")}
          >
            Je suis présent
          </button>
        </div>
      </section>

      <section>
        <h2>Déposer ou remplacer mon exercice (EF3, EF9)</h2>
        <div className="ligne-formulaire">
          <label className="champ">
            Session
            <select value={sessionId} onChange={(evenement) => setSessionId(evenement.target.value)}>
              <option value="">— choisir —</option>
              {(sessions.donnees ?? []).map((session) => (
                <option key={session.id} value={session.id}>
                  {session.titre} {session.cloturee ? "(clôturée)" : ""}
                </option>
              ))}
            </select>
          </label>
          <label className="champ">
            Lien de mon travail (URL)
            <input value={lien} onChange={(evenement) => setLien(evenement.target.value)} placeholder="https://…" />
          </label>
          <button
            type="button"
            disabled={enCours || sessionId === "" || lien.trim() === ""}
            onClick={() =>
              monExercice
                ? executer(() => api.remplacerLien(monExercice.id, lien), "Lien remplacé.")
                : executer(
                    () => api.deposerExercice(Number(sessionId), etudiantId, lien),
                    "Exercice déposé : il est envoyé à un relecteur désigné au hasard."
                  )
            }
          >
            {monExercice ? "Remplacer mon lien" : "Déposer mon exercice"}
          </button>
        </div>
        {exercices.chargement && <Chargement libelle="Chargement de mes dépôts…" />}
        {exercices.erreur && <Erreur message={exercices.erreur} onReessayer={exercices.recharger} />}
        {sessionId !== "" && monExercice && (
          <p className="etat vide">
            Mon dépôt : <strong>{monExercice.statut}</strong> — {monExercice.lien}
          </p>
        )}
        {retour && <p className="etat chargement">{retour}</p>}
      </section>

      <section>
        <h2>Mes notes (EF6)</h2>
        {notes.chargement && <Chargement libelle="Chargement de mes notes…" />}
        {notes.erreur && <Erreur message={notes.erreur} onReessayer={notes.recharger} />}
        {notes.donnees && notes.donnees.length === 0 && <Vide libelle="Aucune note reçue pour le moment." />}
        {notes.donnees && notes.donnees.length > 0 && (
          <table>
            <thead>
              <tr>
                <th>Exercice</th>
                <th>Note</th>
                <th>Commentaire</th>
              </tr>
            </thead>
            <tbody>
              {notes.donnees.map((note) => (
                <tr key={note.relectureId}>
                  <td>#{note.exerciceId}</td>
                  <td>{note.note}/20</td>
                  {/* RG5 : aucune information sur le relecteur n'est affichee */}
                  <td>{note.commentaire}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </div>
  );
}
