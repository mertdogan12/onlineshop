package de.sepe.onlineshop.service;

import de.sepe.onlineshop.dao.ProduktDAO;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.repository.IProduktRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProduktService {

    @Autowired
    IProduktRepository produktRepository;

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
}
