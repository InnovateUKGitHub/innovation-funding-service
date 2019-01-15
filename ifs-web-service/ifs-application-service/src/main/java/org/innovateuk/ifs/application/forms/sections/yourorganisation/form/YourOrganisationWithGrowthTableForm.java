package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.commons.validation.constraints.PastYearMonth;
import org.innovateuk.ifs.commons.validation.constraints.PositiveYearMonth;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.validation.constraints.NotNull;
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
    private Long annualTurnoverAtLastFinancialYear;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Long annualProfitsAtLastFinancialYear;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Long annualExportAtLastFinancialYear;

    @NotNull(message = "{validation.field.must.not.be.blank}")
    private Long researchAndDevelopmentSpendAtLastFinancialYear;

    YourOrganisationWithGrowthTableForm(
            OrganisationSize organisationSize,
            Boolean stateAidAgreed,
            YearMonth financialYearEnd,
            Long headCountAtLastFinancialYear,
            Long annualTurnoverAtLastFinancialYear,
            Long annualProfitsAtLastFinancialYear,
            Long annualExportAtLastFinancialYear,
            Long researchAndDevelopmentSpendAtLastFinancialYear) {

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

    public Long getAnnualTurnoverAtLastFinancialYear() {
        return annualTurnoverAtLastFinancialYear;
    }

    public void setAnnualTurnoverAtLastFinancialYear(Long annualTurnoverAtLastFinancialYear) {
        this.annualTurnoverAtLastFinancialYear = annualTurnoverAtLastFinancialYear;
    }

    public Long getAnnualProfitsAtLastFinancialYear() {
        return annualProfitsAtLastFinancialYear;
    }

    public void setAnnualProfitsAtLastFinancialYear(Long annualProfitsAtLastFinancialYear) {
        this.annualProfitsAtLastFinancialYear = annualProfitsAtLastFinancialYear;
    }

    public Long getAnnualExportAtLastFinancialYear() {
        return annualExportAtLastFinancialYear;
    }

    public void setAnnualExportAtLastFinancialYear(Long annualExportAtLastFinancialYear) {
        this.annualExportAtLastFinancialYear = annualExportAtLastFinancialYear;
    }

    public Long getResearchAndDevelopmentSpendAtLastFinancialYear() {
        return researchAndDevelopmentSpendAtLastFinancialYear;
    }

    public void setResearchAndDevelopmentSpendAtLastFinancialYear(Long researchAndDevelopmentSpendAtLastFinancialYear) {
        this.researchAndDevelopmentSpendAtLastFinancialYear = researchAndDevelopmentSpendAtLastFinancialYear;
    }
}