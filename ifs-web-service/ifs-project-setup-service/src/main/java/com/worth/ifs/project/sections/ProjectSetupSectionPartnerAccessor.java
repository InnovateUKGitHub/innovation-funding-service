package com.worth.ifs.project.sections;

import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.project.resource.ProjectResource;
import com.worth.ifs.user.resource.OrganisationResource;
import com.worth.ifs.user.resource.UserResource;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
public class ProjectSetupSectionPartnerAccessor {

    private ProjectSetupProgressChecker projectSetupProgressChecker = new ProjectSetupProgressChecker();

    public void checkAccessToCompaniesHouseSection(ProjectResource project, UserResource user, OrganisationResource organisation) {

        if (projectSetupProgressChecker.isBusinessOrganisationType(project, user, organisation)) {
            return;
        }

        throwForbiddenException("Unable to access Companies House section if not a Business Organisation");
    }

    public void checkAccessToProjectDetailsSection(ProjectResource project, UserResource user, OrganisationResource organisation) {

        if (!projectSetupProgressChecker.isBusinessOrganisationType(project, user, organisation)) {
            return;
        }

        if (projectSetupProgressChecker.isCompaniesHouseDetailsComplete(project, user, organisation)) {
            return;
        }

        throwForbiddenException("Unable to access Project Details section until Companies House details are complete for Organisation");
    }

    public void checkAccessToMonitoringOfficerSection(ProjectResource project, UserResource user, OrganisationResource organisation) {

        if (projectSetupProgressChecker.isProjectDetailsSectionComplete(project, user, organisation)) {
            return;
        }

        throwForbiddenException("Unable to access Monitoring Officer section until Companies House details are complete for Organisation");
    }

    public void checkAccessToBankDetailsSection(ProjectResource project, UserResource user, OrganisationResource organisation) {

        // TODO DW - discuss with Robin
        throwForbiddenException("Unable to access Monitoring Officer section until Companies House details are complete for Organisation");
    }

    public void throwForbiddenException(String message) {
        throw new ForbiddenActionException(message);
    }
}
