package com.esspbackend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "handover_certificates")
public class HandoverCertificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "work_id", nullable = false)
    private Long workId;

    @Column(name = "certificate_number", unique = true)
    private String certificateNumber;

    @Column(name = "final_cost")
    private Double finalCost;

    @Column(name = "sanctioned_funds")
    private Double sanctionedFunds;

    @Column(name = "actual_expenditure")
    private Double actualExpenditure;

    @Column(name = "variance")
    private Double variance;

    @Column(name = "unspent_funds")
    private Double unspentFunds;

    @Column(name = "fund_return_details")
    private String fundReturnDetails;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "sachiv_esign")
    private String sachivESign; // Path to signature image or digital thumbprint

    @Column(name = "hm_esign")
    private String hmESign;

    @Column(name = "signed_date")
    private LocalDateTime signedDate;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (signedDate == null) signedDate = LocalDateTime.now();
    }

    public HandoverCertificate() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getWorkId() { return workId; }
    public void setWorkId(Long workId) { this.workId = workId; }
    public String getCertificateNumber() { return certificateNumber; }
    public void setCertificateNumber(String certificateNumber) { this.certificateNumber = certificateNumber; }
    public Double getFinalCost() { return finalCost; }
    public void setFinalCost(Double finalCost) { this.finalCost = finalCost; }
    
    public Double getSanctionedFunds() { return sanctionedFunds; }
    public void setSanctionedFunds(Double sanctionedFunds) { this.sanctionedFunds = sanctionedFunds; }
    
    public Double getActualExpenditure() { return actualExpenditure; }
    public void setActualExpenditure(Double actualExpenditure) { this.actualExpenditure = actualExpenditure; }
    
    public Double getVariance() { return variance; }
    public void setVariance(Double variance) { this.variance = variance; }
    
    public Double getUnspentFunds() { return unspentFunds; }
    public void setUnspentFunds(Double unspentFunds) { this.unspentFunds = unspentFunds; }
    
    public String getFundReturnDetails() { return fundReturnDetails; }
    public void setFundReturnDetails(String fundReturnDetails) { this.fundReturnDetails = fundReturnDetails; }

    public LocalDateTime getCompletionDate() { return completionDate; }
    public void setCompletionDate(LocalDateTime completionDate) { this.completionDate = completionDate; }
    public String getSachivESign() { return sachivESign; }
    public void setSachivESign(String sachivESign) { this.sachivESign = sachivESign; }
    public String getHmESign() { return hmESign; }
    public void setHmESign(String hmESign) { this.hmESign = hmESign; }
    public LocalDateTime getSignedDate() { return signedDate; }
    public void setSignedDate(LocalDateTime signedDate) { this.signedDate = signedDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
