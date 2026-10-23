package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.dao.KategorieDAO;
import de.sepe.onlineshop.entity.KategorieEntity;
import de.sepe.onlineshop.service.KategorieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class KategorieController {

    @Autowired
    private KategorieService kategorieService;

    @GetMapping("/kategorie")
    public ResponseEntity<List<KategorieEntity>> getAllKategorien() {
        return ResponseEntity.ok(kategorieService.getAllKategorien());
    }

    @PostMapping("/kategorie")
    public ResponseEntity<KategorieEntity> createKategorie(@RequestBody KategorieDAO kategorie) {
        return ResponseEntity.ok(kategorieService.createKategorie(kategorie));
    }
}
