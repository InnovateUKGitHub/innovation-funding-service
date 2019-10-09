package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.commons.validation.constraints.PastYearMonth;
import org.innovateuk.ifs.commons.validation.constraints.PositiveYearMonth;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.YearMonth;

/**
 * Form used to capture "Your organisation" information when a growth table is required.
 */
public class YourOrganisationWithGrowthTableForm {

    @NotNull(message = "{validation.yourorganisation.organisation.size.required}")
    private OrganisationSize organisationSize;

    private Boolean stateAidAgreed;

    @NotNull(message = "{validation.standard.mm.yyyy.format}")
    @PastYearMonth
    @PositiveYearMonth
    private YearMonth financialYearEnd;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Long headCountAtLastFinancialYear;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal annualTurnoverAtLastFinancialYear;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal annualProfitsAtLastFinancialYear;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal annualExportAtLastFinancialYear;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private BigDecimal researchAndDevelopmentSpendAtLastFinancialYear;

    YourOrganisationWithGrowthTableForm(
            OrganisationSize organisationSize,
            Boolean stateAidAgreed,
            YearMonth financialYearEnd,
            Long headCountAtLastFinancialYear,
            BigDecimal annualTurnoverAtLastFinancialYear,
            BigDecimal annualProfitsAtLastFinancialYear,
            BigDecimal annualExportAtLastFinancialYear,
            BigDecimal researchAndDevelopmentSpendAtLastFinancialYear) {

        this.organisationSize = organisationSize;
        this.stateAidAgreed = stateAidAgreed;
        this.financialYearEnd = financialYearEnd;
        this.headCountAtLastFinancialYear = headCountAtLastFinancialYear;
        this.annualTurnoverAtLastFinancialYear = annualTurnoverAtLastFinancialYear;
        this.annualProfitsAtLastFinancialYear = annualProfitsAtLastFinancialYear;
        this.annualExportAtLastFinancialYear = annualExportAtLastFinancialYear;
        this.researchAndDevelopmentSpendAtLastFinancialYear = researchAndDevelopmentSpendAtLastFinancialYear;
    }

    YourOrganisationWithGrowthTableForm() {
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
}