package org.innovateuk.ifs.project.finance.resource;

import java.time.LocalDate;

/**
 * Resource to hold the Viability details
 */
public class ViabilityResource {

    private Viability viability;
    private ViabilityRagStatus viabilityRagStatus;

    private String viabilityApprovalUserFirstName;
    private String viabilityApprovalUserLastName;
    private LocalDate viabilityApprovalDate;

    // for JSON marshalling
    ViabilityResource() {
    }

    public ViabilityResource(Viability viability, ViabilityRagStatus viabilityRagStatus) {
        this.viability = viability;
        this.viabilityRagStatus = viabilityRagStatus;
    }

    public Viability getViability() {
        return viability;
    }

    public void setViability(Viability viability) {
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
}
