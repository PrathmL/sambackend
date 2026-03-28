package com.esspbackend.repository;

import com.esspbackend.entity.Material;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface MaterialRepository extends JpaRepository<Material, Long> {
    List<Material> findByIsActiveTrue();
    List<Material> findByCategoryId(Long categoryId);
    
    @Query("SELECT m FROM Material m WHERE m.isActive = true AND (LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(m.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Material> searchMaterials(@Param("keyword") String keyword);
    
    @Query("SELECT m FROM Material m WHERE m.currentStock <= m.minStockLevel AND m.isActive = true")
    List<Material> findLowStockMaterials();
    
    @Query("SELECT m FROM Material m WHERE m.currentStock <= 0 AND m.isActive = true")
    List<Material> findOutOfStockMaterials();
}