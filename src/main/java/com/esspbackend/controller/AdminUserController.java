package com.esspbackend.controller;

import com.esspbackend.dto.PasswordResetRequest;
import com.esspbackend.dto.UserDTO;
import com.esspbackend.entity.Role;
import com.esspbackend.entity.School;
import com.esspbackend.entity.Taluka;
import com.esspbackend.entity.User;
import com.esspbackend.repository.SchoolRepository;
import com.esspbackend.repository.TalukaRepository;
import com.esspbackend.repository.UserRepository;
import com.esspbackend.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/users")
@CrossOrigin(origins = "http://localhost:3000")
public class AdminUserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TalukaRepository talukaRepository;

    @Autowired
    private SchoolRepository schoolRepository;

    @Autowired
    private AuditLogService auditLogService;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<User> users = userRepository.findByIsDeletedFalse();
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<List<UserDTO>> getUsersByRole(@PathVariable Role role) {
        List<User> users = userRepository.findByRoleAndIsDeletedFalse(role);
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/taluka/{talukaId}")
    public ResponseEntity<List<UserDTO>> getUsersByTaluka(@PathVariable Long talukaId) {
        List<User> users = userRepository.findByTalukaIdAndIsDeletedFalse(talukaId);
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<UserDTO>> getUsersBySchool(@PathVariable Long schoolId) {
        List<User> users = userRepository.findBySchoolIdAndIsDeletedFalse(schoolId);
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody User user) {
        try {
            // Check if mobile number already exists
            if (userRepository.existsByMobileNumber(user.getMobileNumber())) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Mobile number already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
            }
            
            // Set default password if not provided
            if (user.getPassword() == null || user.getPassword().isEmpty()) {
                user.setPassword(user.getMobileNumber()); // Default password is mobile number
            }
            
            // Set isDeleted to false
            user.setIsDeleted(false);
            
            User savedUser = userRepository.save(user);

            // Log user creation
            auditLogService.log("USER_CREATION", "New user created: " + savedUser.getName() + " (" + savedUser.getRole() + ")", null, "Admin", "ADMIN");

            return ResponseEntity.ok(convertToDTO(savedUser));
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to create user: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(this::convertToDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(user -> {
                    try {
                        // Check if mobile number is being changed and already exists
                        if (!user.getMobileNumber().equals(userDetails.getMobileNumber()) &&
                            userRepository.existsByMobileNumberAndIdNot(userDetails.getMobileNumber(), id)) {
                            Map<String, String> errorResponse = new HashMap<>();
                            errorResponse.put("error", "Mobile number already exists");
                            return ResponseEntity.status(HttpStatus.CONFLICT).body(errorResponse);
                        }
                        
                        user.setName(userDetails.getName());
                        user.setMobileNumber(userDetails.getMobileNumber());
                        user.setEmail(userDetails.getEmail());
                        user.setRole(userDetails.getRole());
                        user.setStatus(userDetails.getStatus());
                        user.setTalukaId(userDetails.getTalukaId());
                        user.setSchoolId(userDetails.getSchoolId());
                        
                        if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                            user.setPassword(userDetails.getPassword());
                        }
                        
                        User updatedUser = userRepository.save(user);

                        // Log user update
                        auditLogService.log("USER_UPDATE", "User updated: " + updatedUser.getName(), null, "Admin", "ADMIN");

                        return ResponseEntity.ok(convertToDTO(updatedUser));
                    } catch (Exception e) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Failed to update user: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/reset-password")
    public ResponseEntity<?> resetPassword(@PathVariable Long id, @RequestBody PasswordResetRequest request) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(user -> {
                    try {
                        String newPassword = request.getNewPassword();
                        if (newPassword == null || newPassword.isEmpty()) {
                            newPassword = user.getMobileNumber();
                        }
                        userRepository.resetPassword(id, newPassword);

                        // Log password reset
                        auditLogService.log("PASSWORD_RESET", "Password reset for user: " + user.getName(), null, "Admin", "ADMIN");

                        Map<String, String> response = new HashMap<>();
                        response.put("message", "Password reset successfully");
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Failed to reset password");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<?> activateUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(user -> {
                    try {
                        userRepository.updateStatus(id, "Active");

                        // Log activation
                        auditLogService.log("USER_ACTIVATE", "User activated: " + user.getName(), null, "Admin", "ADMIN");

                        Map<String, String> response = new HashMap<>();
                        response.put("message", "User activated successfully");
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Failed to activate user");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<?> deactivateUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(user -> {
                    try {
                        userRepository.updateStatus(id, "Inactive");

                        // Log deactivation
                        auditLogService.log("USER_DEACTIVATE", "User deactivated: " + user.getName(), null, "Admin", "ADMIN");

                        Map<String, String> response = new HashMap<>();
                        response.put("message", "User deactivated successfully");
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Failed to deactivate user");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(user -> {
                    try {
                        userRepository.softDelete(id);

                        // Log deletion
                        auditLogService.log("USER_DELETE", "User deleted (soft): " + user.getName(), null, "Admin", "ADMIN");

                        Map<String, String> response = new HashMap<>();
                        response.put("message", "User deleted successfully");
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        Map<String, String> errorResponse = new HashMap<>();
                        errorResponse.put("error", "Failed to delete user");
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String keyword) {
        List<User> users = userRepository.searchUsers(keyword);
        List<UserDTO> userDTOs = users.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userDTOs);
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        try {
            long totalActive = userRepository.countByStatusAndIsDeletedFalse("Active");
            long totalSachivs = userRepository.countByRoleAndIsDeletedFalse(Role.SACHIV);
            long totalHeadMasters = userRepository.countByRoleAndIsDeletedFalse(Role.HEADMASTER);
            long totalClerks = userRepository.countByRoleAndIsDeletedFalse(Role.CLERK);
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalActive", totalActive);
            stats.put("totalSachivs", totalSachivs);
            stats.put("totalHeadMasters", totalHeadMasters);
            stats.put("totalClerks", totalClerks);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to fetch stats");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setMobileNumber(user.getMobileNumber());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setTalukaId(user.getTalukaId());
        dto.setSchoolId(user.getSchoolId());
        
        if (user.getTalukaId() != null) {
            talukaRepository.findById(user.getTalukaId())
                .ifPresent(taluka -> dto.setTalukaName(taluka.getName()));
        }
        
        if (user.getSchoolId() != null) {
            schoolRepository.findById(user.getSchoolId())
                .ifPresent(school -> dto.setSchoolName(school.getName()));
        }
        
        if (user.getCreatedAt() != null) {
            dto.setCreatedAt(user.getCreatedAt().format(formatter));
        }
        
        if (user.getLastLogin() != null) {
            dto.setLastLogin(user.getLastLogin().format(formatter));
        }
        
        return dto;
    }
}
