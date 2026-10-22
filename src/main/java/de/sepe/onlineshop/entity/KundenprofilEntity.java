package de.sepe.onlineshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Date;

@Entity(name = "kundenprofil")
@Data
@NoArgsConstructor
public class KundenprofilEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String vorname;
    private Date geb;

    @OneToOne(mappedBy = "kundenprofil")
    @JsonIgnore
    private KundeEntity kunde;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name ="adresse")
    private AdresseEntity adresse;

    public KundenprofilEntity(String name, String vorname, Date geb) {
        this.name = name;
        this.vorname = vorname;
        this.geb = geb;
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
