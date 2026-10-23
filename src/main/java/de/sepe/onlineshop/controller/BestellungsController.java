package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.dao.BestellungDAO;
import de.sepe.onlineshop.entity.BestellungEntity;
import de.sepe.onlineshop.enums.BestellungsStatus;
import de.sepe.onlineshop.service.BestellungService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.*;

@RestController
public class BestellungsController {

    @Autowired
    private BestellungService bestellungService;

    @PostMapping("/bestellung/bestellen")
    public ResponseEntity<BestellungEntity> showBestellungen(@RequestBody BestellungDAO bestellung) {
        try {
            return ResponseEntity.ok(bestellungService.bestellen(bestellung));
        } catch (MissingRequestValueException | NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.badRequest().headers(headers).build();
        }
    }

    @PutMapping("/bestellung/status/{id}")
    public ResponseEntity<BestellungEntity> changeStatus(@PathVariable Integer id, @RequestBody BestellungsStatus status) {
        try {
            return ResponseEntity.ok(bestellungService.changeStatus(id, status));
        } catch (NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.badRequest().headers(headers).build();
        }
    }

    @DeleteMapping("/bestellung/{id}")
    public ResponseEntity<Void> removeBestellung(@PathVariable Integer id) {
        try {
            bestellungService.removeBestellung(id);
            return ResponseEntity.noContent().build();
        } catch (NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.badRequest().headers(headers).build();
        }
    }
}
