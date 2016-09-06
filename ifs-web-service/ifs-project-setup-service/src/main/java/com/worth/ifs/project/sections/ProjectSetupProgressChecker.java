package com.worth.ifs.project.sections;

import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;

/**
 * Component to check the progress of Project Setup.  This is used by the {@link ProjectSetupSectionPartnerAccessor} to
 * determine which sections are available at a given time
 */
class ProjectSetupProgressChecker {

    public boolean isCompaniesHouseDetailsComplete(ProjectResource project, UserResource user, OrganisationResource organisation) {

        // TODO DW - implement
        return true;
    }

    public boolean isBusinessOrganisationType(ProjectResource project, UserResource user, OrganisationResource organisation) {

        // TODO DW - implement
        return false;
    }


    public boolean isProjectDetailsSectionComplete(ProjectResource project, UserResource user, OrganisationResource organisation) {

        // TODO DW - implement
        return true;
    }

    public boolean isFinanceContactSubmitted(ProjectResource project, UserResource user, OrganisationResource organisation) {

        // TODO DW - implement
        return true;
    }

    public boolean isBankDetailsApproved(ProjectResource project, UserResource user, OrganisationResource organisation) {

        // TODO DW - implement
        return true;
    }

    public boolean isBankDetailsQueried(ProjectResource project, UserResource user, OrganisationResource organisation) {

        // TODO DW - implement
        return true;
    }
}
