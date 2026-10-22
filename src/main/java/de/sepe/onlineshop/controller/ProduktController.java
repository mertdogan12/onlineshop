package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.entity.ProduktEntity;
import de.sepe.onlineshop.service.ProduktService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class ProduktController {

    @Autowired
    private ProduktService produktService;

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Hallo World!");
    }

    @GetMapping("/produkte")
    public ResponseEntity<List<ProduktEntity>> getAllProdukte() {
        return ResponseEntity.ok(produktService.getAllProdukte());
    }
}
