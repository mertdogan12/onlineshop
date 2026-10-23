package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.dao.BestellungDAO;
import de.sepe.onlineshop.entity.BestellungEntity;
import de.sepe.onlineshop.service.BestellungService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.MissingRequestValueException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class BestellungsController {

    @Autowired
    private BestellungService bestellungService;

    @PostMapping("/bestellen")
    public ResponseEntity<BestellungEntity> showBestellungen(@RequestBody BestellungDAO bestellung) {
        try {
            return ResponseEntity.ok(bestellungService.bestellen(bestellung));
        } catch (MissingRequestValueException | NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.badRequest().headers(headers).build();
        }
    }
}
