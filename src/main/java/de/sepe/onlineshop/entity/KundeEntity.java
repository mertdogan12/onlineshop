package de.sepe.onlineshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "kunde")
@Data
@NoArgsConstructor
public class KundeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "kundenprofil")
    private KundenprofilEntity kundenprofil;

    @OneToMany(mappedBy = "kunde")
    private List<BestellungEntity> bestellungen = new ArrayList<>();
}
