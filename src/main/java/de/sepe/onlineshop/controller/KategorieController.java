package de.sepe.onlineshop.controller;

import de.sepe.onlineshop.dao.KategorieDAO;
import de.sepe.onlineshop.entity.KategorieEntity;
import de.sepe.onlineshop.service.KategorieService;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @DeleteMapping("/kategorie/{id}")
    public ResponseEntity<Void> deleteKategorie(@PathVariable Integer id) {
        try {
            kategorieService.deleteKategorie(id);
            return ResponseEntity.ok().build();
        } catch (NotFoundException e) {
            HttpHeaders headers = new HttpHeaders();
            headers.add("error", e.getMessage());
            return ResponseEntity.noContent().headers(headers).build();
        }
    }
}
