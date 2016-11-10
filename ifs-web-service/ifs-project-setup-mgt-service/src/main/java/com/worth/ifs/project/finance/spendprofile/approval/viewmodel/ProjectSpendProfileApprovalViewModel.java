package com.worth.ifs.project.finance.spendprofile.approval.viewmodel;

import com.worth.ifs.application.resource.CompetitionSummaryResource;
import com.worth.ifs.project.resource.ApprovalType;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.util.List;

/**
 * View model backing the internal members view of the Spend Profile approval page
 */
public class ProjectSpendProfileApprovalViewModel {

    private CompetitionSummaryResource competitionSummary;
    private String leadTechnologist;
    private ApprovalType approvalType;
    private List<OrganisationResource> organisations;

    public ProjectSpendProfileApprovalViewModel(CompetitionSummaryResource competitionSummary,
                                                String leadTechnologist,
                                                ApprovalType approvalType,
                                                List<OrganisationResource> organisations) {
        this.competitionSummary = competitionSummary;
        this.leadTechnologist = leadTechnologist;
        this.approvalType = approvalType;
        this.organisations = organisations;
    }

    public CompetitionSummaryResource getCompetitionSummary() {
        return competitionSummary;
    }

    public String getLeadTechnologist() {
        return leadTechnologist;
    }

    public Boolean getEmpty() {
        return ApprovalType.EMPTY.equals(approvalType);
    }

    public Boolean getApproved() {
        return ApprovalType.APPROVED.equals(approvalType);
    }

    public Boolean getRejected() {
        return ApprovalType.REJECTED.equals(approvalType);
    }

    public Boolean getNotApprovedOrRejected() {
        return ApprovalType.UNSET.equals(approvalType);
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
                .append(approvalType, that.approvalType)
                .append(organisations, that.organisations)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionSummary)
                .append(leadTechnologist)
                .append(approvalType)
                .append(organisations)
                .toHashCode();
    }
}
