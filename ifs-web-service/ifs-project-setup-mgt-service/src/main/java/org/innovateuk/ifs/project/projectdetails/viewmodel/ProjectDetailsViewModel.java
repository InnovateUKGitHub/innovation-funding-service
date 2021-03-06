package org.innovateuk.ifs.project.projectdetails.viewmodel;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.PartnerOrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectResource;
import org.innovateuk.ifs.user.resource.UserResource;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.innovateuk.ifs.project.resource.ProjectState.COMPLETED_OFFLINE;
import static org.innovateuk.ifs.project.resource.ProjectState.HANDLED_OFFLINE;
import static org.innovateuk.ifs.user.resource.Authority.IFS_ADMINISTRATOR;
import static org.innovateuk.ifs.user.resource.Authority.PROJECT_FINANCE;

/**
 * View model backing the Project Details page for Project Setup
 */
public class ProjectDetailsViewModel {

    private ProjectResource project;
    private Long competitionId;
    private String competitionName;
    private UserResource userResource;
    private OrganisationResource leadOrganisation;
    private List<PartnerOrganisationResource> partnerOrganisations;
    private List<OrganisationResource> organisations;
    private String financeReviewerName;
    private String financeReviewerEmail;
    private boolean spendProfileGenerated;
    private boolean ktpCompetition;

    public ProjectDetailsViewModel(ProjectResource project,
                                   Long competitionId,
                                   String competitionName,
                                   UserResource userResource,
                                   OrganisationResource leadOrganisation,
                                   List<PartnerOrganisationResource> partnerOrganisations,
                                   List<OrganisationResource> organisations,
                                   String financeReviewerName,
                                   String financeReviewerEmail,
                                   boolean spendProfileGenerated,
                                   boolean ktpCompetition) {
        this.project = project;
        this.competitionId = competitionId;
        this.competitionName = competitionName;
        this.userResource = userResource;
        this.leadOrganisation = leadOrganisation;
        this.partnerOrganisations = partnerOrganisations;
        this.organisations = organisations;
        this.financeReviewerName = financeReviewerName;
        this.financeReviewerEmail = financeReviewerEmail;
        this.spendProfileGenerated = spendProfileGenerated;
        this.ktpCompetition = ktpCompetition;
    }

    public static ProjectDetailsViewModel editDurationViewModel(ProjectResource project, boolean ktpCompetition) {
        return new ProjectDetailsViewModel(project,
                project.getCompetition(),
                project.getCompetitionName(),
                null,
                null,
                Collections.emptyList(),
                Collections.emptyList(),
                null,
                null,
                project.isSpendProfileGenerated(),
                ktpCompetition);
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

    public OrganisationResource getLeadOrganisation() {
        return leadOrganisation;
    }

    public String getFinanceReviewerName() {
        return financeReviewerName;
    }

    public String getFinanceReviewerEmail() {
        return financeReviewerEmail;
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

    public boolean isAbleToManageProjectState() {
        return userResource.hasAuthority(PROJECT_FINANCE);
    }

    public boolean isProjectFinance() {
        return userResource.hasAuthority(PROJECT_FINANCE);
    }

    public String getLocationForPartnerOrganisation(Long organisationId) {
        return partnerOrganisations.stream()
                .filter(partnerOrganisation ->  partnerOrganisation.getOrganisation().equals(organisationId))
                .findFirst()
                .map(partnerOrg -> {
                    Optional<OrganisationResource> organisationResource = organisations.stream().filter(org -> organisationId.equals(org.getId())).findFirst();
                    if (organisationResource.isPresent() && organisationResource.get().isInternational()) {
                        return partnerOrg.getInternationalLocation();
                    } else {
                        return partnerOrg.getPostcode();
                    }
                })
                .orElse("Not yet completed");
    }

    public boolean isLeadOrganisationInternational() {
        return leadOrganisation.isInternational();
    }

    /*
    * View model logic.
    * */

    public boolean modifyTheFinanceReviewer() {
        return userResource.hasAuthority(PROJECT_FINANCE) && project.getProjectState().isActive();
    }

    public boolean modifyStartDate() {
        return userResource.hasAuthority(IFS_ADMINISTRATOR) && !project.isSpendProfileGenerated();
    }

    public boolean isKtpCompetition() {
        return ktpCompetition;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        ProjectDetailsViewModel that = (ProjectDetailsViewModel) o;

        return new EqualsBuilder()
                .append(userResource, that.userResource)
                .append(project, that.project)
                .append(competitionId, that.competitionId)
                .append(competitionName, that.competitionName)
                .append(leadOrganisation, that.leadOrganisation)
                .append(partnerOrganisations, that.partnerOrganisations)
                .append(organisations, that.organisations)
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
                .append(userResource)
                .append(leadOrganisation)
                .append(partnerOrganisations)
                .append(organisations)
                .append(financeReviewerName)
                .append(financeReviewerEmail)
                .toHashCode();
    }
}
