package de.sepe.onlineshop.service;

import de.sepe.onlineshop.dao.KategorieDAO;
import de.sepe.onlineshop.entity.KategorieEntity;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.repository.IKategorieRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class KategorieService {

    @Autowired
    private IKategorieRepository kategorieRepository;

    @Autowired
    private ProduktService produktService;

    public List<KategorieEntity> getAllKategorien() {
        return kategorieRepository.findAll();
    }

    public KategorieEntity createKategorie(KategorieDAO kategorie) {
        KategorieEntity kategorieEntity = new KategorieEntity(kategorie.getName(), kategorie.getBeschreibung());

        return kategorieRepository.save(kategorieEntity);
    }

    public void deleteKategorie(Integer id) throws NotFoundException {
        KategorieEntity kategorieEntity = kategorieRepository.findById(id).orElseThrow(() ->  new NotFoundException("Kategorie not found: " + id));

        for (ProduktEntity produktEntity : kategorieEntity.getProdukte()) {
            produktService.removeKategorie(produktEntity.getId(), List.of(kategorieEntity.getId()));
        }

        kategorieRepository.delete(kategorieEntity);
    }
}
