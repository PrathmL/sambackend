package com.esspbackend.controller;

import com.esspbackend.dto.InventoryStatsDTO;
import com.esspbackend.dto.MaterialDTO;
import com.esspbackend.dto.StockMovementDTO;
import com.esspbackend.entity.*;
import com.esspbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class InventoryController {

    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private MaterialCategoryRepository categoryRepository;
    
    @Autowired
    private SchoolInventoryRepository schoolInventoryRepository;
    
    @Autowired
    private StockMovementRepository stockMovementRepository;
    
    @Autowired
    private WorkRepository workRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private QuotationRepository quotationRepository;

    // Get all materials
    @GetMapping("/materials")
    public ResponseEntity<List<MaterialDTO>> getAllMaterials() {
        List<Material> materials = materialRepository.findByIsActiveTrue();
        List<MaterialDTO> dtos = materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    // Get materials by category
    @GetMapping("/materials/category/{categoryId}")
    public ResponseEntity<List<MaterialDTO>> getMaterialsByCategory(@PathVariable Long categoryId) {
        List<Material> materials = materialRepository.findByCategoryId(categoryId);
        List<MaterialDTO> dtos = materials.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    // Create new material
    @PostMapping("/materials")
    public ResponseEntity<?> createMaterial(@RequestBody Material material) {
        try {
            material.setIsActive(true);
            Material saved = materialRepository.save(material);
            return ResponseEntity.ok(convertToDTO(saved));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create material: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Update material
    @PutMapping("/materials/{id}")
    public ResponseEntity<?> updateMaterial(@PathVariable Long id, @RequestBody Material materialDetails) {
        return materialRepository.findById(id)
                .map(material -> {
                    material.setName(materialDetails.getName());
                    material.setCategoryId(materialDetails.getCategoryId());
                    material.setDescription(materialDetails.getDescription());
                    material.setUnitOfMeasurement(materialDetails.getUnitOfMeasurement());
                    material.setMinStockLevel(materialDetails.getMinStockLevel());
                    material.setUnitPrice(materialDetails.getUnitPrice());
                    material.setStorageLocation(materialDetails.getStorageLocation());
                    material.setPhotoUrl(materialDetails.getPhotoUrl());
                    Material updated = materialRepository.save(material);
                    return ResponseEntity.ok(convertToDTO(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Delete material (soft delete)
    @DeleteMapping("/materials/{id}")
    public ResponseEntity<?> deleteMaterial(@PathVariable Long id) {
        return materialRepository.findById(id)
                .map(material -> {
                    material.setIsActive(false);
                    materialRepository.save(material);
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Material deleted successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Get school inventory
    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<MaterialDTO>> getSchoolInventory(@PathVariable Long schoolId) {
        List<SchoolInventory> inventory = schoolInventoryRepository.findBySchoolId(schoolId);
        List<MaterialDTO> dtos = new ArrayList<>();
        
        for (SchoolInventory inv : inventory) {
            materialRepository.findById(inv.getMaterialId()).ifPresent(material -> {
                MaterialDTO dto = convertToDTO(material);
                dto.setCurrentStock(inv.getCurrentQuantity());
                dtos.add(dto);
            });
        }
        
        return ResponseEntity.ok(dtos);
    }
    
    // Update stock levels
    @PostMapping("/stock/update")
    public ResponseEntity<?> updateStock(@RequestBody Map<String, Object> request) {
        try {
            Long schoolId = Long.valueOf(request.get("schoolId").toString());
            Long materialId = Long.valueOf(request.get("materialId").toString());
            String movementType = (String) request.get("movementType");
            Double quantity = Double.valueOf(request.get("quantity").toString());
            Double unitPrice = request.containsKey("unitPrice") ? Double.valueOf(request.get("unitPrice").toString()) : null;
            Long workId = request.containsKey("workId") ? Long.valueOf(request.get("workId").toString()) : null;
            String purpose = (String) request.get("purpose");
            String remarks = (String) request.get("remarks");
            Long performedById = Long.valueOf(request.get("performedById").toString());
            String performedByRole = (String) request.get("performedByRole");
            
            // Get or create school inventory
            SchoolInventory inventory = schoolInventoryRepository
                    .findBySchoolIdAndMaterialId(schoolId, materialId)
                    .orElse(new SchoolInventory());
            
            if (inventory.getId() == null) {
                inventory.setSchoolId(schoolId);
                inventory.setMaterialId(materialId);
                inventory.setCurrentQuantity(0.0);
                inventory.setReorderLevel(10.0);
            }
            
            // Update quantity based on movement type
            if ("IN".equals(movementType)) {
                inventory.setCurrentQuantity(inventory.getCurrentQuantity() + quantity);
            } else if ("OUT".equals(movementType)) {
                if (inventory.getCurrentQuantity() < quantity) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Insufficient stock");
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                }
                inventory.setCurrentQuantity(inventory.getCurrentQuantity() - quantity);
            }
            
            schoolInventoryRepository.save(inventory);
            
            // Record stock movement
            StockMovement movement = new StockMovement();
            movement.setSchoolId(schoolId);
            movement.setMaterialId(materialId);
            movement.setMovementType(movementType);
            movement.setQuantity(quantity);
            movement.setUnitPrice(unitPrice);
            movement.setWorkId(workId);
            movement.setPurpose(purpose);
            movement.setRemarks(remarks);
            movement.setPerformedById(performedById);
            movement.setPerformedByRole(performedByRole);
            stockMovementRepository.save(movement);
            
            Map<String, String> response = new HashMap<>();
            response.put("message", "Stock updated successfully");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to update stock: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
    // Get stock movement history
    @GetMapping("/stock/movements/{schoolId}")
    public ResponseEntity<List<StockMovementDTO>> getStockMovements(@PathVariable Long schoolId) {
        List<StockMovement> movements = stockMovementRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);
        List<StockMovementDTO> dtos = movements.stream()
                .map(this::convertMovementToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }
    
    // Get low stock alerts
    @GetMapping("/alerts/low-stock/{schoolId}")
    public ResponseEntity<List<MaterialDTO>> getLowStockAlerts(@PathVariable Long schoolId) {
        List<SchoolInventory> lowStock = schoolInventoryRepository.findLowStockBySchool(schoolId);
        List<MaterialDTO> dtos = new ArrayList<>();
        
        for (SchoolInventory inv : lowStock) {
            materialRepository.findById(inv.getMaterialId()).ifPresent(material -> {
                MaterialDTO dto = convertToDTO(material);
                dto.setCurrentStock(inv.getCurrentQuantity());
                dtos.add(dto);
            });
        }
        
        return ResponseEntity.ok(dtos);
    }
    
    // Get inventory stats
    @GetMapping("/stats/{schoolId}")
    public ResponseEntity<InventoryStatsDTO> getInventoryStats(@PathVariable Long schoolId) {
        InventoryStatsDTO stats = new InventoryStatsDTO();
        
        List<SchoolInventory> inventory = schoolInventoryRepository.findBySchoolId(schoolId);
        stats.setTotalMaterials(inventory.size());
        stats.setLowStockItems(inventory.stream().filter(i -> i.getCurrentQuantity() <= i.getReorderLevel()).count());
        stats.setOutOfStockItems(inventory.stream().filter(i -> i.getCurrentQuantity() <= 0).count());
        
        double totalValue = 0;
        for (SchoolInventory inv : inventory) {
            Optional<Material> material = materialRepository.findById(inv.getMaterialId());
            if (material.isPresent() && material.get().getUnitPrice() != null) {
                totalValue += inv.getCurrentQuantity() * material.get().getUnitPrice();
            }
        }
        stats.setTotalInventoryValue(totalValue);
        
        // Get monthly consumption
        YearMonth currentMonth = YearMonth.now();
        LocalDateTime startOfMonth = currentMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfMonth = currentMonth.atEndOfMonth().atTime(23, 59, 59);
        
        List<StockMovement> monthlyMovements = stockMovementRepository.findBySchoolIdAndDateRange(schoolId, startOfMonth, endOfMonth);
        double monthlyConsumption = monthlyMovements.stream()
                .filter(m -> "OUT".equals(m.getMovementType()))
                .mapToDouble(StockMovement::getTotalCost)
                .sum();
        stats.setMonthlyConsumption(monthlyConsumption);
        
        // Get pending quotations count
        List<Quotation> pendingQuotations = quotationRepository.findPendingQuotationsBySchool(schoolId);
        stats.setPendingQuotations(pendingQuotations.size());
        
        return ResponseEntity.ok(stats);
    }
    
    private MaterialDTO convertToDTO(Material material) {
        MaterialDTO dto = new MaterialDTO();
        dto.setId(material.getId());
        dto.setName(material.getName());
        dto.setCategoryId(material.getCategoryId());
        dto.setDescription(material.getDescription());
        dto.setUnitOfMeasurement(material.getUnitOfMeasurement());
        dto.setMinStockLevel(material.getMinStockLevel());
        dto.setCurrentStock(material.getCurrentStock());
        dto.setUnitPrice(material.getUnitPrice());
        dto.setTotalValue(material.getTotalValue());
        dto.setStockStatus(material.getStockStatus());
        dto.setStorageLocation(material.getStorageLocation());
        dto.setPhotoUrl(material.getPhotoUrl());
        dto.setIsActive(material.getIsActive());
        
        categoryRepository.findById(material.getCategoryId())
                .ifPresent(cat -> dto.setCategoryName(cat.getName()));
        
        return dto;
    }
    
    private StockMovementDTO convertMovementToDTO(StockMovement movement) {
        StockMovementDTO dto = new StockMovementDTO();
        dto.setId(movement.getId());
        dto.setMaterialId(movement.getMaterialId());
        dto.setMovementType(movement.getMovementType());
        dto.setQuantity(movement.getQuantity());
        dto.setUnitPrice(movement.getUnitPrice());
        dto.setTotalCost(movement.getTotalCost());
        dto.setWorkId(movement.getWorkId());
        dto.setPurpose(movement.getPurpose());
        dto.setRemarks(movement.getRemarks());
        dto.setCreatedAt(movement.getCreatedAt());
        
        materialRepository.findById(movement.getMaterialId())
                .ifPresent(m -> dto.setMaterialName(m.getName()));
        
        if (movement.getWorkId() != null) {
            workRepository.findById(movement.getWorkId())
                    .ifPresent(w -> dto.setWorkTitle(w.getTitle()));
        }
        
        userRepository.findById(movement.getPerformedById())
                .ifPresent(u -> dto.setPerformedBy(u.getName()));
        
        return dto;
    }
}	