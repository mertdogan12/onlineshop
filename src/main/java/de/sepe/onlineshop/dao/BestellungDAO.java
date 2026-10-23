package de.sepe.onlineshop.dao;

import de.sepe.onlineshop.entity.AdresseEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BestellungDAO {

    private Integer kundeId;
    private List<Integer> produktIds;
    private double lieferungsKosten;
    private AdresseEntity adresse;

}
