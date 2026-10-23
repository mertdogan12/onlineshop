package de.sepe.onlineshop.service;

import de.sepe.onlineshop.dao.KategorieDAO;
import de.sepe.onlineshop.dao.ProduktDAO;
import de.sepe.onlineshop.entity.KategorieEntity;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.repository.IKategorieRepository;
import de.sepe.onlineshop.repository.IProduktRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProduktService {

    @Autowired
    IProduktRepository produktRepository;
    @Autowired
    IKategorieRepository kategorieRepository;

    public List<ProduktEntity> getAllProdukte() {
        return produktRepository.findAll();
    }

    public ProduktEntity createProdukt(ProduktDAO produkt) {
        ProduktEntity produktEntity = new ProduktEntity();
        produktEntity.setName(produkt.getName());
        produktEntity.setPreis(produkt.getPreis());
        produktEntity.setInfo(produkt.getInfo());

        return produktRepository.save(produktEntity);
    }

    public void deleteProdukt(Integer id) throws NotFoundException {
        ProduktEntity produktEntity = produktRepository.findById(id).orElseThrow(() -> new NotFoundException("Produkt mit der ID " + id + " nicht gefunden"));

        produktRepository.delete(produktEntity);
    }

    public ProduktEntity updateProdukt(Integer id, ProduktDAO produkt) throws NotFoundException {
        ProduktEntity produktEntity = produktRepository.findById(id).orElseThrow(() -> new NotFoundException("Produkt mit der ID " + id + " nicht gefunden"));

        produktEntity.setName(produkt.getName());
        produktEntity.setPreis(produkt.getPreis());
        produktEntity.setInfo(produkt.getInfo());

        return produktRepository.save(produktEntity);
    }

    public ProduktEntity addKategorie(Integer id, List<Integer> kategorieIDs) throws NotFoundException {
        ProduktEntity produktEntity = produktRepository.findById(id).orElseThrow(() -> new NotFoundException("Produkt mit der ID " + id + " nicht gefunden"));

        for (Integer kategorieID : kategorieIDs) {
            KategorieEntity ke = kategorieRepository.findById(kategorieID).orElseThrow(() -> new NotFoundException("Kategorie mit der ID " + kategorieID + " nicht gefunden"));
            produktEntity.addKategorie(ke);
        }

        return produktRepository.save(produktEntity);
    }
}
