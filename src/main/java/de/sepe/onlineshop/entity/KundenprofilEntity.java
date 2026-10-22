package de.sepe.onlineshop.entity;

import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Entity(name = "kundenprofil")
@Data
public class KundenprofilEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String vorname;
    private Date geb;

    @OneToOne(mappedBy = "Kundenprofil")
    private KundeEntity kunde;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name ="adresse")
    private AdresseEntity adresse;

    public KundenprofilEntity(String name, String vorname, Date geb) {
        this.name = name;
        this.vorname = vorname;
        this.geb = geb;
    }



    public KundenprofilEntity() {

    }

    @Override
    public String toString() {
        return "Kundenprofil{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vorname='" + vorname + '\'' +
                ", geb=" + geb +
                '}';
    }
}
