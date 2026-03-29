package com.esspbackend.repository;

import com.esspbackend.entity.VerificationPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VerificationPhotoRepository extends JpaRepository<VerificationPhoto, Long> {
    List<VerificationPhoto> findByWorkId(Long workId);
}
