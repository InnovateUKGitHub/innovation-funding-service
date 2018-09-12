package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.project.resource.ProjectState.WITHDRAWN;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectDetailsViewModel {

    private ProjectResource project;
    private Long competitionId;
    private String competitionName;
    private boolean ifsAdministrator;
    private String leadOrganisation;
    private ProjectUserResource projectManager;
    private Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap;
    private boolean locationPerPartnerRequired;
    private List<PartnerOrganisationResource> partnerOrganisations;

    public ProjectDetailsViewModel(ProjectResource project, Long competitionId,
                                   String competitionName, boolean ifsAdministrator,
                                   String leadOrganisation, ProjectUserResource projectManager,
                                   Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap,
                                   boolean locationPerPartnerRequired,
                                   List<PartnerOrganisationResource> partnerOrganisations) {
        this.project = project;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.ifsAdministrator = ifsAdministrator;
        this.leadOrganisation = leadOrganisation;
        this.projectManager = projectManager;
        this.organisationFinanceContactMap = organisationFinanceContactMap;
        this.locationPerPartnerRequired = locationPerPartnerRequired;
        this.partnerOrganisations = partnerOrganisations;
    }

    public ProjectResource getProject() {
        return project;
    }

    public boolean isWithdrawn() {
        return WITHDRAWN.equals(project.getProjectState());
    }

    public boolean isShowWithdrawLink() {
        return ifsAdministrator && !isWithdrawn();
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public boolean isIfsAdministrator() {
        return ifsAdministrator;
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

    public String getPostcodeForPartnerOrganisation(Long organisationId) {
        return partnerOrganisations.stream()
                .filter(partnerOrganisation ->  partnerOrganisation.getOrganisation().equals(organisationId))
                .findFirst()
                .map(PartnerOrganisationResource::getPostcode)
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectDetailsViewModel that = (ProjectDetailsViewModel) o;

        return new EqualsBuilder()
                .append(locationPerPartnerRequired, that.locationPerPartnerRequired)
                .append(project, that.project)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(leadOrganisation, that.leadOrganisation)
                .append(projectManager, that.projectManager)
                .append(organisationFinanceContactMap, that.organisationFinanceContactMap)
                .append(partnerOrganisations, that.partnerOrganisations)
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
                .append(locationPerPartnerRequired)
                .append(partnerOrganisations)
                .toHashCode();
    }
}
