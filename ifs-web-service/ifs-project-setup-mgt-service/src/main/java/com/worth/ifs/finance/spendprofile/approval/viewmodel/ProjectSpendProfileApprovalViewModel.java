package com.worth.ifs.finance.spendprofile.approval.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.util.List;

/**
 * View model backing the internal members view of the Spend Profile approval page
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectSpendProfileApprovalViewModel that = (ProjectSpendProfileApprovalViewModel) o;

        return new EqualsBuilder()
                .append(competitionSummary, that.competitionSummary)
                .append(leadTechnologist, that.leadTechnologist)
                .append(isApproved, that.isApproved)
                .append(isRejected, that.isRejected)
                .append(isNotApprovedOrRejected, that.isNotApprovedOrRejected)
                .append(organisations, that.organisations)
                .isEquals();
    }
}
