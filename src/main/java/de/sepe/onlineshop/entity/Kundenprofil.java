package de.sepe.onlineshop.entity;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Entity
@Data
public class Kundenprofil {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;
    private String vorname;
    private Date geb;
    private int alter;

    public Kundenprofil(String name, String vorname, Date geb, int alter) {
        this.name = name;
        this.vorname = vorname;
        this.geb = geb;
        this.alter = alter;
    }

    public Kundenprofil() {

    }

    @Override
    public String toString() {
        return "Kundenprofil{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", vorname='" + vorname + '\'' +
                ", geb=" + geb +
                ", alter=" + alter +
                '}';
    }
}
