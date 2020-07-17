package org.innovateuk.ifs.finance.resource;

import java.util.List;

public class OrganisationFinancesKtpYearsResource extends AbstractOrganisationFinanceResource {

    private List<KtpYearResource> years;

    private Long groupEmployees;

    public OrganisationFinancesKtpYearsResource() {
    }

    public OrganisationFinancesKtpYearsResource(OrganisationSize organisationSize, List<KtpYearResource> years, Long groupEmployees) {
        super(organisationSize);
        this.years = years;
        this.groupEmployees = groupEmployees;
    }

    public List<KtpYearResource> getYears() {
        return years;
    }

    public void setYears(List<KtpYearResource> years) {
        this.years = years;
    }

    public Long getGroupEmployees() {
        return groupEmployees;
    }

    public void setGroupEmployees(Long groupEmployees) {
        this.groupEmployees = groupEmployees;
    }
}
