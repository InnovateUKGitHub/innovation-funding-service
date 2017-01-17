package org.innovateuk.ifs.project.finance.resource;

/**
 * A resource object to return finance check status for a partner organisation
 */
public class FinanceCheckPartnerStatusResource {
    private Long id;
    private String name;
    private Viability viability;
    private ViabilityStatus viabilityRagStatus;
    private Eligibility eligibility;
    private EligibilityStatus eligibilityRagStatus;

    public FinanceCheckPartnerStatusResource() {
    }

    public FinanceCheckPartnerStatusResource(Long id, String name, Viability viability, ViabilityStatus viabilityRagStatus, Eligibility eligibility, EligibilityStatus eligibilityRagStatus) {
        this.id = id;
        this.name = name;
        this.viability = viability;
        this.viabilityRagStatus = viabilityRagStatus;
        this.eligibility = eligibility;
        this.eligibilityRagStatus = eligibilityRagStatus;
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

    public Eligibility getEligibility() {
        return eligibility;
    }

    public void setEligibility(Eligibility eligibility) {
        this.eligibility = eligibility;
    }

    public EligibilityStatus getEligibilityRagStatus() { return eligibilityRagStatus; }

    public void setEligibilityRagStatus(EligibilityStatus eligibilityRagStatus) {
        this.eligibilityRagStatus = eligibilityRagStatus;
    }

    public Viability getViability() {
        return viability;
    }

    public void setViability(Viability viability) {
        this.viability = viability;
    }

    public ViabilityStatus getViabilityRagStatus() {
        return viabilityRagStatus;
    }

    public void setViabilityRagStatus(ViabilityStatus viabilityRagStatus) {
        this.viabilityRagStatus = viabilityRagStatus;
    }
}
