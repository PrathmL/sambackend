package com.esspbackend.controller;

import com.esspbackend.entity.Taluka;
import com.esspbackend.repository.TalukaRepository;
import com.esspbackend.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/talukas")
@CrossOrigin(origins = "http://localhost:3000")
public class TalukaController {

    private final TalukaRepository talukaRepository;
    private final AuditLogService auditLogService;

    public TalukaController(TalukaRepository talukaRepository, AuditLogService auditLogService) {
        this.talukaRepository = talukaRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<Taluka> getAllTalukas() {
        return talukaRepository.findAll();
    }

    @PostMapping
    public Taluka createTaluka(@RequestBody Taluka taluka) {
        Taluka saved = talukaRepository.save(taluka);
        auditLogService.log("TALUKA_CREATE", "New taluka created: " + saved.getName(), null, "Admin", "ADMIN");
        return saved;
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
                    Taluka updated = talukaRepository.save(taluka);
                    auditLogService.log("TALUKA_UPDATE", "Taluka updated: " + updated.getName(), null, "Admin", "ADMIN");
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaluka(@PathVariable Long id) {
        return talukaRepository.findById(id)
                .map(taluka -> {
                    String name = taluka.getName();
                    talukaRepository.delete(taluka);
                    auditLogService.log("TALUKA_DELETE", "Taluka deleted: " + name, null, "Admin", "ADMIN");
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
