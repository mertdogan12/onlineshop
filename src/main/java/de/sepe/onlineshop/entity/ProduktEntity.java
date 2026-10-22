package de.sepe.onlineshop.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "produkt")
@Data
public class ProduktEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private double preis;

    private String info;

    @ManyToMany
    @JoinColumn(name = "kategorie")
    private List<KategorieEntity> kategorien = new ArrayList<>();

    @ManyToMany(mappedBy = "produkte")
    private List<BestellungEntity> bestellungen = new ArrayList<>();

    public ProduktEntity(String name, double preis, String info) {
        this.name = name;
        this.preis = preis;
        this.info = info;
    }

}
