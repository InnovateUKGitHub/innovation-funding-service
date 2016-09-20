package com.worth.ifs.project.sections;

import com.worth.ifs.project.resource.ProjectPartnerStatusResource;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;

import static com.worth.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Component to check the progress of Project Setup.  This is used by the {@link ProjectSetupSectionPartnerAccessor} to
 * determine which sections are available at a given time
 */
class ProjectSetupProgressChecker {

    private ProjectTeamStatusResource projectTeamStatus;

    public boolean isCompaniesHouseDetailsComplete(OrganisationResource organisation) {


        ProjectPartnerStatusResource matchingPartner = simpleFindFirst(projectTeamStatus.getPartnerStatuses(), status -> status.getOrganisationId().equals(organisation.getId())).get();
        // TODO DW - implement
        return true;
    }

    public boolean isBusinessOrganisationType(OrganisationResource organisation) {

        // TODO DW - implement
        return false;
    }


    public boolean isProjectDetailsSectionComplete() {

        // TODO DW - implement
        return true;
    }

    public boolean isFinanceContactSubmitted(OrganisationResource organisation) {

        // TODO DW - implement
        return true;
    }

    public boolean isBankDetailsApproved(OrganisationResource organisation) {

        // TODO DW - implement
        return true;
    }

    public boolean isBankDetailsQueried(OrganisationResource organisation) {

        // TODO DW - implement
        return true;
    }

    public boolean isSpendProfileGenerated() {

        // TODO DW - implement
        return true;
    }
}
