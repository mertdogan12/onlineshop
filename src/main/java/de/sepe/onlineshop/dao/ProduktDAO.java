package de.sepe.onlineshop.dao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProduktDAO {

    private String name;

    private int preis;

    private String info;
}
