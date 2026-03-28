package com.esspbackend.controller;

import com.esspbackend.entity.School;
import com.esspbackend.repository.SchoolRepository;
import com.esspbackend.service.AuditLogService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schools")
@CrossOrigin(origins = "http://localhost:3000")
public class SchoolController {

    private final SchoolRepository schoolRepository;
    private final AuditLogService auditLogService;

    public SchoolController(SchoolRepository schoolRepository, AuditLogService auditLogService) {
        this.schoolRepository = schoolRepository;
        this.auditLogService = auditLogService;
    }

    @GetMapping
    public List<School> getSchools(@RequestParam(required = false) Long talukaId) {
        if (talukaId != null) {
            return schoolRepository.findByTalukaId(talukaId);
        }
        return schoolRepository.findAll();
    }

    @PostMapping
    public School createSchool(@RequestBody School school) {
        School saved = schoolRepository.save(school);
        auditLogService.log("SCHOOL_CREATE", "New school created: " + saved.getName(), null, "Admin", "ADMIN");
        return saved;
    }

    @GetMapping("/{id}")
    public ResponseEntity<School> getSchoolById(@PathVariable Long id) {
        return schoolRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<School> updateSchool(@PathVariable Long id, @RequestBody School schoolDetails) {
        return schoolRepository.findById(id)
                .map(school -> {
                    school.setName(schoolDetails.getName());
                    school.setCode(schoolDetails.getCode());
                    school.setTalukaId(schoolDetails.getTalukaId());
                    school.setAddress(schoolDetails.getAddress());
                    school.setContactDetails(schoolDetails.getContactDetails());
                    school.setEstablishedYear(schoolDetails.getEstablishedYear());
                    school.setStatus(schoolDetails.getStatus());
                    School updated = schoolRepository.save(school);
                    auditLogService.log("SCHOOL_UPDATE", "School updated: " + updated.getName(), null, "Admin", "ADMIN");
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchool(@PathVariable Long id) {
        return schoolRepository.findById(id)
                .map(school -> {
                    String name = school.getName();
                    schoolRepository.delete(school);
                    auditLogService.log("SCHOOL_DELETE", "School deleted: " + name, null, "Admin", "ADMIN");
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
