package de.sepe.onlineshop.service;

import de.sepe.onlineshop.entity.KundenprofilEntity;
import de.sepe.onlineshop.repository.IKundenprofilRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KundenprofilService {

    @Autowired
    IKundenprofilRepository kundenprofilRepo;

    public List<KundenprofilEntity> getAllKpr(){

        return kundenprofilRepo.findAll();
    }

    public KundenprofilEntity saveKundenprofil(KundenprofilEntity newKpr){
        return kundenprofilRepo.save(newKpr);
    }
}
