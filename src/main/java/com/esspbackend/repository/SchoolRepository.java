package com.esspbackend.repository;

import com.esspbackend.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface SchoolRepository extends JpaRepository<School, Long> {
    
    Optional<School> findByCode(String code);
    
    List<School> findByTalukaId(Long talukaId);
    
    List<School> findByStatus(String status);
    
    @Query("SELECT s FROM School s WHERE s.talukaId = :talukaId AND s.status = 'Active'")
    List<School> findActiveByTalukaId(@Param("talukaId") Long talukaId);
    
    @Query("SELECT COUNT(s) FROM School s WHERE s.talukaId = :talukaId")
    long countByTalukaId(@Param("talukaId") Long talukaId);
    
    @Query("SELECT s FROM School s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(s.code) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<School> searchSchools(@Param("keyword") String keyword);
}