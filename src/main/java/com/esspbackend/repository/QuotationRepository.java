package com.esspbackend.repository;

import com.esspbackend.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    Optional<Quotation> findByWorkRequestId(Long workRequestId);
}
