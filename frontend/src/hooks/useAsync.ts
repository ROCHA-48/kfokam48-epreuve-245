import { useCallback, useEffect, useState } from "react";
import { ApiError } from "../api/client";

export type EtatChargement<T> = {
  donnees: T | null;
  chargement: boolean;
  erreur: string | null;
  recharger: () => void;
};

/**
 * Gere les trois etats imposes par F3 : chargement, erreur, donnees.
 * Les messages d'erreur sont ceux renvoyes par l'API ({ code, message }).
 */
export function useAsync<T>(action: () => Promise<T>, dependances: unknown[]): EtatChargement<T> {
  const [donnees, setDonnees] = useState<T | null>(null);
  const [chargement, setChargement] = useState(true);
  const [erreur, setErreur] = useState<string | null>(null);
  const [compteur, setCompteur] = useState(0);

  const recharger = useCallback(() => setCompteur((valeur) => valeur + 1), []);

  useEffect(() => {
    let annule = false;
    setChargement(true);
    setErreur(null);

    action()
      .then((resultat) => {
        if (!annule) {
          setDonnees(resultat);
        }
      })
      .catch((cause) => {
        if (!annule) {
          setErreur(cause instanceof ApiError ? cause.message : "Erreur inattendue.");
        }
      })
      .finally(() => {
        if (!annule) {
          setChargement(false);
        }
      });

    return () => {
      annule = true;
    };
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [...dependances, compteur]);

  return { donnees, chargement, erreur, recharger };
}
