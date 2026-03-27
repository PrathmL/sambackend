package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "blocker_comments")
public class BlockerComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "blocker_id")
    private Long blockerId;

    @Column(columnDefinition = "TEXT")
    private String comment;

    @Column(name = "commented_by_id")
    private Long commentedById;

    @Column(name = "commented_by_role")
    private String commentedByRole;

    @Column(name = "is_internal")
    private Boolean isInternal = false;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public BlockerComment() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getBlockerId() { return blockerId; }
    public void setBlockerId(Long blockerId) { this.blockerId = blockerId; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public Long getCommentedById() { return commentedById; }
    public void setCommentedById(Long commentedById) { this.commentedById = commentedById; }
    public String getCommentedByRole() { return commentedByRole; }
    public void setCommentedByRole(String commentedByRole) { this.commentedByRole = commentedByRole; }
    public Boolean getIsInternal() { return isInternal; }
    public void setIsInternal(Boolean isInternal) { this.isInternal = isInternal; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}