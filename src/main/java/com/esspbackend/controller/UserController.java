package com.esspbackend.controller;

import com.esspbackend.dto.LoginResponse;
import com.esspbackend.entity.User;
import com.esspbackend.repository.UserRepository;
import com.esspbackend.service.AuditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogService auditLogService;

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserProfile(@PathVariable Long id) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(user -> {
                    LoginResponse response = new LoginResponse(
                            user.getId(),
                            user.getName(),
                            user.getMobileNumber(),
                            user.getEmail(),
                            user.getRole(),
                            user.getStatus(),
                            user.getTalukaId(),
                            user.getSchoolId()
                    );
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody Map<String, String> profileData) {
        return userRepository.findById(id)
                .filter(user -> !user.isDeleted())
                .map(user -> {
                    try {
                        String name = profileData.get("name");
                        String email = profileData.get("email");
                        String mobileNumber = profileData.get("mobileNumber");
                        String password = profileData.get("password");

                        // Validate mobile number uniqueness
                        if (mobileNumber != null && !mobileNumber.equals(user.getMobileNumber())) {
                            if (userRepository.existsByMobileNumberAndIdNot(mobileNumber, id)) {
                                Map<String, String> error = new HashMap<>();
                                error.put("error", "Mobile number already in use");
                                return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
                            }
                            user.setMobileNumber(mobileNumber);
                        }

                        if (name != null) user.setName(name);
                        if (email != null) user.setEmail(email);
                        if (password != null && !password.isEmpty()) user.setPassword(password);

                        User updatedUser = userRepository.save(user);
                        
                        // Log profile update
                        auditLogService.log("PROFILE_UPDATE", "User updated their profile: " + updatedUser.getName(), updatedUser.getId(), updatedUser.getName(), updatedUser.getRole().name());

                        LoginResponse response = new LoginResponse(
                                updatedUser.getId(),
                                updatedUser.getName(),
                                updatedUser.getMobileNumber(),
                                updatedUser.getEmail(),
                                updatedUser.getRole(),
                                updatedUser.getStatus(),
                                updatedUser.getTalukaId(),
                                updatedUser.getSchoolId()
                        );
                        return ResponseEntity.ok(response);
                    } catch (Exception e) {
                        Map<String, String> error = new HashMap<>();
                        error.put("error", "Failed to update profile: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
