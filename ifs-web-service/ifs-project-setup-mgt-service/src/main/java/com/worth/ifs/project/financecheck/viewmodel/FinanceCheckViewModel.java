package com.worth.ifs.project.financecheck.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.time.LocalDate;

public class FinanceCheckViewModel {

    private Long projectId;
    private Long organisationId;
    private String financeContactName;
    private String financeContactEmail;
    private boolean isResearch;
    private boolean financeChecksApproved;
    private String approverName;
    private LocalDate approvalDate;

    FinanceCheckViewModel() {
    }

    public FinanceCheckViewModel(Long projectId, Long organisationId, String financeContactName, String financeContactEmail, boolean isResearch, boolean financeChecksApproved, String approverName, LocalDate approvalDate) {
        this.projectId = projectId;
        this.organisationId = organisationId;
        this.financeContactName = financeContactName;
        this.financeContactEmail = financeContactEmail;
        this.isResearch = isResearch;
        this.financeChecksApproved = financeChecksApproved;
        this.approverName = approverName;
        this.approvalDate = approvalDate;
    }

    public FinanceCheckViewModel(Long projectId, Long organisationId, boolean isResearch, boolean financeChecksApproved, String approverName, LocalDate approvalDate) {
        this(projectId, organisationId, null, null, isResearch, financeChecksApproved, approverName, approvalDate);
    }

    public String getFinanceContactName() {
        return financeContactName;
    }

    public String getFinanceContactEmail() {
        return financeContactEmail;
    }

    public boolean isResearch() {
        return isResearch;
    }

    public boolean isFinanceChecksApproved() {
        return financeChecksApproved;
    }

    public void setFinanceChecksApproved(boolean financeChecksApproved) {
        this.financeChecksApproved = financeChecksApproved;
    }

    public String getApproverName() {
        return approverName;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getOrganisationId() {
        return organisationId;
    }

    public boolean isShowFinanceChecksApprovedMessage() {
        return financeChecksApproved;
    }

    public boolean isShowApproveButton() {
        return !financeChecksApproved;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        FinanceCheckViewModel that = (FinanceCheckViewModel) o;

        return new EqualsBuilder()
                .append(isResearch, that.isResearch)
                .append(financeChecksApproved, that.financeChecksApproved)
                .append(financeContactName, that.financeContactName)
                .append(financeContactEmail, that.financeContactEmail)
                .append(approverName, that.approverName)
                .append(approvalDate, that.approvalDate)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(financeContactName)
                .append(financeContactEmail)
                .append(isResearch)
                .append(financeChecksApproved)
                .append(approverName)
                .append(approvalDate)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("financeContactName", financeContactName)
                .append("financeContactEmail", financeContactEmail)
                .append("isResearch", isResearch)
                .append("isApproved", financeChecksApproved)
                .append("approverName", approverName)
                .append("approvalDate", approvalDate)
                .toString();
    }
}
