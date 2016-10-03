package com.worth.ifs.project.viewmodel;

import java.util.List;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;

/**
 * view model for the "select finance contact" page
 */
public class SelectFinanceContactViewModel {
    private List<FinanceContactModel> organisationUsers;
    private List<FinanceContactModel> invitedUsers;
    private Long organisationId;
    private Long projectId;
    private Long currentUser;
    private ApplicationResource app;
    private CompetitionResource competition;
    private String projectName;

    public SelectFinanceContactViewModel(final List<FinanceContactModel> organisationUsers, final List<FinanceContactModel> invitedUsers, final Long organisationId, final ProjectResource project, final Long currentUser, final ApplicationResource app, final CompetitionResource competition) {
        this.organisationUsers = organisationUsers;
        this.invitedUsers = invitedUsers;
        this.organisationId = organisationId;
        this.projectId = project.getId();
        this.projectName = project.getName();
        this.currentUser = currentUser;
        this.app = app;
        this.competition = competition;
    }

    public List<FinanceContactModel> getOrganisationUsers() {
        return organisationUsers;
    }

    public List<FinanceContactModel> getInvitedUsers() {
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
}
