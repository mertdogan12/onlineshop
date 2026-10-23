package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.dao.ProduktDAO;
import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.service.ProduktService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    @DeleteMapping("/produkte/{id}")
    public ResponseEntity<ProduktEntity> deleteProdukt(@PathVariable Integer id) {
        try {
            produktService.deleteProdukt(id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.noContent().headers(headers).build();
        }
    }

    @PutMapping("/produkte/{id}")
    public ResponseEntity<ProduktEntity> updateProdukt(@PathVariable Integer id, @RequestBody ProduktDAO produkt) {
        try {
            return ResponseEntity.ok(produktService.updateProdukt(id, produkt));
        } catch (NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.noContent().headers(headers).build();
        }
    }

    @PutMapping("/produkt/{id}/addkategorie")
    public ResponseEntity<ProduktEntity> addKategorie(@PathVariable Integer id, @RequestBody List<Integer> kategorieID) {
        try {
            return ResponseEntity.ok(produktService.addKategorie(id, kategorieID));
        } catch (NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.noContent().headers(headers).build();
        }
    }

    @PutMapping("/produkt/{id}/removekategorie")
    public ResponseEntity<ProduktEntity> removeKategorie(@PathVariable Integer id, @RequestBody List<Integer> kategorieID) {
        try {
            return ResponseEntity.ok(produktService.removeKategorie(id, kategorieID));
        } catch (NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.noContent().headers(headers).build();
        }
    }
}
