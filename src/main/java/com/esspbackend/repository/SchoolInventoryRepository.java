package com.esspbackend.repository;

import com.esspbackend.entity.SchoolInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface SchoolInventoryRepository extends JpaRepository<SchoolInventory, Long> {
    
    List<SchoolInventory> findBySchoolId(Long schoolId);
    
    Optional<SchoolInventory> findBySchoolIdAndMaterialId(Long schoolId, Long materialId);
    
    @Query("SELECT si FROM SchoolInventory si WHERE si.schoolId = :schoolId AND si.currentQuantity <= si.reorderLevel")
    List<SchoolInventory> findLowStockBySchool(@Param("schoolId") Long schoolId);
    
    @Modifying
    @Transactional
    @Query("UPDATE SchoolInventory si SET si.currentQuantity = si.currentQuantity + :quantity WHERE si.schoolId = :schoolId AND si.materialId = :materialId")
    void addStock(@Param("schoolId") Long schoolId, @Param("materialId") Long materialId, @Param("quantity") Double quantity);
    
    @Modifying
    @Transactional
    @Query("UPDATE SchoolInventory si SET si.currentQuantity = si.currentQuantity - :quantity WHERE si.schoolId = :schoolId AND si.materialId = :materialId AND si.currentQuantity >= :quantity")
    int deductStock(@Param("schoolId") Long schoolId, @Param("materialId") Long materialId, @Param("quantity") Double quantity);
}