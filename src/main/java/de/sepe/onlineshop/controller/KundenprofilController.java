package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.entity.KundeEntity;
import de.sepe.onlineshop.entity.KundenprofilEntity;
import de.sepe.onlineshop.service.KundenprofilService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class KundenprofilController {

    @Autowired
    KundenprofilService kundenprofilService;

    @GetMapping("/kundenprofile")
    public List<KundenprofilEntity> getAllKundenprofile(){
        return kundenprofilService.getAllKpr();
    }

    @PostMapping("/kundenprofile")
    public KundenprofilEntity saveNewKundenprofil(@RequestBody KundenprofilEntity kundenprofil){
        return kundenprofilService.saveKundenprofil(kundenprofil);
    }

}
