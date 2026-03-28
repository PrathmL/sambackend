package com.esspbackend.repository;

import com.esspbackend.entity.BlockerComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface BlockerCommentRepository extends JpaRepository<BlockerComment, Long> {
    
    @Query("SELECT c FROM BlockerComment c WHERE c.blockerId = :blockerId ORDER BY c.createdAt ASC")
    List<BlockerComment> findByBlockerIdOrderByCreatedAtAsc(@Param("blockerId") Long blockerId);
    
    @Query("SELECT c FROM BlockerComment c WHERE c.blockerId = :blockerId AND c.isInternal = false ORDER BY c.createdAt ASC")
    List<BlockerComment> findPublicCommentsByBlockerId(@Param("blockerId") Long blockerId);
    
    void deleteByBlockerId(Long blockerId);
}