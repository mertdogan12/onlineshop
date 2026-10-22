package de.sepe.onlineshop.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "kategorie")
@Data
@NoArgsConstructor
public class KategorieEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String beschreibung;

    @ManyToMany(mappedBy = "kategorien")
    private List<ProduktEntity> produkte = new ArrayList<>();

    public KategorieEntity(String name, String beschreibung) {
        this.name = name;
        this.beschreibung = beschreibung;
    }

}
