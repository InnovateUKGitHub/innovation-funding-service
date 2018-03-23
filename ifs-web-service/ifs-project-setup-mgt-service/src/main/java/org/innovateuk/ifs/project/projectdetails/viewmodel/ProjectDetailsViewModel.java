package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import java.util.List;
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
    private boolean locationPerPartnerRequired;
    private List<PartnerOrganisationResource> partnerOrganisations;

    public ProjectDetailsViewModel(ProjectResource project, Long competitionId, String leadOrganisation,
                                   ProjectUserResource projectManager,
                                   Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap,
                                   boolean locationPerPartnerRequired,
                                   List<PartnerOrganisationResource> partnerOrganisations) {
        this.project = project;
        this.competitionId = competitionId;
        this.leadOrganisation = leadOrganisation;
        this.projectManager = projectManager;
        this.organisationFinanceContactMap = organisationFinanceContactMap;
        this.locationPerPartnerRequired = locationPerPartnerRequired;
        this.partnerOrganisations = partnerOrganisations;
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

    public boolean isLocationPerPartnerRequired() {
        return locationPerPartnerRequired;
    }

    public String getPostCodeForPartnerOrganisation(Long organisationId) {
        return partnerOrganisations.stream()
                .filter(partnerOrganisation ->  partnerOrganisation.getOrganisation().equals(organisationId))
                .findFirst()
                .map(PartnerOrganisationResource::getPostCode)
                .orElse(null);
    }
}
