package com.worth.ifs.project.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.List;

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
    private boolean inviteAction;

    public SelectProjectManagerViewModel(final List<ProjectUserInviteModel> organisationUsers, final List<ProjectUserInviteModel> invitedUsers, final ProjectResource project, final Long currentUser, final ApplicationResource app, final CompetitionResource competition, final boolean inviteAction) {
        this.organisationUsers = organisationUsers;
        this.invitedUsers = invitedUsers;
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.currentUser = currentUser;
        this.app = app;
        this.competition = competition;
        this.inviteAction = inviteAction;
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

    public boolean isInviteAction() {
        return inviteAction;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        SelectProjectManagerViewModel that = (SelectProjectManagerViewModel) o;

        return new EqualsBuilder()
                .append(inviteAction, that.inviteAction)
                .append(organisationUsers, that.organisationUsers)
                .append(invitedUsers, that.invitedUsers)
                .append(projectId, that.projectId)
                .append(currentUser, that.currentUser)
                .append(app, that.app)
                .append(competition, that.competition)
                .append(projectName, that.projectName)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(organisationUsers)
                .append(invitedUsers)
                .append(projectId)
                .append(currentUser)
                .append(app)
                .append(competition)
                .append(projectName)
                .append(inviteAction)
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
                .append("inviteAction", inviteAction)
                .toString();
    }
}
