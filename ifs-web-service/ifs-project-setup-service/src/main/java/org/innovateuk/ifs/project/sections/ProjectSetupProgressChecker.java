package org.innovateuk.ifs.project.sections;

import org.innovateuk.ifs.project.resource.ProjectPartnerStatusResource;
import org.innovateuk.ifs.project.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import static org.innovateuk.ifs.project.constant.ProjectActivityStates.*;
import static java.util.Arrays.asList;

/**
 * Component to check the progress of Project Setup.  This is used by the {@link ProjectSetupSectionAccessibilityHelper} to
 * determine which sections are available at a given time
 */
class ProjectSetupProgressChecker {

    private ProjectTeamStatusResource projectTeamStatus;

    public ProjectSetupProgressChecker(ProjectTeamStatusResource projectTeamStatus) {
        this.projectTeamStatus = projectTeamStatus;
    }

    public boolean isCompaniesHouseDetailsComplete(OrganisationResource organisation) {
        return COMPLETE.equals(getMatchingPartnerStatus(organisation).getCompaniesHouseStatus());
    }

    public boolean isProjectDetailsSubmitted() {
        return COMPLETE.equals(projectTeamStatus.getLeadPartnerStatus().getProjectDetailsStatus());
    }

    public boolean isFinanceContactSubmitted(OrganisationResource organisation) {
        return COMPLETE.equals(getMatchingPartnerStatus(organisation).getFinanceContactStatus());
    }

    public boolean isBankDetailsApproved(OrganisationResource organisation) {
        return asList(COMPLETE, NOT_REQUIRED).contains(getMatchingPartnerStatus(organisation).getBankDetailsStatus());
    }

    public boolean isBankDetailsQueried(OrganisationResource organisation) {
        return PENDING.equals(getMatchingPartnerStatus(organisation).getBankDetailsStatus());
    }

    public boolean isSpendProfileGenerated() {
        return asList(ACTION_REQUIRED, PENDING, COMPLETE).contains(projectTeamStatus.getLeadPartnerStatus().getSpendProfileStatus());
    }

    public boolean isSpendProfileApproved() {
        return COMPLETE.equals(projectTeamStatus.getLeadPartnerStatus().getSpendProfileStatus());
    }

    public boolean isOtherDocumentsApproved() {
        return COMPLETE.equals(projectTeamStatus.getLeadPartnerStatus().getOtherDocumentsStatus());
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
        return projectTeamStatus.getLeadPartnerStatus().getIsGrantOfferLetterSent();
    }
}
