package com.esspbackend.controller;

import com.esspbackend.entity.*;
import com.esspbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class AlertController {

    @Autowired
    private AlertRepository alertRepository;
    
    @Autowired
    private WorkRepository workRepository;
    
    @Autowired
    private SchoolInventoryRepository inventoryRepository;
    
    @Autowired
    private MaterialRepository materialRepository;
    
    @Autowired
    private BlockerRepository blockerRepository;
    
    @Autowired
    private WorkRequestRepository workRequestRepository;

    @GetMapping
    public ResponseEntity<List<Alert>> getAlerts(
            @RequestParam Long userId,
            @RequestParam Role role,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long talukaId) {
        
        // Before returning, run periodic check logic (this could be a scheduled task in a real app)
        generateDynamicAlerts(userId, role, schoolId, talukaId);
        
        List<Alert> alerts = alertRepository.findActiveAlerts(userId, role, schoolId, talukaId, LocalDateTime.now());
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(
            @RequestParam Long userId,
            @RequestParam Role role,
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long talukaId) {
        long count = alertRepository.countUnreadAlerts(userId, role, schoolId, talukaId);
        return ResponseEntity.ok(count);
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id) {
        alertRepository.updateStatus(id, "READ");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveAlert(@PathVariable Long id) {
        alertRepository.updateStatus(id, "RESOLVED");
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/snooze")
    public ResponseEntity<?> snoozeAlert(@PathVariable Long id, @RequestParam int days) {
        alertRepository.snoozeAlert(id, LocalDateTime.now().plusDays(days));
        return ResponseEntity.ok().build();
    }

    // Dynamic Alert Generation Logic
    private void generateDynamicAlerts(Long userId, Role role, Long schoolId, Long talukaId) {
        // 1. Inactivity Check (15 days no update)
        checkInactivity(schoolId, talukaId);
        
        // 2. Low Inventory Check
        if (schoolId != null) {
            checkLowInventory(schoolId);
        }
        
        // 3. Over Budget Check
        checkOverBudget(schoolId, talukaId);
    }

    private void checkInactivity(Long schoolId, Long talukaId) {
        LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);
        List<Work> inactiveWorks;
        
        if (schoolId != null) {
            inactiveWorks = workRepository.findWorksWithNoRecentUpdate(fifteenDaysAgo);
            inactiveWorks.removeIf(w -> !w.getSchoolId().equals(schoolId));
        } else {
            // This logic would need taluka-aware work repository method
            inactiveWorks = workRepository.findWorksWithNoRecentUpdate(fifteenDaysAgo);
        }

        for (Work work : inactiveWorks) {
            if (!alertRepository.existsByCategoryAndRelatedIdAndStatusNot("NO_UPDATE", work.getId(), "RESOLVED")) {
                Alert alert = new Alert();
                alert.setTitle("Progress Update Required");
                alert.setMessage("Work #" + work.getWorkCode() + " has not been updated for 15+ days.");
                alert.setType("WARNING");
                alert.setCategory("NO_UPDATE");
                alert.setRole(Role.HEADMASTER);
                alert.setSchoolId(work.getSchoolId());
                alert.setRelatedId(work.getId());
                alertRepository.save(alert);
            }
        }
    }

    private void checkLowInventory(Long schoolId) {
        List<SchoolInventory> lowStock = inventoryRepository.findLowStockBySchool(schoolId);
        for (SchoolInventory inv : lowStock) {
            if (!alertRepository.existsByCategoryAndRelatedIdAndStatusNot("LOW_INVENTORY", inv.getId(), "RESOLVED")) {
                materialRepository.findById(inv.getMaterialId()).ifPresent(m -> {
                    Alert alert = new Alert();
                    alert.setTitle("Low Stock: " + m.getName());
                    alert.setMessage(m.getName() + " is below reorder level. Current: " + inv.getCurrentQuantity());
                    alert.setType("CRITICAL");
                    alert.setCategory("LOW_INVENTORY");
                    alert.setRole(Role.CLERK);
                    alert.setSchoolId(schoolId);
                    alert.setRelatedId(inv.getId());
                    alertRepository.save(alert);
                });
            }
        }
    }

    private void checkOverBudget(Long schoolId, Long talukaId) {
        // Find works where utilized > 90% of sanctioned
        List<Work> allWorks = workRepository.findAll();
        for (Work work : allWorks) {
            if (work.getTotalUtilized() > (work.getSanctionedAmount() * 0.9)) {
                if (!alertRepository.existsByCategoryAndRelatedIdAndStatusNot("OVER_BUDGET", work.getId(), "RESOLVED")) {
                    Alert alert = new Alert();
                    alert.setTitle("Budget Threshold Warning");
                    alert.setMessage("Work #" + work.getWorkCode() + " has utilized over 90% of its sanctioned budget.");
                    alert.setType("CRITICAL");
                    alert.setCategory("OVER_BUDGET");
                    alert.setRole(Role.ADMIN);
                    alert.setRelatedId(work.getId());
                    alertRepository.save(alert);
                }
            }
        }
    }
}
