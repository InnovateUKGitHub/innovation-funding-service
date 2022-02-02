package org.innovateuk.ifs.finance.resource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * A class used to capture "Your organisation" information including a growth table
 */
public class OrganisationFinancesWithGrowthTableResource extends AbstractOrganisationFinanceResource {

    private YearMonth financialYearEnd;
    private Long headCountAtLastFinancialYear;
    private BigDecimal annualTurnoverAtLastFinancialYear;
    private BigDecimal annualProfitsAtLastFinancialYear;
    private BigDecimal annualExportAtLastFinancialYear;
    private BigDecimal researchAndDevelopmentSpendAtLastFinancialYear;

    public OrganisationFinancesWithGrowthTableResource() {
    }

    public OrganisationFinancesWithGrowthTableResource(OrganisationSize organisationSize,
                                                       YearMonth financialYearEnd,
                                                       Long headCountAtLastFinancialYear,
                                                       BigDecimal annualTurnoverAtLastFinancialYear,
                                                       BigDecimal annualProfitsAtLastFinancialYear,
                                                       BigDecimal annualExportAtLastFinancialYear,
                                                       BigDecimal researchAndDevelopmentSpendAtLastFinancialYear) {
        super(organisationSize);
        this.financialYearEnd = financialYearEnd;
        this.headCountAtLastFinancialYear = headCountAtLastFinancialYear;
        this.annualTurnoverAtLastFinancialYear = annualTurnoverAtLastFinancialYear;
        this.annualProfitsAtLastFinancialYear = annualProfitsAtLastFinancialYear;
        this.annualExportAtLastFinancialYear = annualExportAtLastFinancialYear;
        this.researchAndDevelopmentSpendAtLastFinancialYear = researchAndDevelopmentSpendAtLastFinancialYear;
    }

    public YearMonth getFinancialYearEnd() {
        return financialYearEnd;
    }

    public void setFinancialYearEnd(YearMonth financialYearEnd) {
        this.financialYearEnd = financialYearEnd;
    }

    public Long getHeadCountAtLastFinancialYear() {
        return headCountAtLastFinancialYear;
    }

    public void setHeadCountAtLastFinancialYear(Long headCountAtLastFinancialYear) {
        this.headCountAtLastFinancialYear = headCountAtLastFinancialYear;
    }

    public BigDecimal getAnnualTurnoverAtLastFinancialYear() {
        return annualTurnoverAtLastFinancialYear;
    }

    public void setAnnualTurnoverAtLastFinancialYear(BigDecimal annualTurnoverAtLastFinancialYear) {
        this.annualTurnoverAtLastFinancialYear = annualTurnoverAtLastFinancialYear;
    }

    public BigDecimal getAnnualProfitsAtLastFinancialYear() {
        return annualProfitsAtLastFinancialYear;
    }

    public void setAnnualProfitsAtLastFinancialYear(BigDecimal annualProfitsAtLastFinancialYear) {
        this.annualProfitsAtLastFinancialYear = annualProfitsAtLastFinancialYear;
    }

    public BigDecimal getAnnualExportAtLastFinancialYear() {
        return annualExportAtLastFinancialYear;
    }

    public void setAnnualExportAtLastFinancialYear(BigDecimal annualExportAtLastFinancialYear) {
        this.annualExportAtLastFinancialYear = annualExportAtLastFinancialYear;
    }

    public BigDecimal getResearchAndDevelopmentSpendAtLastFinancialYear() {
        return researchAndDevelopmentSpendAtLastFinancialYear;
    }

    public void setResearchAndDevelopmentSpendAtLastFinancialYear(BigDecimal researchAndDevelopmentSpendAtLastFinancialYear) {
        this.researchAndDevelopmentSpendAtLastFinancialYear = researchAndDevelopmentSpendAtLastFinancialYear;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        OrganisationFinancesWithGrowthTableResource that = (OrganisationFinancesWithGrowthTableResource) o;

        return new EqualsBuilder()
                .append(financialYearEnd, that.financialYearEnd)
                .append(headCountAtLastFinancialYear, that.headCountAtLastFinancialYear)
                .append(annualTurnoverAtLastFinancialYear, that.annualTurnoverAtLastFinancialYear)
                .append(annualProfitsAtLastFinancialYear, that.annualProfitsAtLastFinancialYear)
                .append(annualExportAtLastFinancialYear, that.annualExportAtLastFinancialYear)
                .append(researchAndDevelopmentSpendAtLastFinancialYear, that.researchAndDevelopmentSpendAtLastFinancialYear)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(financialYearEnd)
                .append(headCountAtLastFinancialYear)
                .append(annualTurnoverAtLastFinancialYear)
                .append(annualProfitsAtLastFinancialYear)
                .append(annualExportAtLastFinancialYear)
                .append(researchAndDevelopmentSpendAtLastFinancialYear)
                .toHashCode();
    }
}
