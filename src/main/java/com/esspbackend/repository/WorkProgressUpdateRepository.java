package com.esspbackend.repository;

import com.esspbackend.entity.WorkProgressUpdate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface WorkProgressUpdateRepository extends JpaRepository<WorkProgressUpdate, Long> {
    
    // Basic finders
    List<WorkProgressUpdate> findByWorkId(Long workId);
    
    List<WorkProgressUpdate> findByWorkIdAndStageId(Long workId, Long stageId);
    
    // Custom queries with ordering
    @Query("SELECT w FROM WorkProgressUpdate w WHERE w.workId = :workId ORDER BY w.updatedAt DESC")
    List<WorkProgressUpdate> findByWorkIdOrderByUpdatedAtDesc(@Param("workId") Long workId);
    
    @Query("SELECT w FROM WorkProgressUpdate w WHERE w.workId = :workId AND w.stageId = :stageId ORDER BY w.updatedAt DESC")
    List<WorkProgressUpdate> findByWorkIdAndStageIdOrderByUpdatedAtDesc(@Param("workId") Long workId, @Param("stageId") Long stageId);
    
    @Query("SELECT w FROM WorkProgressUpdate w WHERE w.workId = :workId ORDER BY w.updatedAt DESC LIMIT 1")
    Optional<WorkProgressUpdate> findLatestByWorkId(@Param("workId") Long workId);
    
    @Query("SELECT w FROM WorkProgressUpdate w WHERE w.workId = :workId ORDER BY w.updatedAt DESC")
    List<WorkProgressUpdate> findRecentUpdatesByWorkId(@Param("workId") Long workId);
    
    // Get total cost for a work
    @Query("SELECT COALESCE(SUM(w.totalCost), 0) FROM WorkProgressUpdate w WHERE w.workId = :workId")
    Double getTotalCostByWorkId(@Param("workId") Long workId);
}