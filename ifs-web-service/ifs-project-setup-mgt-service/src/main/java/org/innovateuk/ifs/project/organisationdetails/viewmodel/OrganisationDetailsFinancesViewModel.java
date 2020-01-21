package org.innovateuk.ifs.project.organisationdetails.viewmodel;

import java.math.BigDecimal;
import java.time.YearMonth;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithoutGrowthTableResource;

public class OrganisationDetailsFinancesViewModel {

    private final boolean includeGrowthTable;
    private final String organisationSize;
    private YearMonth endOfLastFinancialYear;
    private final BigDecimal annualTurnover;
    private BigDecimal annualProfit;
    private BigDecimal annualExport;
    private BigDecimal researchAndDevelopmentSpend;
    private final long employees;

    public OrganisationDetailsFinancesViewModel(OrganisationFinancesWithGrowthTableResource finances) {
        this.includeGrowthTable = true;

        organisationSize = finances.getOrganisationSize().getDescription();
        this.endOfLastFinancialYear = finances.getFinancialYearEnd();
        this.annualTurnover = finances.getAnnualTurnoverAtLastFinancialYear();
        this.annualProfit = finances.getAnnualProfitsAtLastFinancialYear();
        this.annualExport = finances.getAnnualExportAtLastFinancialYear();
        this.researchAndDevelopmentSpend = finances.getResearchAndDevelopmentSpendAtLastFinancialYear();
        this.employees = finances.getHeadCountAtLastFinancialYear();
    }

    public OrganisationDetailsFinancesViewModel(OrganisationFinancesWithoutGrowthTableResource finances) {
        this.includeGrowthTable = false;
        organisationSize = finances.getOrganisationSize().getDescription();
        this.annualTurnover = finances.getTurnover();
        this.employees = finances.getHeadCount();
    }

    public String getOrganisationSize() {
        return organisationSize;
    }

    public BigDecimal getAnnualTurnover() {
        return annualTurnover;
    }

    public long getEmployees() {
        return employees;
    }

    public BigDecimal getAnnualProfit() {
        return annualProfit;
    }

    public BigDecimal getAnnualExport() {
        return annualExport;
    }

    public BigDecimal getResearchAndDevelopmentSpend() {
        return researchAndDevelopmentSpend;
    }

    public boolean isIncludeGrowthTable() {
        return includeGrowthTable;
    }

    public YearMonth getEndOfLastFinancialYear() {
        return endOfLastFinancialYear;
    }
}