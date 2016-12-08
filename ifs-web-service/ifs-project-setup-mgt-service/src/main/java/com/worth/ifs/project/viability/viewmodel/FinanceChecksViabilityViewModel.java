package com.worth.ifs.project.viability.viewmodel;

import com.worth.ifs.user.resource.OrganisationSize;

/**
 * View model for the Viability page
 */
public class FinanceChecksViabilityViewModel {

    private String organisationName;
    private boolean leadPartnerOrganisation;
    private String companyRegistrationNumber;

    // currently always null
    private Integer turnover;

    // currently always null
    private Integer headCount;

    private OrganisationSize organisationSize;

    private boolean creditReportVerified;
    private boolean viabilityApproved;

    public FinanceChecksViabilityViewModel(String organisationName, boolean leadPartnerOrganisation, String companyRegistrationNumber, Integer turnover, Integer headCount, OrganisationSize organisationSize, boolean creditReportVerified, boolean viabilityApproved) {
        this.organisationName = organisationName;
        this.leadPartnerOrganisation = leadPartnerOrganisation;
        this.companyRegistrationNumber = companyRegistrationNumber;
        this.turnover = turnover;
        this.headCount = headCount;
        this.organisationSize = organisationSize;
        this.creditReportVerified = creditReportVerified;
        this.viabilityApproved = viabilityApproved;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public boolean isLeadPartnerOrganisation() {
        return leadPartnerOrganisation;
    }

    public String getCompanyRegistrationNumber() {
        return companyRegistrationNumber;
    }

    public Integer getTurnover() {
        return turnover;
    }

    public Integer getHeadCount() {
        return headCount;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public boolean isCreditReportVerified() {
        return creditReportVerified;
    }

    public boolean isViabilityApproved() {
        return viabilityApproved;
    }
}
