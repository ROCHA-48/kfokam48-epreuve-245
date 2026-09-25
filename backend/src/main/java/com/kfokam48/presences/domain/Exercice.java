package com.kfokam48.presences.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/** Un seul exercice par couple (session, etudiant) : Z8. */
@Entity
@Table(name = "exercice",
        uniqueConstraints = @UniqueConstraint(name = "uk_exercice_session_etudiant",
                columnNames = {"session_id", "etudiant_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Exercice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private SessionCours session;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "etudiant_id", nullable = false)
    private Etudiant etudiant;

    @Column(nullable = false, length = 1000)
    private String lien;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private StatutExercice statut;

    @Column(name = "depose_at", nullable = false)
    private Instant deposeAt;

    @Column(name = "maj_lien_at")
    private Instant majLienAt;

    public Exercice(SessionCours session, Etudiant etudiant, String lien) {
        this.session = session;
        this.etudiant = etudiant;
        this.lien = lien;
        this.statut = StatutExercice.EN_ATTENTE_ASSIGNATION;
        this.deposeAt = Instant.now();
    }
}
