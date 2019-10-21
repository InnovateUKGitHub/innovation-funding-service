package org.innovateuk.ifs.project.spendprofile.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.application.resource.CompetitionSummaryResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.ApprovalType;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.List;

/**
 * View model backing the internal members view of the Spend Profile approval page
 */
public class ProjectSpendProfileApprovalViewModel {

    private final CompetitionSummaryResource competitionSummary;
    private final String leadTechnologist;
    private final ApprovalType approvalType;
    private final List<OrganisationResource> organisations;
    private final Long applicationId;
    private final String projectName;
    private final boolean projectIsActive;
    private final boolean collaborativeProject;


    public ProjectSpendProfileApprovalViewModel(CompetitionSummaryResource competitionSummary,
                                                String leadTechnologist,
                                                ApprovalType approvalType,
                                                List<OrganisationResource> organisations,
                                                ProjectResource project) {
        this.competitionSummary = competitionSummary;
        this.leadTechnologist = leadTechnologist;
        this.approvalType = approvalType;
        this.organisations = organisations;
        this.applicationId = project.getApplication();
        this.projectName = project.getName();
        this.projectIsActive = project.getProjectState().isActive();
        this.collaborativeProject = project.isCollaborativeProject();
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

    public Long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    public boolean isProjectIsActive() {
        return projectIsActive;
    }

    public boolean isCollaborativeProject() {
        return collaborativeProject;
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
                .append(projectIsActive, that.projectIsActive)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(competitionSummary)
                .append(leadTechnologist)
                .append(approvalType)
                .append(organisations)
                .append(projectIsActive)
                .toHashCode();
    }
}
