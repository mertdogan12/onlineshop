package de.sepe.onlineshop.service;

import de.sepe.onlineshop.dao.ProduktDAO;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.repository.IProduktRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister;
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
}
