import { api } from "../api/api";
import { useAsync } from "../hooks/useAsync";
import { Chargement, Erreur } from "./Etat";

/**
 * Q1 : choix du nom dans la liste des etudiants d'une promotion. Aucun mot de passe.
 */
export function ChoixEtudiant({
  promotionId,
  etudiantId,
  onChange,
}: {
  promotionId: number | null;
  etudiantId: number | null;
  onChange: (id: number | null) => void;
}) {
  const etudiants = useAsync(
    () => (promotionId === null ? Promise.resolve([]) : api.listerEtudiants(promotionId)),
    [promotionId]
  );

  if (promotionId === null) {
    return <p className="etat vide">Choisissez d'abord une promotion.</p>;
  }
  if (etudiants.chargement) {
    return <Chargement libelle="Chargement des étudiants…" />;
  }
  if (etudiants.erreur) {
    return <Erreur message={etudiants.erreur} onReessayer={etudiants.recharger} />;
  }
  if (!etudiants.donnees || etudiants.donnees.length === 0) {
    return <p className="etat vide">Aucun étudiant dans cette promotion.</p>;
  }

  return (
    <label className="champ">
      Je suis
      <select
        value={etudiantId ?? ""}
        onChange={(evenement) => onChange(evenement.target.value ? Number(evenement.target.value) : null)}
      >
        <option value="">— choisir mon nom —</option>
        {etudiants.donnees.map((etudiant) => (
          <option key={etudiant.id} value={etudiant.id}>
            {etudiant.nom}
          </option>
        ))}
      </select>
    </label>
  );
}
