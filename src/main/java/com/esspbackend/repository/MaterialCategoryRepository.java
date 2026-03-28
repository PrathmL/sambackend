package com.esspbackend.repository;

import com.esspbackend.entity.MaterialCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import java.util.Optional;

public interface MaterialCategoryRepository extends JpaRepository<MaterialCategory, Long> {
    Optional<MaterialCategory> findByName(String name);
    List<MaterialCategory> findByIsActiveTrue();
    boolean existsByName(String name);
}