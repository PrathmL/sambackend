package com.esspbackend.repository;

import com.esspbackend.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    
    List<StockMovement> findBySchoolIdOrderByCreatedAtDesc(Long schoolId);
    
    List<StockMovement> findBySchoolIdAndMaterialIdOrderByCreatedAtDesc(Long schoolId, Long materialId);
    
    List<StockMovement> findByWorkIdOrderByCreatedAtDesc(Long workId);
    
    @Query("SELECT sm FROM StockMovement sm WHERE sm.schoolId = :schoolId AND sm.createdAt BETWEEN :startDate AND :endDate ORDER BY sm.createdAt DESC")
    List<StockMovement> findBySchoolIdAndDateRange(@Param("schoolId") Long schoolId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT SUM(sm.quantity) FROM StockMovement sm WHERE sm.schoolId = :schoolId AND sm.materialId = :materialId AND sm.movementType = 'OUT' AND sm.createdAt BETWEEN :startDate AND :endDate")
    Double getConsumptionByMaterial(@Param("schoolId") Long schoolId, @Param("materialId") Long materialId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}