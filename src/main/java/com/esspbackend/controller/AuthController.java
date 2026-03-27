package com.esspbackend.controller;

import com.esspbackend.dto.LoginRequest;
import com.esspbackend.dto.LoginResponse;
import com.esspbackend.entity.User;
import com.esspbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        Optional<User> userOptional = userRepository.findByMobileNumber(loginRequest.getMobileNumber());

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // In a real application, you should use a password encoder
            if (user.getPassword().equals(loginRequest.getPassword())) {
                if ("Active".equalsIgnoreCase(user.getStatus())) {
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
                } else {
                    return ResponseEntity.status(HttpStatus.FORBIDDEN).body("User account is inactive");
                }
            }
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid mobile number or password");
    }
}