package com.worth.ifs.finance.spendprofile.approval.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.List;

/**
 * TODO
 */
public class ProjectSpendProfileApprovalViewModel {

    private CompetitionSummaryResource competitionSummary;
    private String leadTechnologist;
    private Boolean isApproved;
    private Boolean isRejected;
    private Boolean isNotApprovedOrRejected;
    private List<OrganisationResource> organisations;

    public ProjectSpendProfileApprovalViewModel(CompetitionSummaryResource competitionSummary,
                                                String leadTechnologist,
                                                Boolean isApproved, Boolean isRejected, Boolean isNotApprovedOrRejected,
                                                List<OrganisationResource> organisations) {
        this.competitionSummary = competitionSummary;
        this.leadTechnologist = leadTechnologist;
        this.isApproved = isApproved;
        this.isRejected = isRejected;
        this.isNotApprovedOrRejected = isNotApprovedOrRejected;
        this.organisations = organisations;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }

    public String getLeadTechnologist() {
        return leadTechnologist;
    }

    public Boolean getApproved() {
        return isApproved;
    }

    public Boolean getRejected() {
        return isRejected;
    }

    public Boolean getNotApprovedOrRejected() {
        return isNotApprovedOrRejected;
    }

    public List<OrganisationResource> getOrganisations() {
        return organisations;
    }
}
