package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
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
    private String competitionName;
    private String leadOrganisation;
    private ProjectUserResource projectManager;
    private Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap;

    public ProjectDetailsViewModel(ProjectResource project, Long competitionId, String competitionName,
                                   String leadOrganisation, ProjectUserResource projectManager,
                                   Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap) {
        this.project = project;
        this.competitionId = competitionId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectDetailsViewModel that = (ProjectDetailsViewModel) o;

        return new EqualsBuilder()
                .append(project, that.project)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(leadOrganisation, that.leadOrganisation)
                .append(projectManager, that.projectManager)
                .append(organisationFinanceContactMap, that.organisationFinanceContactMap)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(project)
                .append(competitionId)
                .append(competitionName)
                .append(leadOrganisation)
                .append(projectManager)
                .append(organisationFinanceContactMap)
                .toHashCode();
    }
}
