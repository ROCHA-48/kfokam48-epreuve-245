package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.Presence;
import com.kfokam48.presences.domain.Promotion;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.domain.SourcePresence;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.PresenceRepository;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.repository.SessionCoursRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test d'integration sur un endpoint impose (B6 et issues 3 et 4) :
 * POST /api/presences, avec les codes 201, 409, 410 et 400 du contrat.
 * Il tourne sur un poste vierge : base H2 en memoire, aucune base locale,
 * aucun Docker (voir application-test.yml).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PresenceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private EtudiantRepository etudiantRepository;
    @Autowired
    private SessionCoursRepository sessionRepository;
    @Autowired
    private PresenceRepository presenceRepository;

    private Promotion promotion;
    private Etudiant awa;
    private Etudiant elsa;
    private SessionCours session;

    @BeforeEach
    void preparerLesDonnees() {
        presenceRepository.deleteAll();
        sessionRepository.deleteAll();
        etudiantRepository.deleteAll();
        promotionRepository.deleteAll();

        promotion = promotionRepository.save(new Promotion("KF48-2026"));
        awa = etudiantRepository.save(new Etudiant("Awa Nkolo", "awa@kfokam48.cm", promotion));
        elsa = etudiantRepository.save(new Etudiant("Elsa Ngo Bell", "elsa@kfokam48.cm", promotion));

        session = new SessionCours();
        session.setTitre("Cours de Java");
        session.setCode("K7M2QD");
        session.setPromotion(promotion);
        session.setOuvertureAt(Instant.now().minus(2, ChronoUnit.MINUTES));
        session.setExpirationAt(Instant.now().plus(13, ChronoUnit.MINUTES));
        session = sessionRepository.save(session);
    }

    private String corps(String code, Long etudiantId) {
        return "{\"code\":\"" + code + "\",\"etudiantId\":" + etudiantId + "}";
    }

    @Test
    @DisplayName("Un code valide enregistre la presence : 201 { id, sessionId, etudiantId, source }")
    void marqueLaPresenceAvecUnCodeValide() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps("K7M2QD", elsa.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.sessionId").value(session.getId()))
                .andExpect(jsonPath("$.etudiantId").value(elsa.getId()))
                .andExpect(jsonPath("$.source").value("ETUDIANT"));

        assertThat(presenceRepository.findBySessionIdAndEtudiantId(session.getId(), elsa.getId()))
                .isPresent();
    }

    @Test
    @DisplayName("Une presence deja enregistree est refusee : 409 DEJA_PRESENT (RG15)")
    void refuseUneSecondePresence() throws Exception {
        presenceRepository.save(new Presence(session, awa, SourcePresence.ETUDIANT, Instant.now()));

        MvcResult resultat = mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps("K7M2QD", awa.getId())))
                .andExpect(status().isConflict())
                .andReturn();

        assertThat(resultat.getResponse().getContentAsString())
                .contains("\"code\":\"DEJA_PRESENT\"")
                .contains("\"message\"");
    }

    @Test
    @DisplayName("Un code inconnu est refuse : 400 CODE_INCONNU")
    void refuseUnCodeInconnu() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps("ZZZZZZ", elsa.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("CODE_INCONNU"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    @DisplayName("Un code expire est refuse : 410 CODE_EXPIRE (RG1, RG12)")
    void refuseUnCodeExpire() throws Exception {
        SessionCours expiree = new SessionCours();
        expiree.setTitre("Seance terminee");
        expiree.setCode("EXPIRE");
        expiree.setPromotion(promotion);
        expiree.setOuvertureAt(Instant.now().minus(40, ChronoUnit.MINUTES));
        expiree.setExpirationAt(Instant.now().minus(25, ChronoUnit.MINUTES));
        sessionRepository.save(expiree);

        MvcResult resultat = mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(corps("EXPIRE", elsa.getId())))
                .andExpect(status().isGone())
                .andReturn();

        assertThat(resultat.getResponse().getContentAsString())
                .contains("\"code\":\"CODE_EXPIRE\"");
    }

    @Test
    @DisplayName("Un corps incomplet renvoie 400 au format { code, message } (B4, ENF3)")
    void refuseUnCorpsIncomplet() throws Exception {
        mockMvc.perform(post("/api/presences")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("DONNEE_INVALIDE"));
    }

    @Test
    @DisplayName("Le tableau du formateur expose exactement les cles du contrat (EF7, Q16)")
    void tableauConformeAuContrat() throws Exception {
        String corps = mockMvc.perform(get("/api/tableau")
                        .param("promotionId", String.valueOf(promotion.getId())))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertThat(corps).contains("etudiantId", "nom", "presences", "exercicesDeposes", "moyenne",
                "relecturesEnAttente");
    }

    @Test
    @DisplayName("Un lien mal forme renvoie 400 LIEN_INVALIDE (contrat, B2)")
    void refuseUnLienInvalide() throws Exception {
        mockMvc.perform(post("/api/exercices")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"sessionId\":" + session.getId() + ",\"etudiantId\":" + elsa.getId()
                                + ",\"lien\":\"pas-une-url\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("LIEN_INVALIDE"));
    }

    @Test
    @DisplayName("Une note non entiere renvoie 400 NOTE_INVALIDE (RG3, contrat)")
    void refuseUneNoteNonEntiere() throws Exception {
        mockMvc.perform(post("/api/relectures/999999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"note\":12.5,\"commentaire\":\"non entiere\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("NOTE_INVALIDE"));
    }

    @Test
    @DisplayName("Une promotion inconnue renvoie 404 PROMOTION_INCONNUE (contrat)")
    void refuseUnePromotionInconnue() throws Exception {
        mockMvc.perform(get("/api/tableau").param("promotionId", "999999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("PROMOTION_INCONNUE"));
    }
}
