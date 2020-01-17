package org.innovateuk.ifs.project.organisationdetails.viewmodel;

import java.math.BigDecimal;
import java.time.YearMonth;
import org.innovateuk.ifs.address.resource.AddressResource;
import org.innovateuk.ifs.finance.resource.OrganisationFinancesWithGrowthTableResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

public class OrganisationDetailsViewModel {

    // Details
    private final long projectId;
    private final long competitionId;
    private final String projectName;
    private final String organisationType;
    private final String organisationName;
    private final String registrationNumber;
    private final String addressLine1;
    private final String addressLine2;
    private final String addressLine3;
    private final String town;
    private final String county;
    private final String postcode;

    // Shared
    private final String organisationSize;
    private final BigDecimal annualTurnover;
    private final long employees;

    // If includes Growth table
    private YearMonth endOfLastFinancialYear;
    private final boolean includeGrowthTable;
    private BigDecimal annualProfit;
    private BigDecimal annualExport;
    private BigDecimal researchAndDevelopmentSpend;

    private final String previousPage;
    private String backLink;
    private final String projectDetailsLink;
    private final String selectProjectPartnerLink;

    public OrganisationDetailsViewModel(long projectId,
                                        long competitionId,
                                        String projectName,
                                        OrganisationResource organisation,
                                        boolean includeGrowthTable,
                                        boolean hasPartners,
                                        OrganisationFinancesWithGrowthTableResource finances,
                                        AddressResource addressResource) {
        this.projectId = projectId;
        this.competitionId = competitionId;
        this.organisationType = organisation.getOrganisationTypeName();
        this.projectName = projectName;
        this.organisationName = organisation.getName();
        this.registrationNumber = organisation.getCompaniesHouseNumber();
        this.addressLine1 = addressResource.getAddressLine1() == null ? "" : addressResource.getAddressLine1();
        this.addressLine2 = addressResource.getAddressLine2() == null ? "" : addressResource.getAddressLine2();
        this.addressLine3 = addressResource.getAddressLine3() == null ? "" : addressResource.getAddressLine3();
        this.town = addressResource.getTown();
        this.county = addressResource.getTown();
        this.postcode = addressResource.getPostcode();

        this.organisationSize = finances.getOrganisationSize().getDescription();
        this.annualTurnover = finances.getAnnualTurnoverAtLastFinancialYear();
        this.employees = finances.getHeadCountAtLastFinancialYear();

        this.includeGrowthTable = includeGrowthTable;
        if(includeGrowthTable) {
            this.endOfLastFinancialYear = finances.getFinancialYearEnd();
            this.annualProfit = finances.getAnnualProfitsAtLastFinancialYear();
            this.annualExport = finances.getAnnualExportAtLastFinancialYear();
            this.researchAndDevelopmentSpend = finances.getResearchAndDevelopmentSpendAtLastFinancialYear();
        }

        projectDetailsLink = String.format("/competition/%d/project/%d/details", competitionId, projectId);
        selectProjectPartnerLink = String.format("/competition/%d/project/%d/details/organisation/select", competitionId, projectId);

        this.previousPage = hasPartners == true ? "partner details" : "project details";
        this.backLink = hasPartners == true ?  projectDetailsLink : selectProjectPartnerLink;
    }

    public String getBackLink() {
        return backLink;
    }

    public String getPreviousPage() {
        return previousPage;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public BigDecimal getAnnualProfit() {
        return annualProfit;
    }

    public BigDecimal getAnnualExport() {
        return annualExport;
    }

    public BigDecimal getAnnualTurnover() {
        return annualTurnover;
    }

    public long getEmployees() {
        return employees;
    }

    public BigDecimal getResearchAndDevelopmentSpend() {
        return researchAndDevelopmentSpend;
    }

    public boolean isIncludeGrowthTable() {
        return includeGrowthTable;
    }

    public String getOrganisationSize() {
        return organisationSize;
    }

    public YearMonth getEndOfLastFinancialYear() {
        return endOfLastFinancialYear;
    }

    public long getProjectId() {
        return projectId;
    }

    public String getOrganisationType() {
        return organisationType;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getOrganisationName() {
        return organisationName;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public String getAddressLine3() {
        return addressLine3;
    }

    public String getTown() {
        return town;
    }

    public String getCounty() {
        return county;
    }

    public String getPostcode() {
        return postcode;
    }

    public boolean getReadOnly() {
        return true;
    }
}