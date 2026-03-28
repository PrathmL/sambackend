package com.esspbackend.repository;

import com.esspbackend.entity.HandoverCertificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface HandoverCertificateRepository extends JpaRepository<HandoverCertificate, Long> {
    Optional<HandoverCertificate> findByWorkId(Long workId);
}
