package de.sepe.onlineshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class Adresse {

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
    private Kundenprofil kundenprofil;
}
