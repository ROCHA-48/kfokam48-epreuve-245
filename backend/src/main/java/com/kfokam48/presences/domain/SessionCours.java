package com.kfokam48.presences.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Session de cours. Le code de presence expire 15 minutes apres l'ouverture (RG1)
 * et la cloture ferme definitivement pointage, depot et relecture (RG14).
 */
@Entity
@Table(name = "session_cours")
@Getter
@Setter
@NoArgsConstructor
public class SessionCours {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String titre;

    @Column(nullable = false, unique = true, length = 12)
    private String code;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    @Column(name = "ouverture_at", nullable = false)
    private Instant ouvertureAt;

    @Column(name = "expiration_at", nullable = false)
    private Instant expirationAt;

    /** Null tant que la session est ouverte. */
    @Column(name = "cloture_at")
    private Instant clotureAt;

    public boolean isCloturee() {
        return clotureAt != null;
    }

    public boolean isCodeExpire(Instant maintenant) {
        return maintenant.isAfter(expirationAt);
    }
}
