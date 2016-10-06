package com.worth.ifs.project.sections;

import com.worth.ifs.project.resource.ProjectPartnerStatusResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;

import static com.worth.ifs.project.constant.ProjectActivityStates.*;
import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;
import static java.util.Arrays.asList;

/**
 * Component to check the progress of Project Setup.  This is used by the {@link ProjectSetupSectionInternalUser} to
 * determine which sections are available at a given time
 */
class ProjectSetupProgressChecker {

    private ProjectStatusResource projectStatus;
    private ProjectTeamStatusResource projectTeamStatus;

    public ProjectSetupProgressChecker(ProjectStatusResource projectStatusResource) {
        this.projectStatus = projectStatusResource;
    }

    public ProjectSetupProgressChecker(ProjectTeamStatusResource projectTeamStatusResource) {
        this.projectTeamStatus = projectTeamStatusResource;
    }

    public boolean isProjectDetailsSubmitted() {
        if(null != projectStatus) {
            return COMPLETE.equals(projectStatus.getProjectDetailsStatus());
        } else {
            return COMPLETE.equals(projectTeamStatus.getLeadPartnerStatus().getProjectDetailsStatus());
        }
    }

    public boolean isBankDetailsApproved() {
        return COMPLETE.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isBankDetailsQueried() {
        return PENDING.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isSpendProfileGenerated() {
        return asList(COMPLETE, ACTION_REQUIRED).contains(projectStatus.getSpendProfileStatus());
    }

    public boolean isOrganisationRequiringFunding() {
        return !NOT_REQUIRED.equals(projectStatus.getBankDetailsStatus());
    }

    public boolean isCompaniesHouseDetailsComplete(OrganisationResource organisation) {
        return COMPLETE.equals(getMatchingPartnerStatus(organisation).getCompaniesHouseStatus());
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

    private ProjectPartnerStatusResource getMatchingPartnerStatus(OrganisationResource organisation) {
        return simpleFindFirst(projectTeamStatus.getPartnerStatuses(), status -> status.getOrganisationId().equals(organisation.getId())).get();
    }

}
