package com.esspbackend.controller;

import com.esspbackend.entity.MaterialCategory;
import com.esspbackend.repository.MaterialCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/material-categories")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class MaterialCategoryController {

    @Autowired
    private MaterialCategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<MaterialCategory>> getAllCategories() {
        List<MaterialCategory> categories = categoryRepository.findByIsActiveTrue();
        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody MaterialCategory category) {
        try {
            if (categoryRepository.existsByName(category.getName())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Category name already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
            }
            MaterialCategory saved = categoryRepository.save(category);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create category: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
}