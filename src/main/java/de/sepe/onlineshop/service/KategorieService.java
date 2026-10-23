package de.sepe.onlineshop.service;

import de.sepe.onlineshop.dao.KategorieDAO;
import de.sepe.onlineshop.entity.KategorieEntity;
import de.sepe.onlineshop.repository.IKategorieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class KategorieService {

    @Autowired
    private IKategorieRepository kategorieRepository;

    public List<KategorieEntity> getAllKategorien() {
        return kategorieRepository.findAll();
    }

    public KategorieEntity createKategorie(KategorieDAO kategorie) {
        KategorieEntity kategorieEntity = new KategorieEntity(kategorie.getName(), kategorie.getBeschreibung());

        return kategorieRepository.save(kategorieEntity);
    }
}
