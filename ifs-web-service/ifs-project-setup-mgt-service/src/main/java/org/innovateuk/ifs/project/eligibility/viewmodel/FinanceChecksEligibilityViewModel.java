package org.innovateuk.ifs.project.eligibility.viewmodel;

import org.innovateuk.ifs.project.finance.resource.EligibilityStatus;

import java.time.LocalDate;

/**
 * View model for the Eligibility page
 */
public class FinanceChecksEligibilityViewModel {

    private Long projectId;
    private boolean eligibilityApproved;
    private EligibilityStatus eligibilityStatus;
    private String approverFirstName;
    private String approverLastName;
    private LocalDate approvalDate;

    public FinanceChecksEligibilityViewModel(Long projectId, boolean eligibilityApproved, EligibilityStatus eligibilityStatus,
                                             String approverFirstName, String approverLastName, LocalDate approvalDate) {

        this.projectId = projectId;
        this.eligibilityApproved = eligibilityApproved;
        this.eligibilityStatus = eligibilityStatus;
        this.approverFirstName = approverFirstName;
        this.approverLastName = approverLastName;
        this.approvalDate = approvalDate;
    }

    public boolean isReadOnly() {
        return eligibilityApproved;
    }

    private boolean isApproved() {
        return eligibilityApproved;
    }

    public boolean isShowSaveAndContinueButton() {
        return !isApproved();
    }

    public boolean isShowBackToFinanceCheckButton() {
        return isApproved();
    }

    public Long getProjectId() {
        return projectId;
    }

    public void setProjectId(Long projectId) {
        this.projectId = projectId;
    }

    public boolean isEligibilityApproved() {
        return eligibilityApproved;
    }

    public void setEligibilityApproved(boolean eligibilityApproved) {
        this.eligibilityApproved = eligibilityApproved;
    }

    public EligibilityStatus getEligibilityStatus() {
        return eligibilityStatus;
    }

    public void setEligibilityStatus(EligibilityStatus eligibilityStatus) {
        this.eligibilityStatus = eligibilityStatus;
    }

    public String getApproverFirstName() {
        return approverFirstName;
    }

    public void setApproverFirstName(String approverFirstName) {
        this.approverFirstName = approverFirstName;
    }

    public String getApproverLastName() {
        return approverLastName;
    }

    public void setApproverLastName(String approverLastName) {
        this.approverLastName = approverLastName;
    }

    public LocalDate getApprovalDate() {
        return approvalDate;
    }

    public void setApprovalDate(LocalDate approvalDate) {
        this.approvalDate = approvalDate;
    }
}

