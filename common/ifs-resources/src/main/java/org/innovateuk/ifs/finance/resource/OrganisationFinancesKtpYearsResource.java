package org.innovateuk.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationFinancesKtpYearsResource that = (OrganisationFinancesKtpYearsResource) o;

        return new EqualsBuilder()
                .append(years, that.years)
                .append(groupEmployees, that.groupEmployees)
                .append(financialYearEnd, that.financialYearEnd)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(years)
                .append(groupEmployees)
                .append(financialYearEnd)
                .toHashCode();
    }
}
