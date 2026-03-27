package com.esspbackend.controller;

import com.esspbackend.entity.Taluka;
import com.esspbackend.repository.TalukaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/talukas")
@CrossOrigin(origins = "http://localhost:3000")
public class TalukaController {

    @Autowired
    private TalukaRepository talukaRepository;

    @GetMapping
    public List<Taluka> getAllTalukas() {
        return talukaRepository.findAll();
    }

    @PostMapping
    public Taluka createTaluka(@RequestBody Taluka taluka) {
        return talukaRepository.save(taluka);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Taluka> getTalukaById(@PathVariable Long id) {
        return talukaRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Taluka> updateTaluka(@PathVariable Long id, @RequestBody Taluka talukaDetails) {
        return talukaRepository.findById(id)
                .map(taluka -> {
                    taluka.setName(talukaDetails.getName());
                    taluka.setDistrict(talukaDetails.getDistrict());
                    taluka.setCode(talukaDetails.getCode());
                    taluka.setStatus(talukaDetails.getStatus());
                    return ResponseEntity.ok(talukaRepository.save(taluka));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaluka(@PathVariable Long id) {
        return talukaRepository.findById(id)
                .map(taluka -> {
                    talukaRepository.delete(taluka);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
