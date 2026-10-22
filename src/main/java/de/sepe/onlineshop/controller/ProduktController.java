package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.dao.ProduktDAO;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.service.ProduktService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class ProduktController {

    @Autowired
    private ProduktService produktService;

    @GetMapping("/produkte")
    public ResponseEntity<List<ProduktEntity>> getAllProdukte() {
        return ResponseEntity.ok(produktService.getAllProdukte());
    }

    @PostMapping("/produkte")
    public ResponseEntity<ProduktEntity> createProdukt(@RequestBody ProduktDAO produkt) {
        return ResponseEntity.ok(produktService.createProdukt(produkt));
    }
}
