package org.innovateuk.ifs.project.finance.resource;

import java.math.BigDecimal;

/**
 * Resource for the summaries table.
 */
public class FinanceCheckSummariesResource {
    private FinanceCheckEligibilityResource financeCheckEligibilityResource;
    private String organisationName;
    private boolean isLeadOrganisation;

    public FinanceCheckSummariesResource() {}

    public FinanceCheckSummariesResource(FinanceCheckEligibilityResource financeCheckEligibilityResource, String organisationName, boolean isLeadOrganisation) {
        this.financeCheckEligibilityResource = financeCheckEligibilityResource;
        this.organisationName = organisationName;
        this.isLeadOrganisation = isLeadOrganisation;
    }

    public FinanceCheckEligibilityResource getFinanceCheckEligibilityResource() {
        return financeCheckEligibilityResource;
    }

    public void setFinanceCheckEligibilityResource(FinanceCheckEligibilityResource financeCheckEligibilityResource) {
        this.financeCheckEligibilityResource = financeCheckEligibilityResource;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public void setOrganisationName(String organisationName) {
        this.organisationName = organisationName;
    }

    public boolean isLeadOrganisation() {
        return isLeadOrganisation;
    }

    public void setLeadOrganisation(boolean leadOrganisation) {
        isLeadOrganisation = leadOrganisation;
    }
}
