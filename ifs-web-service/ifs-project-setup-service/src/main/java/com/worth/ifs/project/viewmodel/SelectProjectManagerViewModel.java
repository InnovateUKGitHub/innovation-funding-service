package com.worth.ifs.project.viewmodel;

import java.util.List;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * view model for the "select project manager contact" page
 */
public class SelectProjectManagerViewModel {
    private List<ProjectUserInviteModel> organisationUsers;
    private List<ProjectUserInviteModel> invitedUsers;
    private Long projectId;
    private Long currentUser;
    private ApplicationResource app;
    private CompetitionResource competition;
    private String projectName;

    public SelectProjectManagerViewModel(final List<ProjectUserInviteModel> organisationUsers, final List<ProjectUserInviteModel> invitedUsers, final ProjectResource project, final Long currentUser, final ApplicationResource app, final CompetitionResource competition) {
        this.organisationUsers = organisationUsers;
        this.invitedUsers = invitedUsers;
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.currentUser = currentUser;
        this.app = app;
        this.competition = competition;
    }

    public List<ProjectUserInviteModel> getOrganisationUsers() {
        return organisationUsers;
    }

    public List<ProjectUserInviteModel> getInvitedUsers() {
        return invitedUsers;
    }

    public Long getProjectId() {
        return projectId;
    }

    public Long getCurrentUser() {
        return currentUser;
    }

    public ApplicationResource getApp() {
        return app;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj.getClass() != getClass()) {
            return false;
        }
        SelectProjectManagerViewModel rhs = (SelectProjectManagerViewModel) obj;
        return new EqualsBuilder()
            .append(this.organisationUsers, rhs.organisationUsers)
            .append(this.invitedUsers, rhs.invitedUsers)
            .append(this.projectId, rhs.projectId)
            .append(this.currentUser, rhs.currentUser)
            .append(this.app, rhs.app)
            .append(this.competition, rhs.competition)
            .append(this.projectName, rhs.projectName)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
            .append(organisationUsers)
            .append(invitedUsers)
            .append(projectId)
            .append(currentUser)
            .append(app)
            .append(competition)
            .append(projectName)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("organisationUsers", organisationUsers)
            .append("invitedUsers", invitedUsers)
            .append("projectId", projectId)
            .append("currentUser", currentUser)
            .append("app", app)
            .append("competition", competition)
            .append("projectName", projectName)
            .toString();
    }
}
