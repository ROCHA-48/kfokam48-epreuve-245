package com.kfokam48.presences.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

/**
 * Deux relecteurs distincts par exercice (issue #19, ancienne regle RG4 issue de Q6).
 * Le relecteur est toujours different de l'auteur (Q5) : la regle est portee par le
 * service, une contrainte SQL ne pouvant pas comparer deux tables.
 */
@Entity
@Table(name = "relecture",
        uniqueConstraints = @UniqueConstraint(name = "uk_relecture_exercice_relecteur",
                columnNames = {"exercice_id", "relecteur_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Relecture {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exercice_id", nullable = false)
    private Exercice exercice;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "relecteur_id", nullable = false)
    private Etudiant relecteur;

    /** Null tant que la relecture n'est pas rendue. Entier de 0 a 20 (RG3). */
    @Column
    private Integer note;

    @Column(length = 4000)
    private String commentaire;

    @Column(name = "assignee_at", nullable = false)
    private Instant assigneeAt;

    @Column(name = "rendue_at")
    private Instant rendueAt;

    public Relecture(Exercice exercice, Etudiant relecteur) {
        this.exercice = exercice;
        this.relecteur = relecteur;
        this.assigneeAt = Instant.now();
    }

    public boolean isRendue() {
        return rendueAt != null;
    }
}
