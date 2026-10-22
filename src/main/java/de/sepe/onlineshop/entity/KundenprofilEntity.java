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
    private int age;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name ="adresse")
    private AdresseEntity adresse;

    public KundenprofilEntity(String name, String vorname, Date geb, int age) {
        this.name = name;
        this.vorname = vorname;
        this.geb = geb;
        this.age = age;
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
                ", age=" + age +
                '}';
    }
}
