package org.innovateuk.ifs.finance.resource;

import java.time.YearMonth;
import java.util.List;

public class OrganisationFinancesKtpYearsResource extends AbstractOrganisationFinanceResource {

    private List<KtpYearResource> years;

    private Long groupEmployees;

    private YearMonth financialYearEnd;

    public OrganisationFinancesKtpYearsResource() {
    }

    public OrganisationFinancesKtpYearsResource(OrganisationSize organisationSize, List<KtpYearResource> years, Long groupEmployees, YearMonth financialYearEnd) {
        super(organisationSize);
        this.years = years;
        this.groupEmployees = groupEmployees;
        this.financialYearEnd = financialYearEnd;
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

    public YearMonth getFinancialYearEnd() {
        return financialYearEnd;
    }

    public void setFinancialYearEnd(YearMonth financialYearEnd) {
        this.financialYearEnd = financialYearEnd;
    }
}
