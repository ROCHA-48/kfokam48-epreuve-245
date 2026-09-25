import { useEffect, useState } from "react";

const CLE = "kfokam48.etudiantId";

/** Q1 : l'etudiant choisit son nom une fois, il est retenu pour la navigation. */
export function useEtudiantSelectionne(): [number | null, (id: number | null) => void] {
  const [etudiantId, setEtudiantId] = useState<number | null>(() => {
    const valeur = window.localStorage.getItem(CLE);
    return valeur ? Number(valeur) : null;
  });

  useEffect(() => {
    if (etudiantId === null) {
      window.localStorage.removeItem(CLE);
    } else {
      window.localStorage.setItem(CLE, String(etudiantId));
    }
  }, [etudiantId]);

  return [etudiantId, setEtudiantId];
}
