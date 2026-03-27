package com.esspbackend.repository;

import com.esspbackend.entity.School;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SchoolRepository extends JpaRepository<School, Long> {
    List<School> findByTalukaId(Long talukaId);
}
