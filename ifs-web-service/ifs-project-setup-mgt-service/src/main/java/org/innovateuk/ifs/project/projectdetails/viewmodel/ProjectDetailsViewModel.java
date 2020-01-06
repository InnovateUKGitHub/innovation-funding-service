package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;

import java.util.Collections;
import java.util.List;

import static org.innovateuk.ifs.project.resource.ProjectState.COMPLETED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.HANDLED_OFFLINE;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectDetailsViewModel {

    private ProjectResource project;
    private Long competitionId;
    private String competitionName;
    private boolean projectFinance;
    private boolean ifsAdministrator;
    private String leadOrganisation;
    private boolean locationPerPartnerRequired;
    private List<PartnerOrganisationResource> partnerOrganisations;
    private String financeReviewerName;
    private String financeReviewerEmail;
    private boolean spendProfileGenerated;


    public ProjectDetailsViewModel(ProjectResource project,
                                   Long competitionId,
                                   String competitionName,
                                   boolean projectFinance,
                                   boolean ifsAdministrator,
                                   String leadOrganisation,
                                   boolean locationPerPartnerRequired,
                                   List<PartnerOrganisationResource> partnerOrganisations,
                                   String financeReviewerName,
                                   String financeReviewerEmail,
                                   boolean spendProfileGenerated) {
        this.project = project;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.projectFinance = projectFinance;
        this.ifsAdministrator = ifsAdministrator;
        this.leadOrganisation = leadOrganisation;
        this.locationPerPartnerRequired = locationPerPartnerRequired;
        this.partnerOrganisations = partnerOrganisations;
        this.financeReviewerName = financeReviewerName;
        this.financeReviewerEmail = financeReviewerEmail;
        this.spendProfileGenerated = spendProfileGenerated;
    }

    public static ProjectDetailsViewModel editDurationViewModel(ProjectResource project) {
        return new ProjectDetailsViewModel(project,
                project.getCompetition(),
                project.getCompetitionName(),
                false,
                false,
                null,
                false,
                Collections.emptyList(),
                null,
                null,
                project.isSpendProfileGenerated());
    }

    public ProjectResource getProject() {
        return project;
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

    public boolean isAbleToManageProjectState() {
        return projectFinance;
    }

    public boolean isProjectFinance() {
        return projectFinance;
    }

    public String getLeadOrganisation() {
        return leadOrganisation;
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

    public List<PartnerOrganisationResource> getPartnerOrganisations() {
        return partnerOrganisations;
    }

    public boolean isFinanceReviewerAssigned() {
        return financeReviewerEmail != null;
    }

    public boolean isSpendProfileGenerated() {
        return spendProfileGenerated;
    }

    public boolean isIfsAdministrator() {
        return ifsAdministrator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectDetailsViewModel that = (ProjectDetailsViewModel) o;

        return new EqualsBuilder()
                .append(projectFinance, that.projectFinance)
                .append(locationPerPartnerRequired, that.locationPerPartnerRequired)
                .append(project, that.project)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(leadOrganisation, that.leadOrganisation)
                .append(partnerOrganisations, that.partnerOrganisations)
                .append(financeReviewerName, that.financeReviewerName)
                .append(financeReviewerEmail, that.financeReviewerEmail)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(project)
                .append(competitionId)
                .append(competitionName)
                .append(projectFinance)
                .append(leadOrganisation)
                .append(locationPerPartnerRequired)
                .append(partnerOrganisations)
                .append(financeReviewerName)
                .append(financeReviewerEmail)
                .toHashCode();
    }
}
