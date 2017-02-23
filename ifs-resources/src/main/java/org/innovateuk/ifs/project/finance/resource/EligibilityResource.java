package org.innovateuk.ifs.project.finance.resource;

import java.time.LocalDate;

/**
 * Resource to hold the Eligibility details
 */
public class EligibilityResource {

    private Eligibility eligibility;
    private EligibilityRagStatus eligibilityRagStatus;

    private String eligibilityApprovalUserFirstName;
    private String eligibilityApprovalUserLastName;
    private LocalDate eligibilityApprovalDate;

    // for JSON marshalling
    EligibilityResource() {
    }

    public EligibilityResource(Eligibility eligibility, EligibilityRagStatus eligibilityRagStatus) {
        this.eligibility = eligibility;
        this.eligibilityRagStatus = eligibilityRagStatus;
    }

    public Eligibility getEligibility() {
        return eligibility;
    }

    public void setEligibility(Eligibility eligibility) {
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
}


