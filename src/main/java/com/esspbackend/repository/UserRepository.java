package com.esspbackend.repository;

import com.esspbackend.entity.User;
import com.esspbackend.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    
    // Find by mobile number
    Optional<User> findByMobileNumber(String mobileNumber);
    
    // Find by role
    List<User> findByRole(Role role);
    
    // Find by role and not deleted (using isDeleted = false)
    @Query("SELECT u FROM User u WHERE u.role = :role AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    List<User> findByRoleAndIsDeletedFalse(@Param("role") Role role);
    
    // Find by taluka and not deleted
    @Query("SELECT u FROM User u WHERE u.talukaId = :talukaId AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    List<User> findByTalukaIdAndIsDeletedFalse(@Param("talukaId") Long talukaId);
    
    // Find by school and not deleted
    @Query("SELECT u FROM User u WHERE u.schoolId = :schoolId AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    List<User> findBySchoolIdAndIsDeletedFalse(@Param("schoolId") Long schoolId);
    
    // Find by status and not deleted
    @Query("SELECT u FROM User u WHERE u.status = :status AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    List<User> findByStatusAndIsDeletedFalse(@Param("status") String status);
    
    // Find all non-deleted users
    @Query("SELECT u FROM User u WHERE u.isDeleted IS NULL OR u.isDeleted = false")
    List<User> findAllNonDeleted();
    
    // Alternative method name approach
    @Query("SELECT u FROM User u WHERE u.isDeleted IS NULL OR u.isDeleted = false")
    List<User> findByIsDeletedFalse();
    
    // Search users by name, mobile, or email (non-deleted only)
    @Query("SELECT u FROM User u WHERE (u.isDeleted IS NULL OR u.isDeleted = false) AND " +
           "(LOWER(u.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "u.mobileNumber LIKE CONCAT('%', :keyword, '%') OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<User> searchUsers(@Param("keyword") String keyword);
    
    // Update user status
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.status = :status WHERE u.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") String status);
    
    // Reset user password
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.password = :password WHERE u.id = :id")
    void resetPassword(@Param("id") Long id, @Param("password") String password);
    
    // Soft delete user
    @Modifying
    @Transactional
    @Query("UPDATE User u SET u.isDeleted = true, u.deletedAt = CURRENT_TIMESTAMP WHERE u.id = :id")
    void softDelete(@Param("id") Long id);
    
    // Check if mobile number exists (only active users)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
           "WHERE u.mobileNumber = :mobileNumber AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    boolean existsByMobileNumber(@Param("mobileNumber") String mobileNumber);
    
    // Check if mobile number exists for a different user (for update validation)
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u " +
           "WHERE u.mobileNumber = :mobileNumber AND u.id != :id AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    boolean existsByMobileNumberAndIdNot(@Param("mobileNumber") String mobileNumber, @Param("id") Long id);
    
    // Count users by status (non-deleted only)
    @Query("SELECT COUNT(u) FROM User u WHERE u.status = :status AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    long countByStatusAndIsDeletedFalse(@Param("status") String status);
    
    // Count users by role (non-deleted only)
    @Query("SELECT COUNT(u) FROM User u WHERE u.role = :role AND (u.isDeleted IS NULL OR u.isDeleted = false)")
    long countByRoleAndIsDeletedFalse(@Param("role") Role role);
}