/**
 * Couche API unique (F3) : aucun fetch ailleurs dans l'application.
 * Toutes les erreurs de l'API sont converties en ApiError { code, message }.
 */

const BASE_URL = import.meta.env.VITE_API_URL ?? "http://localhost:8080";

export class ApiError extends Error {
  readonly code: string;
  readonly status: number;

  constructor(code: string, message: string, status: number) {
    super(message);
    this.code = code;
    this.status = status;
  }
}

async function requete<T>(chemin: string, options: RequestInit = {}): Promise<T> {
  let reponse: Response;
  try {
    reponse = await fetch(`${BASE_URL}${chemin}`, {
      headers: { "Content-Type": "application/json" },
      ...options,
    });
  } catch {
    throw new ApiError("RESEAU_INDISPONIBLE", "L'API ne répond pas. Vérifiez qu'elle est démarrée.", 0);
  }

  if (reponse.status === 204) {
    return undefined as T;
  }

  const texte = await reponse.text();
  let corps: unknown = null;
  try {
    corps = texte ? JSON.parse(texte) : null;
  } catch {
    corps = null;
  }

  if (!reponse.ok) {
    const erreur = (corps ?? {}) as { code?: string; message?: string };
    throw new ApiError(
      erreur.code ?? "ERREUR_INCONNUE",
      erreur.message ?? "Une erreur est survenue.",
      reponse.status
    );
  }

  return corps as T;
}

export const apiClient = {
  get: <T>(chemin: string) => requete<T>(chemin),
  post: <T>(chemin: string, corps?: unknown) =>
    requete<T>(chemin, { method: "POST", body: corps === undefined ? undefined : JSON.stringify(corps) }),
  put: <T>(chemin: string, corps: unknown) => requete<T>(chemin, { method: "PUT", body: JSON.stringify(corps) }),
};
