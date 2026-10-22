package de.sepe.onlineshop.service;

import de.sepe.onlineshop.entity.KundeEntity;
import de.sepe.onlineshop.repository.IKundeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KundeService {

    @Autowired
    IKundeRepository IkundeRepository;

    public List<KundeEntity> getAllKunden(){


    }
}
