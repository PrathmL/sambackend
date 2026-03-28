package com.esspbackend.repository;

import com.esspbackend.entity.FundSource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface FundSourceRepository extends JpaRepository<FundSource, Long> {
    
    List<FundSource> findByWorkId(Long workId);
    
    @Query("SELECT SUM(f.amount) FROM FundSource f WHERE f.workId = :workId")
    Double getTotalAllocatedByWorkId(@Param("workId") Long workId);
    
    void deleteByWorkId(Long workId);
}