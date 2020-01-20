package org.innovateuk.ifs.project.organisationsize.form;

import org.innovateuk.ifs.application.forms.sections.yourorganisation.validation.constraints.LastFinancialYearEnd;
import org.innovateuk.ifs.finance.resource.OrganisationSize;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.YearMonth;

public class ProjectOrganisationSizeWithGrowthTableForm {

    @NotNull(message = "{validation.yourorganisation.organisation.size.required}")
    private OrganisationSize organisationSize;

    @LastFinancialYearEnd(messageNotNull = "{validation.standard.mm.yyyy.format}",
            messagePastYearMonth = "{validation.standard.past.mm.yyyy.not.past.format}",
            messagePositiveYearMonth = "{validation.standard.mm.yyyy.format}")
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

    public ProjectOrganisationSizeWithGrowthTableForm(OrganisationSize organisationSize, YearMonth financialYearEnd,
                                                      Long headCountAtLastFinancialYear,
                                                      BigDecimal annualTurnoverAtLastFinancialYear,
                                                      BigDecimal annualProfitsAtLastFinancialYear,
                                                      BigDecimal annualExportAtLastFinancialYear,
                                                      BigDecimal researchAndDevelopmentSpendAtLastFinancialYear) {
        this.organisationSize = organisationSize;
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

    public YearMonth getFinancialYearEnd() {
        return financialYearEnd;
    }

    public Long getHeadCountAtLastFinancialYear() {
        return headCountAtLastFinancialYear;
    }

    public BigDecimal getAnnualTurnoverAtLastFinancialYear() {
        return annualTurnoverAtLastFinancialYear;
    }

    public BigDecimal getAnnualProfitsAtLastFinancialYear() {
        return annualProfitsAtLastFinancialYear;
    }

    public BigDecimal getAnnualExportAtLastFinancialYear() {
        return annualExportAtLastFinancialYear;
    }

    public BigDecimal getResearchAndDevelopmentSpendAtLastFinancialYear() {
        return researchAndDevelopmentSpendAtLastFinancialYear;
    }
}
