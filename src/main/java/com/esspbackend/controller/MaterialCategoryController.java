package com.esspbackend.controller;

import com.esspbackend.entity.MaterialCategory;
import com.esspbackend.repository.MaterialCategoryRepository;
import com.esspbackend.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/material-categories")
@CrossOrigin(origins = "http://localhost:3000")
public class MaterialCategoryController {

    private final MaterialCategoryRepository materialCategoryRepository;
    private final AuditLogService auditLogService;

    public MaterialCategoryController(MaterialCategoryRepository materialCategoryRepository, AuditLogService auditLogService) {
        this.materialCategoryRepository = materialCategoryRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<MaterialCategory> getAllCategories() {
        return materialCategoryRepository.findAll();
    }

    @PostMapping
    public MaterialCategory createCategory(@RequestBody MaterialCategory category) {
        MaterialCategory saved = materialCategoryRepository.save(category);
        auditLogService.log("CATEGORY_CREATE", "New material category created: " + saved.getName(), null, "Admin", "ADMIN");
        return saved;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        return materialCategoryRepository.findById(id)
                .map(category -> {
                    String name = category.getName();
                    materialCategoryRepository.delete(category);
                    auditLogService.log("CATEGORY_DELETE", "Material category deleted: " + name, null, "Admin", "ADMIN");
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
