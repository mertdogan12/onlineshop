package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.entity.KundeEntity;
import de.sepe.onlineshop.service.KundenprofilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KundenprofilController {

    @Autowired
    KundenprofilService kundenprofilService;

    @GetMapping("/kundenprofile")
    public List<KundeEntity> getAllKu(){
        return kundenprofilService.getAllKunden();
    }

}
