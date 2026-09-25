package com.kfokam48.presences.web;

import com.kfokam48.presences.domain.Etudiant;
import com.kfokam48.presences.domain.Exercice;
import com.kfokam48.presences.domain.Promotion;
import com.kfokam48.presences.domain.SessionCours;
import com.kfokam48.presences.repository.EtudiantRepository;
import com.kfokam48.presences.repository.ExerciceRepository;
import com.kfokam48.presences.repository.PresenceRepository;
import com.kfokam48.presences.repository.PromotionRepository;
import com.kfokam48.presences.repository.RelectureRepository;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Issue #18 : deux etudiants pointent « en meme temps », un seul apparait dans
 * la liste du formateur. Reproduction du bug AVANT correctif (enveloppe etape 3 :
 * un test qui echoue et qui demontre le bug).
 *
 * La presence est unique par couple (session, etudiant) : RG15 concerne le meme
 * etudiant, jamais deux etudiants differents. Un exercice en attente d'assignation
 * (Z1) est pose dans le premier test : chaque nouvelle presence declenche
 * l'assignation du relecteur (RG13), et c'est la que les deux transactions
 * simultanees se battaient sur la contrainte uk_relecture_exercice.
 *
 * Base H2 en memoire : tourne sans base locale ni Docker (B6).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PresenceConcurrenteIntegrationTest {

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
    @Autowired
    private ExerciceRepository exerciceRepository;
    @Autowired
    private RelectureRepository relectureRepository;

    private Promotion promotion;
    private Etudiant awa;
    private Etudiant elsa;
    private Etudiant brice;
    private SessionCours session;

    @BeforeEach
    void preparerLesDonnees() {
        // ordre impose par la clef etrangere : les relectures d'abord (issue #18)
        relectureRepository.deleteAll();
        exerciceRepository.deleteAll();
        presenceRepository.deleteAll();
        sessionRepository.deleteAll();
        etudiantRepository.deleteAll();
        promotionRepository.deleteAll();

        promotion = promotionRepository.save(new Promotion("KF48-2026"));
        awa = etudiantRepository.save(new Etudiant("Awa Nkolo", "awa@kfokam48.cm", promotion));
        elsa = etudiantRepository.save(new Etudiant("Elsa Ngo Bell", "elsa@kfokam48.cm", promotion));
        brice = etudiantRepository.save(new Etudiant("Brice Fotso", "brice@kfokam48.cm", promotion));

        session = new SessionCours();
        session.setTitre("Session concurrente");
        session.setCode("K7M2QD");
        session.setPromotion(promotion);
        session.setOuvertureAt(Instant.now().minus(2, ChronoUnit.MINUTES));
        session.setExpirationAt(Instant.now().plus(13, ChronoUnit.MINUTES));
        session = sessionRepository.save(session);
    }

    /** Lance un pointage par thread en simultane et renvoie les codes HTTP obtenus. */
    private List<Integer> pointerSimultanement(List<Long> etudiantIds) throws Exception {
        ExecutorService executeur = Executors.newFixedThreadPool(etudiantIds.size());
        CountDownLatch pret = new CountDownLatch(etudiantIds.size());
        CountDownLatch partir = new CountDownLatch(1);
        List<Integer> codes = Collections.synchronizedList(new ArrayList<>());

        for (Long etudiantId : etudiantIds) {
            executeur.submit(() -> {
                pret.countDown();
                try {
                    partir.await();
                    MvcResult resultat = mockMvc.perform(post("/api/presences")
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content("{\"code\":\"K7M2QD\",\"etudiantId\":" + etudiantId + "}"))
                            .andReturn();
                    codes.add(resultat.getResponse().getStatus());
                } catch (Exception e) {
                    codes.add(500);
                }
            });
        }
        pret.await(5, TimeUnit.SECONDS);
        partir.countDown();
        executeur.shutdown();
        executeur.awaitTermination(30, TimeUnit.SECONDS);
        return codes;
    }

    @Test
    @DisplayName("Issue #18 : deux etudiants pointent en meme temps, les deux presences apparaissent")
    void deuxEtudiantsSimultanesPassentTousLesDeux() throws Exception {
        // Un exercice attend un relecteur : l'assignation (RG13) est declenchee par
        // chaque nouvelle presence, et les deux transactions se battaient dessus.
        exerciceRepository.save(new Exercice(session, awa, "https://exercice.kfokam48.cm/awa"));

        List<Integer> codes = pointerSimultanement(List.of(elsa.getId(), brice.getId()));

        assertThat(codes).containsExactlyInAnyOrder(201, 201);
        assertThat(presenceRepository.count()).isEqualTo(2);
    }

    @Test
    @DisplayName("Le meme etudiant pointe deux fois en meme temps : un 201 et un 409, jamais deux presences (RG15)")
    void memeEtudiantDeuxFoisSimultanement() throws Exception {
        List<Integer> codes = pointerSimultanement(List.of(elsa.getId(), elsa.getId()));

        assertThat(codes).containsExactlyInAnyOrder(201, 409);
        assertThat(presenceRepository.count()).isEqualTo(1);
    }
}
