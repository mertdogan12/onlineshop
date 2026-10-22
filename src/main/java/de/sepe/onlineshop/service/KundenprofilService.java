package de.sepe.onlineshop.service;

import de.sepe.onlineshop.entity.KundeEntity;
import de.sepe.onlineshop.entity.KundenprofilEntity;
import de.sepe.onlineshop.repository.IKundeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KundenprofilService {

    @Autowired
    IKundeRepository kundenprofilRepo;

    public List<KundeEntity> getAllKunden(){

        return kundenprofilRepo.findAll();
    }
}
