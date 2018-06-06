package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ApplicationFinanceSummaryViewModel {

    private final ApplicationResource currentApplication;
    private final Boolean hasFinanceSection;
    private final Map<FinanceRowType, BigDecimal> financeTotalPerType;
    private final List<OrganisationResource> applicationOrganisations;
    private final Set<Long> sectionsMarkedAsComplete;
    private final Long financeSectionId;
    private final OrganisationResource leadOrganisation;
    private final CompetitionResource currentCompetition;
    private final OrganisationResource userOrganisation;
    private final Map<Long, BaseFinanceResource> organisationFinances;
    private final BigDecimal totalFundingSought;
    private final BigDecimal totalOtherFunding;
    private final BigDecimal totalContribution;
    private final BigDecimal financeTotal;

    public ApplicationFinanceSummaryViewModel(ApplicationResource currentApplication,
                                              Boolean hasFinanceSection,
                                              Map<FinanceRowType, BigDecimal> financeTotalPerType,
                                              List<OrganisationResource> applicationOrganisations,
                                              Set<Long> sectionsMarkedAsComplete,
                                              Long financeSectionId,
                                              OrganisationResource leadOrganisation,
                                              CompetitionResource currentCompetition,
                                              OrganisationResource userOrganisation,
                                              Map<Long, BaseFinanceResource> organisationFinances,
                                              BigDecimal totalFundingSought,
                                              BigDecimal totalOtherFunding,
                                              BigDecimal totalContribution,
                                              BigDecimal financeTotal) {
        this.currentApplication = currentApplication;
        this.hasFinanceSection = hasFinanceSection;
        this.financeTotalPerType = financeTotalPerType;
        this.applicationOrganisations = applicationOrganisations;
        this.sectionsMarkedAsComplete = sectionsMarkedAsComplete;
        this.financeSectionId = financeSectionId;
        this.leadOrganisation = leadOrganisation;
        this.currentCompetition = currentCompetition;
        this.userOrganisation = userOrganisation;
        this.organisationFinances = organisationFinances;
        this.totalFundingSought = totalFundingSought;
        this.totalOtherFunding = totalOtherFunding;
        this.totalContribution = totalContribution;
        this.financeTotal = financeTotal;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public Boolean isHasFinanceSection() {
        return hasFinanceSection;
    }

    public Map<FinanceRowType, BigDecimal> getFinanceTotalPerType() {
        return financeTotalPerType;
    }

    public List<OrganisationResource> getApplicationOrganisations() {
        return applicationOrganisations;
    }

    public Set<Long> getSectionsMarkedAsComplete() {
        return sectionsMarkedAsComplete;
    }

    public Boolean getHasFinanceSection() {
        return hasFinanceSection;
    }

    public Long getFinanceSectionId() {
        return financeSectionId;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }

    public OrganisationResource getUserOrganisation() {
        return userOrganisation;
    }

    public Map<Long, BaseFinanceResource> getOrganisationFinances() {
        return organisationFinances;
    }

    public BigDecimal getTotalFundingSought() {
        return totalFundingSought;
    }

    public BigDecimal getTotalOtherFunding() {
        return totalOtherFunding;
    }

    public BigDecimal getTotalContribution() {
        return totalContribution;
    }

    public BigDecimal getFinanceTotal() {
        return financeTotal;
    }
}
