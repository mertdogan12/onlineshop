package de.sepe.onlineshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import de.sepe.onlineshop.enums.BestellungsStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "bestellung")
@Data
@NoArgsConstructor
public class BestellungEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private Date datum;

    private double lieferKosten;

    private double gesamtKosten;

    private BestellungsStatus status;

    @ManyToMany
    @JoinColumn(name = "produkt")
    private List<ProduktEntity> produkte = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "kunde")
    @JsonIgnore
    private KundeEntity kunde;

    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "adresse")
    private AdresseEntity adresse;

    public BestellungEntity(Date datum, double lieferKosten, double gesamtKosten) {
        this.datum = datum;
        this.lieferKosten = lieferKosten;
        this.gesamtKosten = gesamtKosten;
    }

    public void addProdukt(ProduktEntity produktEntity) {
        produkte.add(produktEntity);
    }
}
