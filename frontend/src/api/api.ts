/**
 * Toutes les operations de l'API, une fonction par operation du contrat.
 * Aucune regle metier n'est recalculee ici : la moyenne vient du serveur (F3).
 */
import { apiClient } from "./client";
import type {
  Etudiant,
  Exercice,
  LigneTableau,
  NoteRecue,
  Presence,
  Promotion,
  RelectureAFaire,
  RelectureDetail,
  Session,
} from "./types";

export const api = {
  // --- promotions et etudiants (Q1) ---
  listerPromotions: () => apiClient.get<Promotion[]>("/api/promotions"),
  listerEtudiants: (promotionId: number) =>
    apiClient.get<Etudiant[]>(`/api/promotions/${promotionId}/etudiants`),

  // --- sessions (EF2, EF10) ---
  ouvrirSession: (titre: string, promotionId: number) =>
    apiClient.post<{ id: number; code: string; ouvertureAt: string; expirationAt: string }>("/api/sessions", {
      titre,
      promotionId,
    }),
  listerSessions: (promotionId: number) => apiClient.get<Session[]>(`/api/sessions?promotionId=${promotionId}`),
  detailSession: (sessionId: number) => apiClient.get<Session>(`/api/sessions/${sessionId}`),
  cloturerSession: (sessionId: number) =>
    apiClient.post<{ id: number; clotureAt: string; cloturee: boolean }>(`/api/sessions/${sessionId}/cloture`),

  // --- presences (EF1, EF8) ---
  marquerPresence: (code: string, etudiantId: number) =>
    apiClient.post<Presence>("/api/presences", { code, etudiantId }),
  ajouterPresenceFormateur: (sessionId: number, etudiantId: number) =>
    apiClient.post<Presence>("/api/presences/formateur", { sessionId, etudiantId }),

  // --- exercices (EF3, EF9) ---
  deposerExercice: (sessionId: number, etudiantId: number, lien: string) =>
    apiClient.post<{ id: number; statut: string }>("/api/exercices", { sessionId, etudiantId, lien }),
  remplacerLien: (exerciceId: number, lien: string) =>
    apiClient.put<Exercice>(`/api/exercices/${exerciceId}/lien`, { lien }),
  listerExercices: (sessionId: number) => apiClient.get<Exercice[]>(`/api/sessions/${sessionId}/exercices`),

  // --- relectures (EF5, EF6, EF11, EF12) ---
  relecturesAFaire: (etudiantId: number) =>
    apiClient.get<RelectureAFaire[]>(`/api/etudiants/${etudiantId}/relectures-a-faire`),
  detailRelecture: (relectureId: number) => apiClient.get<RelectureDetail>(`/api/relectures/${relectureId}`),
  rendreRelecture: (relectureId: number, note: number, commentaire: string) =>
    apiClient.post<RelectureDetail>(`/api/relectures/${relectureId}`, { note, commentaire }),
  notesRecues: (etudiantId: number) => apiClient.get<NoteRecue[]>(`/api/etudiants/${etudiantId}/notes-recues`),

  // --- tableau du formateur (EF7) ---
  tableau: (promotionId: number) => apiClient.get<LigneTableau[]>(`/api/tableau?promotionId=${promotionId}`),
};
