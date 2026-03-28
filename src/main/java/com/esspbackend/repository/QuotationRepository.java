package com.esspbackend.repository;

import com.esspbackend.entity.Quotation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface QuotationRepository extends JpaRepository<Quotation, Long> {
    
    // Find by work request ID (unique)
    Optional<Quotation> findByWorkRequestId(Long workRequestId);
    
    // Find all quotations for a school
    List<Quotation> findBySchoolIdOrderBySubmittedAtDesc(Long schoolId);
    
    // Find all quotations by status
    List<Quotation> findByStatus(String status);
    
    // Find pending quotations for a school
    @Query("SELECT q FROM Quotation q WHERE q.schoolId = :schoolId AND q.status = 'PENDING' ORDER BY q.submittedAt DESC")
    List<Quotation> findPendingQuotationsBySchool(@Param("schoolId") Long schoolId);
    
    // Find approved quotations for a school
    @Query("SELECT q FROM Quotation q WHERE q.schoolId = :schoolId AND q.status = 'APPROVED' ORDER BY q.approvedAt DESC")
    List<Quotation> findApprovedQuotationsBySchool(@Param("schoolId") Long schoolId);
    
    // Find rejected quotations for a school
    @Query("SELECT q FROM Quotation q WHERE q.schoolId = :schoolId AND q.status = 'REJECTED' ORDER BY q.rejectedAt DESC")
    List<Quotation> findRejectedQuotationsBySchool(@Param("schoolId") Long schoolId);
    
    // Find quotations by work request with details
    @Query("SELECT q FROM Quotation q WHERE q.workRequestId = :workRequestId")
    Optional<Quotation> findByWorkRequestIdWithDetails(@Param("workRequestId") Long workRequestId);
    
    // Find all quotations by date range
    @Query("SELECT q FROM Quotation q WHERE q.submittedAt BETWEEN :startDate AND :endDate ORDER BY q.submittedAt DESC")
    List<Quotation> findBySubmittedAtBetween(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Find quotations by school and date range
    @Query("SELECT q FROM Quotation q WHERE q.schoolId = :schoolId AND q.submittedAt BETWEEN :startDate AND :endDate ORDER BY q.submittedAt DESC")
    List<Quotation> findBySchoolIdAndSubmittedAtBetween(@Param("schoolId") Long schoolId, @Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    // Count pending quotations by school
    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.schoolId = :schoolId AND q.status = 'PENDING'")
    long countPendingQuotationsBySchool(@Param("schoolId") Long schoolId);
    
    // Count approved quotations by school
    @Query("SELECT COUNT(q) FROM Quotation q WHERE q.schoolId = :schoolId AND q.status = 'APPROVED'")
    long countApprovedQuotationsBySchool(@Param("schoolId") Long schoolId);
    
    // Update quotation status
    @Modifying
    @Transactional
    @Query("UPDATE Quotation q SET q.status = :status, q.adminRemarks = :remarks, q.approvedAt = :approvedAt WHERE q.id = :id")
    void updateQuotationStatus(@Param("id") Long id, @Param("status") String status, @Param("remarks") String remarks, @Param("approvedAt") LocalDateTime approvedAt);
    
    // Reject quotation with reason
    @Modifying
    @Transactional
    @Query("UPDATE Quotation q SET q.status = 'REJECTED', q.adminRemarks = :remarks, q.rejectedAt = :rejectedAt WHERE q.id = :id")
    void rejectQuotation(@Param("id") Long id, @Param("remarks") String remarks, @Param("rejectedAt") LocalDateTime rejectedAt);
    
    // Find quotations by work request ID and status
    @Query("SELECT q FROM Quotation q WHERE q.workRequestId = :workRequestId AND q.status = :status")
    Optional<Quotation> findByWorkRequestIdAndStatus(@Param("workRequestId") Long workRequestId, @Param("status") String status);
    
    // Find recent quotations for dashboard
    @Query("SELECT q FROM Quotation q WHERE q.schoolId = :schoolId ORDER BY q.submittedAt DESC LIMIT 5")
    List<Quotation> findRecentQuotationsBySchool(@Param("schoolId") Long schoolId);
    
    // Get total quotation value for a school (approved only)
    @Query("SELECT COALESCE(SUM(q.grandTotal), 0) FROM Quotation q WHERE q.schoolId = :schoolId AND q.status = 'APPROVED'")
    Double getTotalApprovedQuotationValue(@Param("schoolId") Long schoolId);
    
    // Check if quotation exists for work request
    @Query("SELECT CASE WHEN COUNT(q) > 0 THEN true ELSE false END FROM Quotation q WHERE q.workRequestId = :workRequestId")
    boolean existsByWorkRequestId(@Param("workRequestId") Long workRequestId);
}