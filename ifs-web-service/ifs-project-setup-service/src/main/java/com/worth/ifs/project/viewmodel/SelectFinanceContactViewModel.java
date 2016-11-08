package com.worth.ifs.project.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;

import java.util.List;

/**
 * view model for the "select finance contact" page
 */
public class SelectFinanceContactViewModel {
    private List<ProjectUserInviteModel> organisationUsers;
    private List<ProjectUserInviteModel> invitedUsers;
    private Long organisationId;
    private Long projectId;
    private Long currentUser;
    private ApplicationResource app;
    private CompetitionResource competition;
    private String projectName;
    private boolean inviteAction;

    public SelectFinanceContactViewModel(final List<ProjectUserInviteModel> organisationUsers, final List<ProjectUserInviteModel> invitedUsers, final Long organisationId, final ProjectResource project, final Long currentUser, final ApplicationResource app, final CompetitionResource competition, final boolean inviteAction) {
        this.organisationUsers = organisationUsers;
        this.invitedUsers = invitedUsers;
        this.organisationId = organisationId;
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

    public Long getOrganisationId() {
        return organisationId;
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
}
