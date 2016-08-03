package com.worth.ifs.finance.spendprofile.partnerorganisation.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.user.resource.OrganisationResource;

/**
 * View model backing the internal Finance Team members view of the Spend Profile summary page
 */
public class PartnerOrganisationSpendProfileViewModel {

    private Long projectId;
    private Long partnerOrganisationId;
    private String partnerOrganisationName;
    private CompetitionSummaryResource competitionSummary;

    public PartnerOrganisationSpendProfileViewModel(Long projectId, OrganisationResource partnerOrganisation, CompetitionSummaryResource competitionSummary) {
        this.projectId = projectId;
        this.partnerOrganisationId = partnerOrganisation.getId();
        this.partnerOrganisationName = partnerOrganisation.getName();
        this.competitionSummary = competitionSummary;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getPartnerOrganisationId() {
        return partnerOrganisationId;
    }

    public String getPartnerOrganisationName() {
        return partnerOrganisationName;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }
}
