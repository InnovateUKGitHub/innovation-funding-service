package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.competition.resource.CompetitionResource;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.project.resource.ProjectUserResource;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.innovateuk.ifs.project.resource.ProjectState.COMPLETED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.HANDLED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.SETUP;

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
    private String financeReviewerName;
    private String financeReviewerEmail;

    public ProjectDetailsViewModel(ProjectResource project, Long competitionId,
                                   String competitionName, boolean ifsAdministrator,
                                   String leadOrganisation, ProjectUserResource projectManager,
                                   Map<OrganisationResource, ProjectUserResource> organisationFinanceContactMap,
                                   boolean locationPerPartnerRequired,
                                   List<PartnerOrganisationResource> partnerOrganisations,
                                   String financeReviewerName,
                                   String financeReviewerEmail) {
        this.project = project;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.ifsAdministrator = ifsAdministrator;
        this.leadOrganisation = leadOrganisation;
        this.projectManager = projectManager;
        this.organisationFinanceContactMap = organisationFinanceContactMap;
        this.locationPerPartnerRequired = locationPerPartnerRequired;
        this.partnerOrganisations = partnerOrganisations;
        this.financeReviewerName = financeReviewerName;
        this.financeReviewerEmail = financeReviewerEmail;
    }

    public static ProjectDetailsViewModel editDurationViewModel(ProjectResource project, CompetitionResource competition) {
        return new ProjectDetailsViewModel(project,
                competition.getId(),
                competition.getName(),
                false,
                null,
                null,
                null,
                false,
                Collections.emptyList(),
                null,
                null);
    }

    public ProjectResource getProject() {
        return project;
    }

    public boolean isSetup() {
        return SETUP.equals(project.getProjectState());
    }

    public boolean isHandleOffline() {
        return HANDLED_OFFLINE.equals(project.getProjectState());
    }

    public boolean isCompleteOffline() {
        return COMPLETED_OFFLINE.equals(project.getProjectState());
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


    public String getFinanceReviewerName() {
        return financeReviewerName;
    }

    public String getFinanceReviewerEmail() {
        return financeReviewerEmail;
    }

    public boolean isLocationPerPartnerRequired() {
        return locationPerPartnerRequired;
    }

    public String getPostcodeForPartnerOrganisation(Long organisationId) {
        return partnerOrganisations.stream()
                .filter(partnerOrganisation -> partnerOrganisation.getOrganisation().equals(organisationId))
                .findFirst()
                .map(PartnerOrganisationResource::getPostcode)
                .orElse(null);
    }

    public boolean isFinanceReviewerAssigned() {
        return financeReviewerEmail != null;
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
