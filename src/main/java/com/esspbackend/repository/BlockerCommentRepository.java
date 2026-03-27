package com.esspbackend.repository;

import com.esspbackend.entity.BlockerComment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BlockerCommentRepository extends JpaRepository<BlockerComment, Long> {
    List<BlockerComment> findByBlockerIdOrderByCreatedAtAsc(Long blockerId);
    
    void deleteByBlockerId(Long blockerId);
}