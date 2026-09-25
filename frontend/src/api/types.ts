/** Types des reponses de l'API : ils suivent api/contrat.yaml. */

export type Promotion = { id: number; nom: string };
export type Etudiant = { id: number; nom: string; promotionId: number };

export type Session = {
  id: number;
  titre: string;
  code: string;
  promotionId: number;
  ouvertureAt: string;
  expirationAt: string;
  clotureAt: string | null;
  cloturee: boolean;
};

export type Presence = { id: number; sessionId: number; etudiantId: number; source: "ETUDIANT" | "FORMATEUR" };

export type StatutExercice = "EN_ATTENTE_ASSIGNATION" | "EN_ATTENTE_RELECTURE" | "RELU";

export type Exercice = {
  id: number;
  sessionId: number;
  etudiantId: number;
  lien: string;
  statut: StatutExercice;
  deposeAt: string;
  majLienAt: string | null;
};

export type LigneTableau = {
  etudiantId: number;
  nom: string;
  presences: number;
  exercicesDeposes: number;
  moyenne: number | null;
  relecturesEnAttente: number;
};

export type RelectureAFaire = {
  relectureId: number;
  exerciceId: number;
  sessionId: number;
  lien: string;
  statut: StatutExercice;
  assigneeAt: string;
};

export type RelectureDetail = {
  id: number;
  exerciceId: number;
  lien: string;
  statut: StatutExercice;
  note: number | null;
  commentaire: string | null;
  assigneeAt: string;
  rendueAt: string | null;
};

/** Q8 / RG5 : cette reponse ne contient jamais l'identite du relecteur. */
export type NoteRecue = {
  relectureId: number;
  exerciceId: number;
  note: number;
  commentaire: string;
  rendueAt: string;
};
