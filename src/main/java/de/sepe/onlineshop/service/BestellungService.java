package de.sepe.onlineshop.service;

import de.sepe.onlineshop.dao.BestellungDAO;
import de.sepe.onlineshop.entity.BestellungEntity;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.repository.IBestellungRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.MissingRequestValueException;

import java.sql.Date;

@Service
public class BestellungService {

    @Autowired
    private IBestellungRepository bestellungRepository;

    @Autowired
    private ProduktService produktService;

    public BestellungEntity bestellen(BestellungDAO bestellungDAO) throws NotFoundException, MissingRequestValueException {
        if (bestellungDAO == null || bestellungDAO.getProduktIds() == null || bestellungDAO.getProduktIds().isEmpty()) {
            throw new MissingRequestValueException("BestellungDAO is invalid");
        }

        double gesamtKosten = bestellungDAO.getLieferungsKosten();

        BestellungEntity bestellung = new BestellungEntity(new Date(System.currentTimeMillis()), bestellungDAO.getLieferungsKosten(), 0);
        bestellung.setAdresse(bestellungDAO.getAdresse());

        for (Integer produktId : bestellungDAO.getProduktIds()) {
            ProduktEntity produktEntity = produktService.getProduktById(produktId);
            bestellung.addProdukt(produktEntity);
            gesamtKosten += produktEntity.getPreis();
        }

        bestellung.setGesamtKosten(gesamtKosten);
        bestellungRepository.save(bestellung);

        return bestellung;
    }
}
