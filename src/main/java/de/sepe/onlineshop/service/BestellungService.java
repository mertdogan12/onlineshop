package de.sepe.onlineshop.service;

import de.sepe.onlineshop.dao.BestellungDAO;
import de.sepe.onlineshop.entity.BestellungEntity;
import de.sepe.onlineshop.entity.KundeEntity;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.enums.BestellungsStatus;
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

    @Autowired
    private KundeService kundeService;

    public BestellungEntity bestellen(BestellungDAO bestellungDAO) throws NotFoundException, MissingRequestValueException {
        if (bestellungDAO == null || bestellungDAO.getProduktIds() == null || bestellungDAO.getProduktIds().isEmpty()) {
            throw new MissingRequestValueException("BestellungDAO is invalid");
        }

        KundeEntity kunde = kundeService.getKunde(bestellungDAO.getKundeId());

        double gesamtKosten = bestellungDAO.getLieferungsKosten();

        BestellungEntity bestellung = new BestellungEntity(new Date(System.currentTimeMillis()), bestellungDAO.getLieferungsKosten(), 0);
        bestellung.setAdresse(bestellungDAO.getAdresse());
        bestellung.setStatus(BestellungsStatus.PENDING);
        bestellung.setKunde(kunde);

        for (Integer produktId : bestellungDAO.getProduktIds()) {
            ProduktEntity produktEntity = produktService.getProduktById(produktId);
            bestellung.addProdukt(produktEntity);
            gesamtKosten += produktEntity.getPreis();
        }

        bestellung.setGesamtKosten(gesamtKosten);
        bestellungRepository.save(bestellung);

        return bestellung;
    }

    public BestellungEntity changeStatus(Integer bestllungsID, BestellungsStatus status) throws NotFoundException {
        BestellungEntity bestellung = bestellungRepository.findById(bestllungsID).orElseThrow(() -> new NotFoundException("Bestellung not found: " + bestllungsID));
        bestellung.setStatus(status);
        bestellungRepository.save(bestellung);
        return bestellung;
    }
}
