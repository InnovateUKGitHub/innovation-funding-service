package com.worth.ifs.project.finance.resource;

/**
 * A resource object to return finance check status for a partner organisation
 */
public class FinanceCheckPartnerStatusResource {
    private Long id;
    private String name;
    private Eligibility eligibility;

    public FinanceCheckPartnerStatusResource() {
    }

    public FinanceCheckPartnerStatusResource(Long id, String name, Eligibility eligibility) {
        this.id = id;
        this.name = name;
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


    public enum Eligibility {
        REVIEW,
        APPROVED,
    }
}
