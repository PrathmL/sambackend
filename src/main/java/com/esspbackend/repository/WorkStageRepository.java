package com.esspbackend.repository;

import com.esspbackend.entity.WorkStage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface WorkStageRepository extends JpaRepository<WorkStage, Long> {
    
    // Basic finders
    List<WorkStage> findByWorkId(Long workId);
    
    @Query("SELECT w FROM WorkStage w WHERE w.workId = :workId ORDER BY w.id ASC")
    List<WorkStage> findByWorkIdOrderByIdAsc(@Param("workId") Long workId);
    
    @Query("SELECT w FROM WorkStage w WHERE w.workId = :workId AND w.status != 'COMPLETED' ORDER BY w.id ASC")
    List<WorkStage> findIncompleteStagesByWorkId(@Param("workId") Long workId);
    
    @Query("SELECT COALESCE(SUM(w.weightage), 0) FROM WorkStage w WHERE w.workId = :workId AND w.status = 'COMPLETED'")
    Integer getCompletedWeightageSum(@Param("workId") Long workId);
    
    @Query("SELECT w FROM WorkStage w WHERE w.workId = :workId AND w.status = 'IN_PROGRESS'")
    Optional<WorkStage> findCurrentInProgressStage(@Param("workId") Long workId);
    
    @Query("SELECT w FROM WorkStage w WHERE w.workId = :workId ORDER BY w.id ASC")
    List<WorkStage> findAllByWorkIdOrderByOrderIndex(@Param("workId") Long workId);
}