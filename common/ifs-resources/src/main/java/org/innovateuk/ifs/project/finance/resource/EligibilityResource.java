package org.innovateuk.ifs.project.finance.resource;

import java.time.LocalDate;

/**
 * Resource to hold the Eligibility details
 */
public class EligibilityResource {

    private EligibilityState eligibility;
    private EligibilityRagStatus eligibilityRagStatus;

    private String eligibilityApprovalUserFirstName;
    private String eligibilityApprovalUserLastName;
    private LocalDate eligibilityApprovalDate;

    private String eligibilityResetUserFirstName;
    private String eligibilityResetUserLastName;
    private LocalDate eligibilityResetDate;

    // for JSON marshalling
    EligibilityResource() {
    }

    public EligibilityResource(EligibilityState eligibility, EligibilityRagStatus eligibilityRagStatus) {
        this.eligibility = eligibility;
        this.eligibilityRagStatus = eligibilityRagStatus;
    }

    public EligibilityState getEligibility() {
        return eligibility;
    }

    public void setEligibility(EligibilityState eligibility) {
        this.eligibility = eligibility;
    }

    public EligibilityRagStatus getEligibilityRagStatus() {
        return eligibilityRagStatus;
    }

    public void setEligibilityRagStatus(EligibilityRagStatus eligibilityRagStatus) {
        this.eligibilityRagStatus = eligibilityRagStatus;
    }

    public String getEligibilityApprovalUserFirstName() {
        return eligibilityApprovalUserFirstName;
    }

    public void setEligibilityApprovalUserFirstName(String eligibilityApprovalUserFirstName) {
        this.eligibilityApprovalUserFirstName = eligibilityApprovalUserFirstName;
    }

    public String getEligibilityApprovalUserLastName() {
        return eligibilityApprovalUserLastName;
    }

    public void setEligibilityApprovalUserLastName(String eligibilityApprovalUserLastName) {
        this.eligibilityApprovalUserLastName = eligibilityApprovalUserLastName;
    }

    public LocalDate getEligibilityApprovalDate() {
        return eligibilityApprovalDate;
    }

    public void setEligibilityApprovalDate(LocalDate eligibilityApprovalDate) {
        this.eligibilityApprovalDate = eligibilityApprovalDate;
    }

    public LocalDate getEligibilityResetDate() {
        return eligibilityResetDate;
    }

    public void setEligibilityResetDate(LocalDate eligibilityResetDate) {
        this.eligibilityResetDate = eligibilityResetDate;
    }

    public String getEligibilityResetUserFirstName() {
        return eligibilityResetUserFirstName;
    }

    public void setEligibilityResetUserFirstName(String eligibilityResetUserFirstName) {
        this.eligibilityResetUserFirstName = eligibilityResetUserFirstName;
    }

    public String getEligibilityResetUserLastName() {
        return eligibilityResetUserLastName;
    }

    public void setEligibilityResetUserLastName(String eligibilityResetUserLastName) {
        this.eligibilityResetUserLastName = eligibilityResetUserLastName;
    }
}


