package com.worth.ifs.bankdetails.domain;

import javax.persistence.*;

/**
 * Experian verification conditions
 */
@Entity
public class VerificationCondition {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String severity;
    private Integer code;
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bankDetailsId" , referencedColumnName = "id")
    private BankDetails bankDetails;

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BankDetails getBankDetails() {
        return bankDetails;
    }

    public void setBankDetails(BankDetails bankDetails) {
        this.bankDetails = bankDetails;
    }
}
