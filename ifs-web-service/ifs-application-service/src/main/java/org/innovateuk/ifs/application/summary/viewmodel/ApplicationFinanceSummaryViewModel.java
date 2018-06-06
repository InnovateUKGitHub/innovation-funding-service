package org.innovateuk.ifs.application.summary.viewmodel;

import org.innovateuk.ifs.application.resource.ApplicationResource;
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

    public ApplicationFinanceSummaryViewModel(ApplicationResource currentApplication,
                                              Boolean hasFinanceSection,
                                              Map<FinanceRowType, BigDecimal> financeTotalPerType,
                                              List<OrganisationResource> applicationOrganisations,
                                              Set<Long> sectionsMarkedAsComplete,
                                              Long financeSectionId,
                                              OrganisationResource leadOrganisation) {
        this.currentApplication = currentApplication;
        this.hasFinanceSection = hasFinanceSection;
        this.financeTotalPerType = financeTotalPerType;
        this.applicationOrganisations = applicationOrganisations;
        this.sectionsMarkedAsComplete = sectionsMarkedAsComplete;
        this.financeSectionId = financeSectionId;
        this.leadOrganisation = leadOrganisation;
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
}
