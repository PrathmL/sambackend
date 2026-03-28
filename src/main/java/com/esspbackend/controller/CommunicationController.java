package com.esspbackend.controller;

import com.esspbackend.entity.Communication;
import com.esspbackend.service.CommunicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/communications")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class CommunicationController {

    private final CommunicationService communicationService;

    public CommunicationController(CommunicationService communicationService) {
        this.communicationService = communicationService;
    }

    @PostMapping
    public ResponseEntity<Communication> sendMessage(@RequestBody Communication communication) {
        return ResponseEntity.ok(communicationService.sendMessage(communication));
    }

    @GetMapping("/sent/{senderId}")
    public ResponseEntity<List<Communication>> getSentMessages(@PathVariable Long senderId) {
        return ResponseEntity.ok(communicationService.getSentMessages(senderId));
    }

    @GetMapping("/received")
    public ResponseEntity<List<Communication>> getReceivedMessages(
            @RequestParam Long schoolId,
            @RequestParam Long talukaId) {
        return ResponseEntity.ok(communicationService.getSchoolMessages(schoolId, talukaId));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        communicationService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }
}
