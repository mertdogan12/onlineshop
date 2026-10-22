package de.sepe.onlineshop.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "bestellung")
@Data
public class BestellungEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date datum;

    private double lieferKosten;

    private double gesamtKosten;

    @ManyToMany
    @JoinColumn(name = "produkt")
    private List<ProduktEntity> produkte = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "kunde")
    private KundeEntity kunde;

    @ManyToOne
    @JoinColumn(name = "adresse")
    private AdresseEntity adresse;

    public BestellungEntity(Date datum, double lieferKosten, double gesamtKosten) {
        this.datum = datum;
        this.lieferKosten = lieferKosten;
        this.gesamtKosten = gesamtKosten;
    }
}
