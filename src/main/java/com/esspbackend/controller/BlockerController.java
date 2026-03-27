package com.esspbackend.controller;

import com.esspbackend.dto.BlockerCommentDTO;
import com.esspbackend.dto.BlockerDTO;
import com.esspbackend.entity.*;
import com.esspbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/blockers")
@CrossOrigin(origins = "http://localhost:3000")
public class BlockerController {

    @Autowired
    private BlockerRepository blockerRepository;
    
    @Autowired
    private BlockerCommentRepository blockerCommentRepository;
    
    @Autowired
    private WorkRepository workRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private SchoolRepository schoolRepository;

    // Create new blocker
    @PostMapping
    public ResponseEntity<?> createBlocker(@RequestBody Blocker blocker) {
        try {
            blocker.setCreatedAt(LocalDateTime.now());
            blocker.setUpdatedAt(LocalDateTime.now());
            blocker.setStatus("NEW");
            Blocker savedBlocker = blockerRepository.save(blocker);
            return ResponseEntity.ok(convertToDTO(savedBlocker));
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create blocker: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }
    
 // Get blockers by school or work
    @GetMapping
    public ResponseEntity<List<BlockerDTO>> getBlockers(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long workId) {
        
        List<Blocker> blockers;
        
        if (schoolId != null) {
            blockers = blockerRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);
        } else if (workId != null) {
            blockers = blockerRepository.findByWorkIdOrderByCreatedAtDesc(workId);
        } else {
            blockers = blockerRepository.findAll();
            blockers.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }
        
        List<BlockerDTO> dtos = blockers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    
    
    // Get blocker by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getBlockerById(@PathVariable Long id) {
        return blockerRepository.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Update blocker status
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateBlockerStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    String status = body.get("status");
                    blocker.setStatus(status);
                    
                    if ("RESOLVED".equals(status)) {
                        blocker.setResolvedAt(LocalDateTime.now());
                        blocker.setResolutionNotes(body.get("resolutionNotes"));
                    }
                    
                    blocker.setUpdatedAt(LocalDateTime.now());
                    blockerRepository.save(blocker);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Blocker status updated to " + status);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Add comment to blocker
    @PostMapping("/{id}/comments")
    public ResponseEntity<?> addComment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    BlockerComment comment = new BlockerComment();
                    comment.setBlockerId(id);
                    comment.setComment((String) body.get("comment"));
                    comment.setCommentedById(Long.valueOf(body.get("commentedById").toString()));
                    comment.setCommentedByRole((String) body.get("commentedByRole"));
                    comment.setIsInternal((Boolean) body.getOrDefault("isInternal", false));
                    
                    BlockerComment savedComment = blockerCommentRepository.save(comment);
                    
                    BlockerCommentDTO dto = new BlockerCommentDTO();
                    dto.setId(savedComment.getId());
                    dto.setComment(savedComment.getComment());
                    dto.setCommentedByRole(savedComment.getCommentedByRole());
                    dto.setIsInternal(savedComment.getIsInternal());
                    dto.setCreatedAt(savedComment.getCreatedAt());
                    
                    // Get user name
                    userRepository.findById(savedComment.getCommentedById())
                            .ifPresent(user -> dto.setCommentedBy(user.getName()));
                    
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    // Escalate blocker
    @PostMapping("/{id}/escalate")
    public ResponseEntity<?> escalateBlocker(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    blocker.setStatus("ESCALATED");
                    blocker.setEscalatedAt(LocalDateTime.now());
                    blocker.setEscalationReason(body.get("reason"));
                    blocker.setPriority("High");
                    blocker.setUpdatedAt(LocalDateTime.now());
                    blockerRepository.save(blocker);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Blocker escalated to Admin");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    private BlockerDTO convertToDTO(Blocker blocker) {
        BlockerDTO dto = new BlockerDTO();
        dto.setId(blocker.getId());
        dto.setWorkId(blocker.getWorkId());
        dto.setTitle(blocker.getTitle());
        dto.setType(blocker.getType());
        dto.setPriority(blocker.getPriority());
        dto.setDescription(blocker.getDescription());
        dto.setImpact(blocker.getImpact());
        dto.setEstimatedDelayDays(blocker.getEstimatedDelayDays());
        dto.setStatus(blocker.getStatus());
        dto.setReportedByRole(blocker.getReportedByRole());
        dto.setResolutionNotes(blocker.getResolutionNotes());
        dto.setResolvedAt(blocker.getResolvedAt());
        dto.setCreatedAt(blocker.getCreatedAt());
        
        // Get work title
        workRepository.findById(blocker.getWorkId())
                .ifPresent(work -> dto.setWorkTitle(work.getTitle()));
        
        // Get reporter name
        userRepository.findById(blocker.getReportedById())
                .ifPresent(user -> dto.setReportedBy(user.getName()));
        
        // Get comments
        List<BlockerComment> comments = blockerCommentRepository.findByBlockerIdOrderByCreatedAtAsc(blocker.getId());
        List<BlockerCommentDTO> commentDTOs = comments.stream().map(comment -> {
            BlockerCommentDTO commentDTO = new BlockerCommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setComment(comment.getComment());
            commentDTO.setCommentedByRole(comment.getCommentedByRole());
            commentDTO.setIsInternal(comment.getIsInternal());
            commentDTO.setCreatedAt(comment.getCreatedAt());
            
            userRepository.findById(comment.getCommentedById())
                    .ifPresent(user -> commentDTO.setCommentedBy(user.getName()));
            
            return commentDTO;
        }).collect(Collectors.toList());
        
        dto.setComments(commentDTOs);
        
        return dto;
    }
}