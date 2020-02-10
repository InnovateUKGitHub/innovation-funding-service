package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;

/**
 * Component to check the progress of Project Setup.  This is used by the {@link SetupSectionAccessibilityHelper} to
 * determine which sections are available at a given time
 */
class SetupProgressChecker {

    private ProjectTeamStatusResource projectTeamStatus;

    public SetupProgressChecker(ProjectTeamStatusResource projectTeamStatus) {
        this.projectTeamStatus = projectTeamStatus;
    }

    public boolean isCompaniesHouseDetailsComplete(OrganisationResource organisation) {
        return COMPLETE.equals(getMatchingPartnerStatus(organisation).getCompaniesHouseStatus());
    }

    public boolean isProjectDetailsSubmitted() {
        return COMPLETE.equals(projectTeamStatus.getLeadPartnerStatus().getProjectDetailsStatus());
    }

    public boolean isProjectTeamCompleted() {
        return projectTeamStatus.checkForAllPartners(projectPartnerStatusResource ->
                                                             COMPLETE.equals(projectPartnerStatusResource.getProjectTeamStatus()));
    }

    public boolean isMonitoringOfficerAssigned() {
        return COMPLETE.equals(projectTeamStatus.getLeadPartnerStatus().getMonitoringOfficerStatus());
    }

    public boolean isAllPartnerProjectLocationsSubmitted() {
        return projectTeamStatus.checkForAllPartners(projectPartnerStatusResource -> COMPLETE.equals(projectPartnerStatusResource.getPartnerProjectLocationStatus()));
    }

    public boolean isFinanceContactSubmitted(OrganisationResource organisation) {
        return COMPLETE.equals(getMatchingPartnerStatus(organisation).getFinanceContactStatus());
    }

    public boolean isPartnerProjectLocationSubmitted(OrganisationResource organisation) {
        return COMPLETE.equals(getMatchingPartnerStatus(organisation).getPartnerProjectLocationStatus());
    }

    public boolean isBankDetailsApproved(OrganisationResource organisation) {
        return asList(COMPLETE, NOT_REQUIRED).contains(getMatchingPartnerStatus(organisation).getBankDetailsStatus());
    }

    public boolean isBankDetailsQueried(OrganisationResource organisation) {
        return PENDING.equals(getMatchingPartnerStatus(organisation).getBankDetailsStatus());
    }

    public boolean isSpendProfileGenerated() {
        return asList(ACTION_REQUIRED, LEAD_ACTION_REQUIRED, PENDING, COMPLETE).contains(projectTeamStatus.getLeadPartnerStatus().getSpendProfileStatus());
    }

    public boolean isSpendProfileApproved() {
        return COMPLETE.equals(projectTeamStatus.getLeadPartnerStatus().getSpendProfileStatus());
    }

    public boolean isDocumentsApproved() {
        return COMPLETE.equals(projectTeamStatus.getLeadPartnerStatus().getDocumentsStatus());
    }

    public boolean isGrantOfferLetterAvailable() {
        return !NOT_REQUIRED.equals(projectTeamStatus.getLeadPartnerStatus().getGrantOfferLetterStatus());
    }

    private ProjectPartnerStatusResource getMatchingPartnerStatus(OrganisationResource organisation) {
        return projectTeamStatus.getPartnerStatusForOrganisation(organisation.getId()).get();
    }

    public boolean isLeadPartnerOrganisation(OrganisationResource organisation) {
        return projectTeamStatus.getLeadPartnerStatus().getOrganisationId().equals(organisation.getId());
    }

    public boolean isCompaniesHouseSectionRequired(OrganisationResource organisation) {
        return !NOT_REQUIRED.equals(getMatchingPartnerStatus(organisation).getCompaniesHouseStatus());
    }

    public boolean isOrganisationRequiringFunding(OrganisationResource organisation) {
        return !NOT_REQUIRED.equals(getMatchingPartnerStatus(organisation).getBankDetailsStatus());
    }

    public boolean isGrantOfferLetterSent() {
        return projectTeamStatus.getLeadPartnerStatus().isGrantOfferLetterSent();
    }

    public boolean isOfflineOrWithdrawn() {
        return projectTeamStatus.getProjectState().isOffline() || projectTeamStatus.getProjectState().isWithdrawn();
    }
}
