package org.innovateuk.ifs.application.common.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.finance.resource.BaseFinanceResource;
import org.innovateuk.ifs.finance.resource.cost.FinanceRowType;
import org.innovateuk.ifs.form.resource.SectionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * View model for finance/finance-summary :: finance-breakdown-table.
 */
public class ApplicationFundingBreakdownViewModel {

    private final Map<FinanceRowType, BigDecimal> financeTotalPerType;
    private final List<OrganisationResource> applicationOrganisations;
    private final SectionResource financeSection;
    private BigDecimal financeTotal;
    private final OrganisationResource leadOrganisation;
    private final Map<Long, BaseFinanceResource> organisationFinances;
    private final List<String> pendingOrganisationNames;
    private final ApplicationResource currentApplication;
    private final OrganisationResource userOrganisation;
    private final Map<Long, Boolean> showDetailedFinanceLink;
    private final boolean collaborativeProject;
    private final boolean applicant;
    private final boolean hasFinanceSection;
    private final Set<Long> sectionsMarkedAsComplete;
    private final Map<Long, Set<Long>> completedSectionsByOrganisation;
    private final boolean yourFinancesCompleteForAllOrganisations;
    private final CompetitionResource currentCompetition;

    public ApplicationFundingBreakdownViewModel(Map<FinanceRowType, BigDecimal> financeTotalPerType, List<OrganisationResource> applicationOrganisations, SectionResource financeSection, BigDecimal financeTotal, OrganisationResource leadOrganisation, Map<Long, BaseFinanceResource> organisationFinances, List<String> pendingOrganisationNames, ApplicationResource currentApplication, OrganisationResource userOrganisation, Map<Long, Boolean> showDetailedFinanceLink, boolean collaborativeProject, boolean applicant, Set<Long> sectionsMarkedAsComplete, Map<Long, Set<Long>> completedSectionsByOrganisation, boolean yourFinancesCompleteForAllOrganisations, CompetitionResource currentCompetition) {
        this.financeTotalPerType = financeTotalPerType;
        this.applicationOrganisations = applicationOrganisations;
        this.financeSection = financeSection;
        this.financeTotal = financeTotal;
        this.leadOrganisation = leadOrganisation;
        this.organisationFinances = organisationFinances;
        this.pendingOrganisationNames = pendingOrganisationNames;
        this.currentApplication = currentApplication;
        this.userOrganisation = userOrganisation;
        this.showDetailedFinanceLink = showDetailedFinanceLink;
        this.collaborativeProject = collaborativeProject;
        this.applicant = applicant;
        this.hasFinanceSection = financeSection != null;
        this.sectionsMarkedAsComplete = sectionsMarkedAsComplete;
        this.completedSectionsByOrganisation = completedSectionsByOrganisation;
        this.yourFinancesCompleteForAllOrganisations = yourFinancesCompleteForAllOrganisations;
        this.currentCompetition = currentCompetition;
    }

    public Map<FinanceRowType, BigDecimal> getFinanceTotalPerType() {
        return financeTotalPerType;
    }

    public List<OrganisationResource> getApplicationOrganisations() {
        return applicationOrganisations;
    }

    public SectionResource getFinanceSection() {
        return financeSection;
    }

    public BigDecimal getFinanceTotal() {
        return financeTotal;
    }

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public Map<Long, BaseFinanceResource> getOrganisationFinances() {
        return organisationFinances;
    }

    public List<String> getPendingOrganisationNames() {
        return pendingOrganisationNames;
    }

    public ApplicationResource getCurrentApplication() {
        return currentApplication;
    }

    public OrganisationResource getUserOrganisation() {
        return userOrganisation;
    }

    public Map<Long, Boolean> getShowDetailedFinanceLink() {
        return showDetailedFinanceLink;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
    }

    public boolean isApplicant() {
        return applicant;
    }

    public boolean isHasFinanceSection() {
        return hasFinanceSection;
    }

    public Set<Long> getSectionsMarkedAsComplete() {
        return sectionsMarkedAsComplete;
    }

    public Map<Long, Set<Long>> getCompletedSectionsByOrganisation() {
        return completedSectionsByOrganisation;
    }

    public boolean isYourFinancesCompleteForAllOrganisations() {
        return yourFinancesCompleteForAllOrganisations;
    }

    public CompetitionResource getCurrentCompetition() {
        return currentCompetition;
    }
}