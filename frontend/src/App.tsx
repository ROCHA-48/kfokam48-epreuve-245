import { useEffect, useState } from "react";
import { Link, Navigate, Route, Routes } from "react-router-dom";
import { api } from "./api/api";
import { ChoixEtudiant } from "./components/ChoixEtudiant";
import { useEtudiantSelectionne } from "./hooks/useEtudiantSelectionne";
import { EtudiantPage } from "./pages/EtudiantPage";
import { FormateurPage } from "./pages/FormateurPage";
import { RelecteurPage } from "./pages/RelecteurPage";
import { Promotion } from "./api/types";

export default function App() {
  const [promotions, setPromotions] = useState<Promotion[]>([]);
  const [promotionId, setPromotionId] = useState<number | null>(null);
  const [etudiantId, setEtudiantId] = useEtudiantSelectionne();

  useEffect(() => {
    api
      .listerPromotions()
      .then((donnees) => {
        setPromotions(donnees);
        if (donnees.length > 0) {
          setPromotionId(donnees[0].id);
        }
      })
      .catch(() => setPromotions([]));
  }, []);

  return (
    <div className="application">
      <header>
        <h1>Présences &amp; Relectures — KFOKAM48</h1>
        <nav>
          <Link to="/formateur">Formateur</Link>
          <Link to="/etudiant">Étudiant</Link>
          <Link to="/relecteur">Relecteur</Link>
        </nav>
        <label className="champ">
          Promotion
          <select
            value={promotionId ?? ""}
            onChange={(evenement) => {
              setPromotionId(evenement.target.value ? Number(evenement.target.value) : null);
              setEtudiantId(null);
            }}
          >
            <option value="">— choisir —</option>
            {promotions.map((promotion) => (
              <option key={promotion.id} value={promotion.id}>
                {promotion.nom}
              </option>
            ))}
          </select>
        </label>
        <ChoixEtudiant promotionId={promotionId} etudiantId={etudiantId} onChange={setEtudiantId} />
      </header>

      <main>
        <Routes>
          <Route path="/" element={<Navigate to="/formateur" replace />} />
          <Route path="/formateur" element={<FormateurPage promotionId={promotionId} etudiantId={etudiantId} />} />
          <Route path="/etudiant" element={<EtudiantPage promotionId={promotionId} etudiantId={etudiantId} />} />
          <Route path="/relecteur" element={<RelecteurPage etudiantId={etudiantId} />} />
        </Routes>
      </main>
    </div>
  );
}
