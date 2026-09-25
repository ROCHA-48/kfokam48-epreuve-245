/** Affichage uniforme des etats de chargement, d'erreur et de vide (F3). */

export function Chargement({ libelle = "Chargement…" }: { libelle?: string }) {
  return <p className="etat chargement">{libelle}</p>;
}

export function Erreur({ message, onReessayer }: { message: string; onReessayer?: () => void }) {
  return (
    <p className="etat erreur">
      {message}
      {onReessayer && (
        <button type="button" onClick={onReessayer}>
          Réessayer
        </button>
      )}
    </p>
  );
}

export function Vide({ libelle }: { libelle: string }) {
  return <p className="etat vide">{libelle}</p>;
}
