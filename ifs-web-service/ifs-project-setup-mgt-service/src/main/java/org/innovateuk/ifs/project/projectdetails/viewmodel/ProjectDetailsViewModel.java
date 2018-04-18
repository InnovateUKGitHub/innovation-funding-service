package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.Map;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectDetailsViewModel {

    private ProjectResource project;
    private Long competitionId;
    private boolean ifsAdministrator;
    private String competitionName;
    private String leadOrganisation;
    private ProjectUserResource projectManager;
    private Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap;

    public ProjectDetailsViewModel(ProjectResource project, Long competitionId,
                                   boolean isIfsAdministrator, String competitionName, String leadOrganisation,
                                   ProjectUserResource projectManager,
                                   Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap) {
        this.project = project;
        this.competitionId = competitionId;
        this.ifsAdministrator = isIfsAdministrator;
        this.competitionName = competitionName;
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

    public boolean isIfsAdministrator() {
        return ifsAdministrator;
    }

    public String getCompetitionName() {
        return competitionName;
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
