package com.esspbackend.repository;

import com.esspbackend.entity.WorkProgressPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WorkProgressPhotoRepository extends JpaRepository<WorkProgressPhoto, Long> {
    List<WorkProgressPhoto> findByProgressUpdateId(Long progressUpdateId);
    
    void deleteByProgressUpdateId(Long progressUpdateId);
}