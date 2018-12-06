package org.innovateuk.ifs.application.forms.sections.yourorganisation.form;

import org.innovateuk.ifs.finance.resource.OrganisationSize;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.Collections.emptyList;

/**
 * Form used to capture "Your organisation" information
 */
public class YourOrganisationForm {

    private LocalDateTime financialYearEnd;
    private Long headCountAtLastFinancialYear;
    private OrganisationSize organisationSize;
    private Long turnover;
    private Long headCount;
    private Boolean stateAidAgreed;
    private List<GrowthTableRow> growthTableRows;
    private boolean growthTableIncluded;

    private YourOrganisationForm(
            OrganisationSize organisationSize,
            Long turnover,
            Long headCount,
            Boolean stateAidAgreed,
            List<GrowthTableRow> growthTableRows,
            LocalDateTime financialYearEnd,
            Long headCountAtLastFinancialYear,
            boolean growthTableIncluded) {

        this.organisationSize = organisationSize;
        this.turnover = turnover;
        this.headCount = headCount;
        this.stateAidAgreed = stateAidAgreed;
        this.growthTableRows = growthTableRows;
        this.financialYearEnd = financialYearEnd;
        this.headCountAtLastFinancialYear = headCountAtLastFinancialYear;
        this.growthTableIncluded = growthTableIncluded;
    }

    public OrganisationSize getOrganisationSize() {
        return organisationSize;
    }

    public void setOrganisationSize(OrganisationSize organisationSize) {
        this.organisationSize = organisationSize;
    }

    public Long getTurnover() {
        return turnover;
    }

    public void setTurnover(Long turnover) {
        this.turnover = turnover;
    }

    public Long getHeadCount() {
        return headCount;
    }

    public void setHeadCount(Long headCount) {
        this.headCount = headCount;
    }

    public Boolean getStateAidAgreed() {
        return stateAidAgreed;
    }

    public void setStateAidAgreed(Boolean stateAidAgreed) {
        this.stateAidAgreed = stateAidAgreed;
    }

    public List<GrowthTableRow> getGrowthTableRows() {
        return growthTableRows;
    }

    public LocalDateTime getFinancialYearEnd() {
        return financialYearEnd;
    }

    public void setFinancialYearEnd(LocalDateTime financialYearEnd) {
        this.financialYearEnd = financialYearEnd;
    }

    public Long getHeadCountAtLastFinancialYear() {
        return headCountAtLastFinancialYear;
    }

    public void setHeadCountAtLastFinancialYear(Long headCountAtLastFinancialYear) {
        this.headCountAtLastFinancialYear = headCountAtLastFinancialYear;
    }

    public boolean isGrowthTableIncluded() {
        return growthTableIncluded;
    }

    public static YourOrganisationForm noGrowthTable(OrganisationSize organisationSize, Long turnover, Long headCount, Boolean stateAidAgreed) {
        return new YourOrganisationForm(organisationSize, turnover, headCount, stateAidAgreed, emptyList(), null, null, false);
    }

    public static YourOrganisationForm withGrowthTable(OrganisationSize organisationSize, Long headCount, Boolean stateAidAgreed, List<GrowthTableRow> growthTableRows, LocalDateTime financialYearEnd, Long headCountAtLastFinancialYear) {
        return new YourOrganisationForm(organisationSize, null, headCount, stateAidAgreed, growthTableRows, financialYearEnd, headCountAtLastFinancialYear, true);
    }
}