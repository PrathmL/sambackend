package com.esspbackend.config;

import com.esspbackend.entity.Role;
import com.esspbackend.entity.User;
import com.esspbackend.entity.Taluka;
import com.esspbackend.entity.School;
import com.esspbackend.repository.UserRepository;
import com.esspbackend.repository.TalukaRepository;
import com.esspbackend.repository.SchoolRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository, 
                                 TalukaRepository talukaRepository, 
                                 SchoolRepository schoolRepository) {
        return args -> {
            // Seed Talukas if empty
            Long haveliId = null;
            Long mulshiId = null;
            
            if (talukaRepository.count() == 0) {
                Taluka t1 = new Taluka(null, "Haveli", "Pune", "HAV01", "Active");
                Taluka t2 = new Taluka(null, "Mulshi", "Pune", "MUL02", "Active");
                haveliId = talukaRepository.save(t1).getId();
                mulshiId = talukaRepository.save(t2).getId();
                System.out.println("Talukas seeded!");
            } else {
                List<Taluka> talukas = talukaRepository.findAll();
                for (Taluka t : talukas) {
                    if ("Haveli".equals(t.getName())) {
                        haveliId = t.getId();
                    }
                    if ("Mulshi".equals(t.getName())) {
                        mulshiId = t.getId();
                    }
                }
            }

            // Seed Schools if empty
            Long school1Id = null;
            Long school2Id = null;
            
            if (schoolRepository.count() == 0) {
                if (haveliId != null) {
                    School s1 = new School(null, "Z.P. School Haveli 1", "SCH001", haveliId, 
                        "Haveli Main Road", "020-123456", 1950, "Active");
                    school1Id = schoolRepository.save(s1).getId();
                }
                if (mulshiId != null) {
                    School s2 = new School(null, "Z.P. School Mulshi 1", "SCH002", mulshiId, 
                        "Mulshi Market", "020-654321", 1960, "Active");
                    school2Id = schoolRepository.save(s2).getId();
                }
                System.out.println("Schools seeded!");
            } else {
                List<School> schools = schoolRepository.findAll();
                for (School s : schools) {
                    if ("SCH001".equals(s.getCode())) {
                        school1Id = s.getId();
                    }
                    if ("SCH002".equals(s.getCode())) {
                        school2Id = s.getId();
                    }
                }
            }

            // Seed Users if empty
            if (userRepository.count() == 0) {
                // Initial Admin
                User admin = new User(null, "Admin User", "1234567890", "admin123", 
                    "admin@gmail.com", Role.ADMIN, "Active", null, null);
                admin.setIsDeleted(false);
                userRepository.save(admin);
                
                // Initial Sachiv
                User sachiv = new User(null, "Sachiv User", "9876543210", "sachiv123", 
                    "sachiv@gmail.com", Role.SACHIV, "Active", haveliId, null);
                sachiv.setIsDeleted(false);
                userRepository.save(sachiv);
                
                // Initial Headmaster - assign to school1
                User headmaster = new User(null, "Headmaster User", "8888888888", "headmaster123", 
                    "headmaster@gmail.com", Role.HEADMASTER, "Active", null, school1Id);
                headmaster.setIsDeleted(false);
                userRepository.save(headmaster);
                
                // Initial Clerk - assign to school1
                User clerk = new User(null, "Clerk User", "7777777777", "clerk123", 
                    "clerk@gmail.com", Role.CLERK, "Active", null, school1Id);
                clerk.setIsDeleted(false);
                userRepository.save(clerk);
                
                System.out.println("Database seeded with initial users!");
                System.out.println("Headmaster School ID: " + school1Id);
                System.out.println("Clerk School ID: " + school1Id);
            }
        };
    }
}