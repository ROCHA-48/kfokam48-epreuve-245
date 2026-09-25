package com.kfokam48.presences.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** Aucun mot de passe : l'identite est declarative (Q1, ENF4). */
@Entity
@Table(name = "etudiant")
@Getter
@Setter
@NoArgsConstructor
public class Etudiant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 160)
    private String nom;

    @Column(length = 200)
    private String email;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "promotion_id", nullable = false)
    private Promotion promotion;

    public Etudiant(String nom, String email, Promotion promotion) {
        this.nom = nom;
        this.email = email;
        this.promotion = promotion;
    }
}
