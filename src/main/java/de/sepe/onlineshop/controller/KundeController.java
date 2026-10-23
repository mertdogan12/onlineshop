package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.entity.BestellungEntity;
import de.sepe.onlineshop.entity.KundeEntity;
import de.sepe.onlineshop.service.KundeService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class KundeController {

    @Autowired
    KundeService kundeService;

    @GetMapping("/test")
    public String test(){
        return "moin";
    }

    @GetMapping("/kunden")
    public List<KundeEntity> getAllKu(){
        return kundeService.getAllKunden();
    }

    @PostMapping("/kunden")
    public KundeEntity saveNewKunde(@RequestBody KundeEntity kunde){
        return kundeService.saveKunde(kunde);
    }

    @DeleteMapping("/kunden/{id}")
    public ResponseEntity<KundeEntity> deleteKunde(@PathVariable int id){
        try {
            kundeService.deleteKunde(id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/kunde/bestellungen/{id}")
    public List<BestellungEntity> getKundeBestellungen(@PathVariable int id) throws NotFoundException {
        return kundeService.getBesFromKunde(id);
    }

}
