package com.esspbackend.controller;

import com.esspbackend.dto.BlockerCommentDTO;
import com.esspbackend.dto.BlockerDTO;
import com.esspbackend.dto.BlockerStatsDTO;
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
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
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
    
    @Autowired
    private TalukaRepository talukaRepository;

    @Autowired
    private AlertRepository alertRepository;

    // ==================== CREATE ====================
    
    @PostMapping
    public ResponseEntity<?> createBlocker(@RequestBody Blocker blocker) {
        try {
            blocker.setCreatedAt(LocalDateTime.now());
            blocker.setUpdatedAt(LocalDateTime.now());
            blocker.setStatus("NEW");
            Blocker savedBlocker = blockerRepository.save(blocker);
            
            // Create Alert for Sachiv
            School school = schoolRepository.findById(savedBlocker.getSchoolId()).orElse(null);
            if (school != null) {
                Alert alert = new Alert();
                alert.setTitle("New Blocker Reported: " + savedBlocker.getTitle());
                alert.setMessage("A new blocker has been reported for school " + school.getName() + ".");
                alert.setType("CRITICAL");
                alert.setCategory("BLOCKER");
                alert.setRole(Role.SACHIV);
                alert.setTalukaId(school.getTalukaId());
                alert.setRelatedId(savedBlocker.getId());
                alertRepository.save(alert);
            }
            
            // Add initial comment
            BlockerComment comment = new BlockerComment();
            comment.setBlockerId(savedBlocker.getId());
            comment.setComment("Blocker reported: " + blocker.getDescription());
            comment.setCommentedById(blocker.getReportedById());
            comment.setCommentedByRole(blocker.getReportedByRole());
            comment.setCommentedByName(getUserName(blocker.getReportedById()));
            comment.setIsInternal(false);
            blockerCommentRepository.save(comment);
            
            return ResponseEntity.ok(convertToDTO(savedBlocker));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> error = new HashMap<>();
            error.put("error", "Failed to create blocker: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // ==================== GET ====================
    
    @GetMapping
    public ResponseEntity<List<BlockerDTO>> getBlockers(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long talukaId,
            @RequestParam(required = false) Long workId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Long assignedToId) {
        
        List<Blocker> blockers = new ArrayList<>();
        
        if (schoolId != null) {
            blockers = blockerRepository.findBySchoolIdOrderByCreatedAtDesc(schoolId);
        } else if (talukaId != null) {
            blockers = blockerRepository.findByTalukaId(talukaId);
        } else if (workId != null) {
            blockers = blockerRepository.findByWorkIdOrderByCreatedAtDesc(workId);
        } else if (status != null) {
            blockers = blockerRepository.findByStatus(status);
            blockers.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        } else if (type != null) {
            blockers = blockerRepository.findAll();
            blockers = blockers.stream()
                    .filter(b -> type.equals(b.getType()))
                    .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                    .collect(Collectors.toList());
        } else if (assignedToId != null) {
            blockers = blockerRepository.findAssignedBlockers(assignedToId);
        } else {
            blockers = blockerRepository.findAll();
            blockers.sort((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()));
        }
        
        List<BlockerDTO> dtos = blockers.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(dtos);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getBlockerById(@PathVariable Long id) {
        return blockerRepository.findById(id)
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/stats")
    public ResponseEntity<BlockerStatsDTO> getBlockerStats(
            @RequestParam(required = false) Long schoolId,
            @RequestParam(required = false) Long talukaId) {
        
        BlockerStatsDTO stats = new BlockerStatsDTO();
        
        // Get all blockers based on filter
        List<Blocker> blockers;
        if (schoolId != null) {
            blockers = blockerRepository.findBySchoolId(schoolId);
        } else if (talukaId != null) {
            // Get schools in taluka and fetch blockers
            List<School> schools = schoolRepository.findByTalukaId(talukaId);
            List<Long> schoolIds = schools.stream().map(School::getId).collect(Collectors.toList());
            blockers = new ArrayList<>();
            for (Long sid : schoolIds) {
                blockers.addAll(blockerRepository.findBySchoolId(sid));
            }
        } else {
            blockers = blockerRepository.findAll();
        }
        
        // Calculate stats
        stats.setTotalBlockers(blockers.size());
        stats.setNewBlockers(blockers.stream().filter(b -> "NEW".equals(b.getStatus())).count());
        stats.setInProgressBlockers(blockers.stream().filter(b -> "IN_PROGRESS".equals(b.getStatus())).count());
        stats.setResolvedBlockers(blockers.stream().filter(b -> "RESOLVED".equals(b.getStatus())).count());
        stats.setEscalatedBlockers(blockers.stream().filter(b -> "ESCALATED".equals(b.getStatus())).count());
        stats.setHighPriorityBlockers(blockers.stream().filter(b -> "HIGH".equals(b.getPriority())).count());
        
        // Blockers by type
        Map<String, Long> byType = blockers.stream()
                .collect(Collectors.groupingBy(Blocker::getType, Collectors.counting()));
        stats.setBlockersByType(byType);
        
        // Blockers by priority (active only)
        Map<String, Long> byPriority = blockers.stream()
                .filter(b -> !"RESOLVED".equals(b.getStatus()))
                .collect(Collectors.groupingBy(Blocker::getPriority, Collectors.counting()));
        stats.setBlockersByPriority(byPriority);
        
        // Blockers by status
        Map<String, Long> byStatus = blockers.stream()
                .collect(Collectors.groupingBy(Blocker::getStatus, Collectors.counting()));
        stats.setBlockersByStatus(byStatus);
        
        // Average resolution time (last 30 days) - using MySQL compatible query
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Double avgTime = blockerRepository.getAverageResolutionTimeDays(thirtyDaysAgo);
        stats.setAverageResolutionTimeDays(avgTime != null ? avgTime : 0);
        
        // Blockers by taluka (if taluka filter not applied)
        if (talukaId == null && schoolId == null) {
            Map<String, Long> byTaluka = new HashMap<>();
            List<Taluka> talukas = talukaRepository.findAll();
            for (Taluka taluka : talukas) {
                List<School> schoolsInTaluka = schoolRepository.findByTalukaId(taluka.getId());
                long blockerCount = 0;
                for (School school : schoolsInTaluka) {
                    blockerCount += blockerRepository.countBySchoolIdAndStatus(school.getId(), "NEW");
                    blockerCount += blockerRepository.countBySchoolIdAndStatus(school.getId(), "IN_PROGRESS");
                    blockerCount += blockerRepository.countBySchoolIdAndStatus(school.getId(), "ESCALATED");
                }
                byTaluka.put(taluka.getName(), blockerCount);
            }
            stats.setBlockersByTaluka(byTaluka);
        }
        
        return ResponseEntity.ok(stats);
    }

    // ==================== UPDATE ====================
    
    @PutMapping("/{id}/assign")
    public ResponseEntity<?> assignBlocker(
            @PathVariable Long id,
            @RequestBody Map<String, Object> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    try {
                        Object assignedToIdObj = body.get("assignedToId");
                        if (assignedToIdObj == null || assignedToIdObj.toString().isEmpty()) {
                            Map<String, String> error = new HashMap<>();
                            error.put("error", "Assigned user is required");
                            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
                        }
                        
                        Long assignedToId = Long.valueOf(assignedToIdObj.toString());
                        String assignedToRole = (String) body.getOrDefault("assignedToRole", "SACHIV");
                        String status = "IN_PROGRESS";
                        
                        LocalDateTime targetDate = null;
                        Object targetDateObj = body.get("targetDate");
                        if (targetDateObj != null && !targetDateObj.toString().isEmpty()) {
                            String dateStr = targetDateObj.toString();
                            try {
                                if (dateStr.length() == 10) { // YYYY-MM-DD
                                    targetDate = java.time.LocalDate.parse(dateStr).atStartOfDay();
                                } else {
                                    targetDate = LocalDateTime.parse(dateStr);
                                }
                            } catch (Exception e) {
                                System.err.println("Failed to parse target date: " + dateStr);
                            }
                        }
                        
                        blockerRepository.assignBlocker(id, status, assignedToId, assignedToRole, targetDate);
                        
                        // Add comment
                        BlockerComment comment = new BlockerComment();
                        comment.setBlockerId(id);
                        String commentText = "Assigned to " + assignedToRole + " for resolution.";
                        if (targetDate != null) {
                            commentText += " Target Date: " + targetDate.toLocalDate();
                        }
                        if (body.get("notes") != null && !body.get("notes").toString().isEmpty()) {
                            commentText += " Notes: " + body.get("notes").toString();
                        }
                        comment.setComment(commentText);
                        
                        Object assignedByObj = body.get("assignedBy");
                        if (assignedByObj != null) {
                            Long assignedById = Long.valueOf(assignedByObj.toString());
                            comment.setCommentedById(assignedById);
                            comment.setCommentedByRole((String) body.get("assignedByRole"));
                            comment.setCommentedByName(getUserName(assignedById));
                        }
                        
                        comment.setIsInternal(false);
                        blockerCommentRepository.save(comment);
                        
                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Blocker assigned successfully");
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Internal server error: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/resolve")
    public ResponseEntity<?> resolveBlocker(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    String resolutionNotes = body.get("resolutionNotes");
                    blockerRepository.resolveBlocker(id, resolutionNotes, LocalDateTime.now());
                    
                    // Add comment
                    BlockerComment comment = new BlockerComment();
                    comment.setBlockerId(id);
                    comment.setComment("Blocker resolved: " + resolutionNotes);
                    comment.setCommentedById(Long.valueOf(body.get("resolvedById")));
                    comment.setCommentedByRole(body.get("resolvedByRole"));
                    comment.setCommentedByName(getUserName(Long.valueOf(body.get("resolvedById"))));
                    comment.setIsInternal(false);
                    blockerCommentRepository.save(comment);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Blocker resolved successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/escalate")
    public ResponseEntity<?> escalateBlocker(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    Long escalatedToId = Long.valueOf(body.get("escalatedToId"));
                    String escalatedToRole = body.get("escalatedToRole");
                    String reason = body.get("reason");
                    
                    blockerRepository.escalateBlocker(id, LocalDateTime.now(), escalatedToId, escalatedToRole, reason);
                    
                    // Add comment
                    BlockerComment comment = new BlockerComment();
                    comment.setBlockerId(id);
                    comment.setComment("Escalated to " + escalatedToRole + ". Reason: " + reason);
                    comment.setCommentedById(Long.valueOf(body.get("escalatedById")));
                    comment.setCommentedByRole(body.get("escalatedByRole"));
                    comment.setCommentedByName(getUserName(Long.valueOf(body.get("escalatedById"))));
                    comment.setIsInternal(false);
                    blockerCommentRepository.save(comment);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Blocker escalated successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/priority")
    public ResponseEntity<?> updatePriority(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    String priority = body.get("priority");
                    blockerRepository.updatePriority(id, priority);
                    
                    // Add comment
                    BlockerComment comment = new BlockerComment();
                    comment.setBlockerId(id);
                    comment.setComment("Priority changed to " + priority);
                    comment.setCommentedById(Long.valueOf(body.get("updatedById")));
                    comment.setCommentedByRole(body.get("updatedByRole"));
                    comment.setCommentedByName(getUserName(Long.valueOf(body.get("updatedById"))));
                    comment.setIsInternal(false);
                    blockerCommentRepository.save(comment);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Priority updated successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/duplicate")
    public ResponseEntity<?> markDuplicate(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    Long duplicateOfId = Long.valueOf(body.get("duplicateOfId"));
                    blockerRepository.markDuplicate(id, duplicateOfId);
                    
                    // Add comment
                    BlockerComment comment = new BlockerComment();
                    comment.setBlockerId(id);
                    comment.setComment("Marked as duplicate of Blocker #" + duplicateOfId);
                    comment.setCommentedById(Long.valueOf(body.get("updatedById")));
                    comment.setCommentedByRole(body.get("updatedByRole"));
                    comment.setCommentedByName(getUserName(Long.valueOf(body.get("updatedById"))));
                    comment.setIsInternal(false);
                    blockerCommentRepository.save(comment);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Blocker marked as duplicate");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/{id}/request-info")
    public ResponseEntity<?> requestInfo(
            @PathVariable Long id,
            @RequestBody Map<String, String> body) {
        
        return blockerRepository.findById(id)
                .map(blocker -> {
                    blocker.setStatus("INFO_REQUESTED");
                    blockerRepository.save(blocker);
                    
                    // Add comment
                    BlockerComment comment = new BlockerComment();
                    comment.setBlockerId(id);
                    comment.setComment("Additional information requested: " + body.get("notes"));
                    comment.setCommentedById(Long.valueOf(body.get("requestedById")));
                    comment.setCommentedByRole(body.get("requestedByRole"));
                    comment.setCommentedByName(getUserName(Long.valueOf(body.get("requestedById"))));
                    comment.setIsInternal(false);
                    blockerCommentRepository.save(comment);
                    
                    Map<String, String> response = new HashMap<>();
                    response.put("message", "Information requested successfully");
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

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
                    comment.setCommentedByName((String) body.get("commentedByName"));
                    comment.setIsInternal((Boolean) body.getOrDefault("isInternal", false));
                    
                    BlockerComment savedComment = blockerCommentRepository.save(comment);
                    
                    BlockerCommentDTO dto = new BlockerCommentDTO();
                    dto.setId(savedComment.getId());
                    dto.setComment(savedComment.getComment());
                    dto.setCommentedBy(savedComment.getCommentedByName());
                    dto.setCommentedByRole(savedComment.getCommentedByRole());
                    dto.setIsInternal(savedComment.getIsInternal());
                    dto.setCreatedAt(savedComment.getCreatedAt());
                    
                    return ResponseEntity.ok(dto);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ==================== HELPER METHODS ====================
    
    private String getUserName(Long userId) {
        return userRepository.findById(userId)
                .map(User::getName)
                .orElse("Unknown User");
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
        dto.setSchoolId(blocker.getSchoolId());
        dto.setReportedByRole(blocker.getReportedByRole());
        dto.setReportedAt(blocker.getCreatedAt());
        dto.setAssignedToId(blocker.getAssignedToId());
        dto.setAssignedToRole(blocker.getAssignedToRole());
        dto.setResolutionNotes(blocker.getResolutionNotes());
        dto.setResolvedAt(blocker.getResolvedAt());
        dto.setEscalatedAt(blocker.getEscalatedAt());
        dto.setEscalationReason(blocker.getEscalationReason());
        dto.setDuplicateOfId(blocker.getDuplicateOfId());
        dto.setTargetDate(blocker.getTargetDate());
        dto.setCreatedAt(blocker.getCreatedAt());
        dto.setUpdatedAt(blocker.getUpdatedAt());
        
        // Get duplicate title
        if (blocker.getDuplicateOfId() != null) {
            blockerRepository.findById(blocker.getDuplicateOfId()).ifPresent(dup -> {
                dto.setDuplicateOfTitle(dup.getTitle());
            });
        }
        
        // Get work details
        workRepository.findById(blocker.getWorkId()).ifPresent(work -> {
            dto.setWorkTitle(work.getTitle());
            dto.setWorkCode(work.getWorkCode());
        });
        
        // Get school name
        schoolRepository.findById(blocker.getSchoolId()).ifPresent(school -> {
            dto.setSchoolName(school.getName());
        });
        
        // Get reporter name
        userRepository.findById(blocker.getReportedById()).ifPresent(user -> {
            dto.setReportedBy(user.getName());
        });
        
        // Get assigned to name
        if (blocker.getAssignedToId() != null) {
            userRepository.findById(blocker.getAssignedToId()).ifPresent(user -> {
                dto.setAssignedTo(user.getName());
            });
        }
        
        // Get escalated to name
        if (blocker.getEscalatedToId() != null) {
            userRepository.findById(blocker.getEscalatedToId()).ifPresent(user -> {
                dto.setEscalatedTo(user.getName());
            });
        }
        
        // Get comments (only public comments for non-internal users)
        List<BlockerComment> comments = blockerCommentRepository.findByBlockerIdOrderByCreatedAtAsc(blocker.getId());
        List<BlockerCommentDTO> commentDTOs = comments.stream().map(comment -> {
            BlockerCommentDTO commentDTO = new BlockerCommentDTO();
            commentDTO.setId(comment.getId());
            commentDTO.setComment(comment.getComment());
            commentDTO.setCommentedBy(comment.getCommentedByName());
            commentDTO.setCommentedByRole(comment.getCommentedByRole());
            commentDTO.setIsInternal(comment.getIsInternal());
            commentDTO.setCreatedAt(comment.getCreatedAt());
            return commentDTO;
        }).collect(Collectors.toList());
        dto.setComments(commentDTOs);
        
        return dto;
    }
}