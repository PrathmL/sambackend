package com.esspbackend.repository;

import com.esspbackend.entity.WorkProgressItemUsage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface WorkProgressItemUsageRepository extends JpaRepository<WorkProgressItemUsage, Long> {
    List<WorkProgressItemUsage> findByWorkProgressUpdateId(Long workProgressUpdateId);
}
