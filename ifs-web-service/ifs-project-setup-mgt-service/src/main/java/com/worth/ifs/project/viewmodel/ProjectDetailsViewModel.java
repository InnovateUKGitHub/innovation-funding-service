package com.worth.ifs.project.viewmodel;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.project.resource.ProjectUserResource;
import com.worth.ifs.user.resource.OrganisationResource;

import java.util.Map;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectDetailsViewModel {

    private ProjectResource project;
    private Long competitionId;
    private String leadOrganisation;
    private ProjectUserResource projectManager;
    private Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap;

    public ProjectDetailsViewModel(ProjectResource project, Long competitionId, String leadOrganisation,
                                   ProjectUserResource projectManager,
                                   Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap) {
        this.project = project;
        this.competitionId = competitionId;
        this.leadOrganisation = leadOrganisation;
        this.projectManager = projectManager;
        this.organisationFinanceContactMap = organisationFinanceContactMap;
    }

    public ProjectResource getProject() {
        return project;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
    }

    public ProjectUserResource getProjectManager() {
        return projectManager;
    }

    public Map<OrganisationResource, ProjectUserResource> getOrganisationFinanceContactMap() {
        return organisationFinanceContactMap;
    }
}
