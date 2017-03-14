package org.innovateuk.ifs.project.finance.resource;

/**
 * A resource object to return finance check status for a partner organisation
 */
public class FinanceCheckPartnerStatusResource {
    private Long id;
    private String name;

    private boolean isLead;
    private Viability viability;
    private ViabilityRagStatus viabilityRagStatus;
    private Eligibility eligibility;
    private EligibilityRagStatus eligibilityRagStatus;
    private boolean awaitingResponse;

    public FinanceCheckPartnerStatusResource() {
    }

    public FinanceCheckPartnerStatusResource(Long id, String name, boolean isLead, Viability viability,
                                             ViabilityRagStatus viabilityRagStatus, Eligibility eligibility,
                                             EligibilityRagStatus eligibilityRagStatus, boolean awaitingResponse) {
        this.id = id;
        this.name = name;
        this.isLead = isLead;
        this.viability = viability;
        this.viabilityRagStatus = viabilityRagStatus;
        this.eligibility = eligibility;
        this.eligibilityRagStatus = eligibilityRagStatus;
        this.awaitingResponse = awaitingResponse;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isLead() {
        return isLead;
    }

    public void setLead(boolean lead) {
        isLead = lead;
    }

    public Eligibility getEligibility() {
        return eligibility;
    }

    public void setEligibility(Eligibility eligibility) {
        this.eligibility = eligibility;
    }

    public EligibilityRagStatus getEligibilityRagStatus() { return eligibilityRagStatus; }

    public void setEligibilityRagStatus(EligibilityRagStatus eligibilityRagStatus) {
        this.eligibilityRagStatus = eligibilityRagStatus;
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

    public boolean isAwaitingResponse() {
        return awaitingResponse;
    }

    public void setAwaitingResponse(boolean awaitingResponse) {
        this.awaitingResponse = awaitingResponse;
    }
}
