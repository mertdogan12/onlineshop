package de.sepe.onlineshop.service;

import de.sepe.onlineshop.dao.ProduktDAO;
import de.sepe.onlineshop.entity.KategorieEntity;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.repository.IKategorieRepository;
import de.sepe.onlineshop.repository.IProduktRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduktService {

    @Autowired
    IProduktRepository produktRepository;
    @Autowired
    KategorieService kategorieService;

    public List<ProduktEntity> getAllProdukte() {
        return produktRepository.findAll();
    }

    public ProduktEntity getProduktById(Integer id) throws NotFoundException {
        return produktRepository.findById(id).orElseThrow(() -> new NotFoundException("Produkt mit der ID " + id + " nicht gefunden"));
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
        ProduktEntity produktEntity = getProduktById(id);

        produktEntity.setName(produkt.getName());
        produktEntity.setPreis(produkt.getPreis());
        produktEntity.setInfo(produkt.getInfo());

        return produktRepository.save(produktEntity);
    }

    public ProduktEntity addKategorie(Integer id, List<Integer> kategorieIDs) throws NotFoundException {
        ProduktEntity produktEntity = getProduktById(id);

        for (Integer kategorieID : kategorieIDs) {
            KategorieEntity ke = kategorieService.getKategorieById(kategorieID);
            produktEntity.addKategorie(ke);
        }

        return produktRepository.save(produktEntity);
    }

    public ProduktEntity removeKategorie(Integer id, List<Integer> kategorieIDs) throws NotFoundException {
        ProduktEntity produktEntity = getProduktById(id);

        for (Integer kategorieID : kategorieIDs) {
            KategorieEntity ke = kategorieService.getKategorieById(kategorieID);
            produktEntity.removeKategorie(ke);
        }

        return produktRepository.save(produktEntity);
    }
}
