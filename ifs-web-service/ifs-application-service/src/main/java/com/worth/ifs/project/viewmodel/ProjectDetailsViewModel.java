package com.worth.ifs.project.viewmodel;

import com.worth.ifs.application.resource.ApplicationResource;
import com.worth.ifs.competition.resource.CompetitionResource;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;

import java.util.List;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectDetailsViewModel {

    private ProjectResource project;
    private UserResource currentUser;
    private Long currentOrganisation;
    private List<OrganisationResource> partnerOrganisations;
    private ApplicationResource app;
    private CompetitionResource competition;

    public ProjectDetailsViewModel(ProjectResource project, UserResource currentUser, Long currentOrganisation, List<OrganisationResource> partnerOrganisations, ApplicationResource app, CompetitionResource competition) {
        this.project = project;
        this.currentUser = currentUser;
        this.currentOrganisation = currentOrganisation;
        this.partnerOrganisations = partnerOrganisations;
        this.app = app;
        this.competition = competition;
    }

    public ProjectResource getProject() {
        return project;
    }

    public UserResource getCurrentUser() {
        return currentUser;
    }

    public Long getCurrentOrganisation() {
        return currentOrganisation;
    }

    public List<OrganisationResource> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public ApplicationResource getApp() {
        return app;
    }

    public CompetitionResource getCompetition() {
        return competition;
    }
}
