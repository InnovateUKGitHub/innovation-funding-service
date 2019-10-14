package org.innovateuk.ifs.project.projectteam.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.resource.ProjectResource;

public class ProjectPartnerInviteViewModel {

    private final long projectId;
    private final long applicationId;
    private final long competitionId;
    private final String projectName;

    public ProjectPartnerInviteViewModel(ProjectResource project) {
        this.projectId = project.getId();
        this.applicationId = project.getApplication();
        this.competitionId = project.getCompetition();
        this.projectName = project.getName();
    }

    public long getProjectId() {
        return projectId;
    }

    public long getCompetitionId() {
        return competitionId;
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectPartnerInviteViewModel that = (ProjectPartnerInviteViewModel) o;

        return new EqualsBuilder()
                .append(projectId, that.projectId)
                .append(applicationId, that.applicationId)
                .append(competitionId, that.competitionId)
                .append(projectName, that.projectName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(projectId)
                .append(applicationId)
                .append(competitionId)
                .append(projectName)
                .toHashCode();
    }
}
