package com.esspbackend.dto;

import java.time.LocalDateTime;

public class BlockerCommentDTO {
    private Long id;
    private String comment;
    private String commentedBy;
    private String commentedByRole;
    private Boolean isInternal;
    private LocalDateTime createdAt;

    public BlockerCommentDTO() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    
    public String getCommentedBy() { return commentedBy; }
    public void setCommentedBy(String commentedBy) { this.commentedBy = commentedBy; }
    
    public String getCommentedByRole() { return commentedByRole; }
    public void setCommentedByRole(String commentedByRole) { this.commentedByRole = commentedByRole; }
    
    public Boolean getIsInternal() { return isInternal; }
    public void setIsInternal(Boolean isInternal) { this.isInternal = isInternal; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}