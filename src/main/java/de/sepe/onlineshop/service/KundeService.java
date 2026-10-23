package de.sepe.onlineshop.service;

import de.sepe.onlineshop.entity.BestellungEntity;
import de.sepe.onlineshop.entity.KundeEntity;
import de.sepe.onlineshop.repository.IKundeRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KundeService {

    @Autowired
    IKundeRepository kundeRepo;

    public List<KundeEntity> getAllKunden(){

        return kundeRepo.findAll();
    }

    public KundeEntity getKunde(int id) throws NotFoundException {
        return kundeRepo.findById(id).orElseThrow(() -> new NotFoundException("Kunde mit der ID " + id + " nicht gefunden"));
    }

    public KundeEntity saveKunde(KundeEntity newKu){
        return kundeRepo.save(newKu);
    }

    public void deleteKunde(int id) throws NotFoundException{
        KundeEntity kunde = getKunde(id);

        kundeRepo.delete(kunde);
    }

    public List<BestellungEntity> getBesFromKunde(int id) throws NotFoundException {
        KundeEntity kunde = getKunde(id);
        return kunde.getBestellungen();
    }

}
