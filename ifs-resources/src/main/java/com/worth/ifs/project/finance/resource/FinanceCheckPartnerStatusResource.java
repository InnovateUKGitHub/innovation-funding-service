package com.worth.ifs.project.finance.resource;

/**
 * A resource object to return finance check status for a partner organisation
 */
public class FinanceCheckPartnerStatusResource {
    private Long id;
    private String name;
    private Viability viability;
    private Eligibility eligibility;

    public FinanceCheckPartnerStatusResource() {
    }

    public FinanceCheckPartnerStatusResource(Long id, String name, Viability viability, Eligibility eligibility) {
        this.id = id;
        this.name = name;
        this.viability = viability;
        this.eligibility = eligibility;
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

    public Viability getViability() {
        return viability;
    }

    public void setViability(Viability viability) {
        this.viability = viability;
    }

    public enum Eligibility {
        REVIEW,
        APPROVED,
    }

    public enum Viability {
        REVIEW,
        APPROVED,
        NOT_APPLICABLE;

        public boolean isNotApplicable() {
            return this == NOT_APPLICABLE;
        }
    }
}
