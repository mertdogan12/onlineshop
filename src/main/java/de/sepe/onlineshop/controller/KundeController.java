package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.service.KundeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class KundeController {

    /*@Autowired
    KundeService kundeService;*/

    @GetMapping("/test")
    public String test(){
        return "moin";
    }

}
