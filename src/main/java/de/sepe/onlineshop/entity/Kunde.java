package de.sepe.onlineshop.entity;

import javax.persistence.*;

@Entity
public class Kunde {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "kundenprofil")
    private Kundenprofil kundenprofil;
}
