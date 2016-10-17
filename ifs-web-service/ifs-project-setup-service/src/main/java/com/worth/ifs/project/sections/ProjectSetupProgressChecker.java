package com.worth.ifs.project.sections;

import com.worth.ifs.project.resource.ProjectPartnerStatusResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;

import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;

/**
 * Component to check the progress of Project Setup.  This is used by the {@link ProjectSetupSectionPartnerAccessor} to
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
        return COMPLETE.equals(getMatchingPartnerStatus(organisation).getBankDetailsStatus());
    }

    public boolean isBankDetailsQueried(OrganisationResource organisation) {
        return PENDING.equals(getMatchingPartnerStatus(organisation).getBankDetailsStatus());
    }

    public boolean isSpendProfileGenerated() {
        return asList(COMPLETE, ACTION_REQUIRED).contains(projectTeamStatus.getLeadPartnerStatus().getSpendProfileStatus());
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
}
