package org.innovateuk.ifs.project.finance.resource;

import java.time.LocalDate;

/**
 * Resource to hold the Viability details
 */
public class ViabilityResource {

    private ViabilityState viability;
    private ViabilityRagStatus viabilityRagStatus;

    private String viabilityApprovalUserFirstName;
    private String viabilityApprovalUserLastName;
    private LocalDate viabilityApprovalDate;

    private String viabilityResetUserFirstName;
    private String viabilityResetUserLastName;
    private LocalDate viabilityResetDate;

    // for JSON marshalling
    ViabilityResource() {
    }

    public ViabilityResource(ViabilityState viability, ViabilityRagStatus viabilityRagStatus) {
        this.viability = viability;
        this.viabilityRagStatus = viabilityRagStatus;
    }

    public ViabilityState getViability() {
        return viability;
    }

    public void setViability(ViabilityState viability) {
        this.viability = viability;
    }

    public ViabilityRagStatus getViabilityRagStatus() {
        return viabilityRagStatus;
    }

    public void setViabilityRagStatus(ViabilityRagStatus viabilityRagStatus) {
        this.viabilityRagStatus = viabilityRagStatus;
    }
    public String getViabilityApprovalUserFirstName() {
        return viabilityApprovalUserFirstName;
    }

    public void setViabilityApprovalUserFirstName(String viabilityApprovalUserFirstName) {
        this.viabilityApprovalUserFirstName = viabilityApprovalUserFirstName;
    }

    public String getViabilityApprovalUserLastName() {
        return viabilityApprovalUserLastName;
    }

    public void setViabilityApprovalUserLastName(String viabilityApprovalUserLastName) {
        this.viabilityApprovalUserLastName = viabilityApprovalUserLastName;
    }

    public LocalDate getViabilityApprovalDate() {
        return viabilityApprovalDate;
    }

    public void setViabilityApprovalDate(LocalDate viabilityApprovalDate) {
        this.viabilityApprovalDate = viabilityApprovalDate;
    }

    public LocalDate getViabilityResetDate() {
        return viabilityResetDate;
    }

    public void setViabilityResetDate(LocalDate viabilityResetDate) {
        this.viabilityResetDate = viabilityResetDate;
    }

    public String getViabilityResetUserFirstName() {
        return viabilityResetUserFirstName;
    }

    public void setViabilityResetUserFirstName(String viabilityResetUserFirstName) {
        this.viabilityResetUserFirstName = viabilityResetUserFirstName;
    }

    public String getViabilityResetUserLastName() {
        return viabilityResetUserLastName;
    }

    public void setViabilityResetUserLastName(String viabilityResetUserLastName) {
        this.viabilityResetUserLastName = viabilityResetUserLastName;
    }
}
