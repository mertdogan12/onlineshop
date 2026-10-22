package de.sepe.onlineshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import javax.persistence.*;

@Entity(name = "adresse")
@Data
public class AdresseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String vorname;
    private String ort;
    private int plz;
    private String land;

    @OneToOne(mappedBy = "adresse")
    @JsonIgnore
    private KundenprofilEntity kundenprofil;

    public AdresseEntity(String name, String vorname, String ort, int plz, String land) {
        this.name = name;
        this.vorname = vorname;
        this.ort = ort;
        this.plz = plz;
        this.land = land;
    }
}
