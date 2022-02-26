package org.innovateuk.ifs.finance.resource;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.time.YearMonth;
import java.util.List;

@Getter
@Setter
public class OrganisationFinancesKtpYearsResource extends AbstractOrganisationFinanceResource {

    private Long userId;

    private List<KtpYearResource> years;

    private Long groupEmployees;

    private Boolean hasAdditionalInfoSection;

    private String additionalInfo;

    private YearMonth financialYearEnd;

    public OrganisationFinancesKtpYearsResource() {
    }

    public OrganisationFinancesKtpYearsResource(Long userId, OrganisationSize organisationSize, List<KtpYearResource> years, Boolean hasAdditionalInfoSection, String additionalInfo, Long groupEmployees, YearMonth financialYearEnd) {
        super(organisationSize);
        this.userId = userId;
        this.years = years;
        this.groupEmployees = groupEmployees;
        this.hasAdditionalInfoSection = hasAdditionalInfoSection;
        this.additionalInfo = additionalInfo;
        this.financialYearEnd = financialYearEnd;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationFinancesKtpYearsResource that = (OrganisationFinancesKtpYearsResource) o;

        return new EqualsBuilder()
                .append(userId, that.userId)
                .append(years, that.years)
                .append(groupEmployees, that.groupEmployees)
                .append(additionalInfo, that.additionalInfo)
                .append(financialYearEnd, that.financialYearEnd)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(userId)
                .append(years)
                .append(groupEmployees)
                .append(additionalInfo)
                .append(financialYearEnd)
                .toHashCode();
    }
}
