package org.innovateuk.ifs.finance.resource;

import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * A class used to capture "Your organisation" information including a growth table
 */
public class OrganisationFinancesWithGrowthTableResource {

    private OrganisationSize organisationSize;
    private Boolean stateAidAgreed;
    private YearMonth financialYearEnd;
    private Integer headCountAtLastFinancialYear;
    private BigDecimal annualTurnoverAtLastFinancialYear;
    private BigDecimal annualProfitsAtLastFinancialYear;
    private BigDecimal annualExportAtLastFinancialYear;
    private BigDecimal researchAndDevelopmentSpendAtLastFinancialYear;

    public OrganisationFinancesWithGrowthTableResource() {
    }

    public OrganisationFinancesWithGrowthTableResource(OrganisationSize organisationSize, Boolean stateAidAgreed, YearMonth financialYearEnd, Integer headCountAtLastFinancialYear, BigDecimal annualTurnoverAtLastFinancialYear, BigDecimal annualProfitsAtLastFinancialYear, BigDecimal annualExportAtLastFinancialYear, BigDecimal researchAndDevelopmentSpendAtLastFinancialYear) {
        this.organisationSize = organisationSize;
        this.stateAidAgreed = stateAidAgreed;
        this.financialYearEnd = financialYearEnd;
        this.headCountAtLastFinancialYear = headCountAtLastFinancialYear;
        this.annualTurnoverAtLastFinancialYear = annualTurnoverAtLastFinancialYear;
        this.annualProfitsAtLastFinancialYear = annualProfitsAtLastFinancialYear;
        this.annualExportAtLastFinancialYear = annualExportAtLastFinancialYear;
        this.researchAndDevelopmentSpendAtLastFinancialYear = researchAndDevelopmentSpendAtLastFinancialYear;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public Boolean getStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(Boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
    }

    public YearMonth getFinancialYearEnd() {
        return financialYearEnd;
    }

    public void setFinancialYearEnd(YearMonth financialYearEnd) {
        this.financialYearEnd = financialYearEnd;
    }

    public Integer getHeadCountAtLastFinancialYear() {
        return headCountAtLastFinancialYear;
    }

    public void setHeadCountAtLastFinancialYear(Integer headCountAtLastFinancialYear) {
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
}
