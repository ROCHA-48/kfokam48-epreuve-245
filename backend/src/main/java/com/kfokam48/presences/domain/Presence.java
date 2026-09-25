package com.kfokam48.presences.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Une presence est unique par couple (session, etudiant) : RG15. */
@Entity
@Table(name = "presence",
        uniqueConstraints = @UniqueConstraint(name = "uk_presence_session_etudiant",
                columnNames = {"session_id", "etudiant_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCours session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private SourcePresence source;

    @Column(name = "ajoute_at", nullable = false)
    private Instant ajouteAt;

    public Presence(SessionCours session, Etudiant etudiant, SourcePresence source, Instant ajouteAt) {
        this.session = session;
        this.etudiant = etudiant;
        this.source = source;
        this.ajouteAt = ajouteAt;
    }
}
