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

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(project, user, organisation,
                "Unable to access Project Details section until Companies House details are complete for Organisation");
    }

    public void checkAccessToMonitoringOfficerSection(ProjectResource project, UserResource user, OrganisationResource organisation) {

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(project, user, organisation,
                "Unable to access Monitoring Officer section until Companies House details are complete for Organisation");

        if (!projectSetupProgressChecker.isProjectDetailsSectionComplete(project, user, organisation)) {
            throwForbiddenException("Unable to access Monitoring Officer section until Project Details are submitted");
        }
    }

    public void checkAccessToBankDetailsSection(ProjectResource project, UserResource user, OrganisationResource organisation) {

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(project, user, organisation,
                "Unable to access Bank Details section until Companies House information is complete");

        if (!projectSetupProgressChecker.isFinanceContactSubmitted(project, user, organisation)) {

            throwForbiddenException("Unable to access Bank Details section until this Partner Organisation has submitted " +
                    "its Finance Contact");
        }
    }

    public void checkAccessToFinanceChecksSection(ProjectResource project, UserResource user, OrganisationResource organisation) {

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(project, user, organisation,
                "Unable to access Bank Details section until Companies House information is complete");

        if (!projectSetupProgressChecker.isProjectDetailsSectionComplete(project, user, organisation)) {
            throwForbiddenException("Unable to access Finance Checks section until the Project Details section is complete");
        }

        if (projectSetupProgressChecker.isBankDetailsApproved(project, user, organisation)) {
            return;
        }

        if (projectSetupProgressChecker.isBankDetailsQueried(project, user, organisation)) {
            return;
        }

        throwForbiddenException("Unable to access Finance Checks section until this Partner Organisation has had its " +
                "Bank Details approved or queried");
    }


    public void checkAccessToSpendProfileSection(ProjectResource project, UserResource user, OrganisationResource organisation) {

        if (projectSetupProgressChecker.isSpendProfileGenerated(project, user, organisation)) {
            return;
        }

        throwForbiddenException("Unable to access Spend Profile section until this Partner Organisation has had its " +
                "Spend Profile generated");
    }

    private void throwForbiddenException(String message) {
        throw new ForbiddenActionException(message);
    }

    private void checkCompaniesHouseSectionIsUnnecessaryOrComplete(ProjectResource project, UserResource user, OrganisationResource organisation, String failureMessage) {

        if (!projectSetupProgressChecker.isBusinessOrganisationType(project, user, organisation)) {
            return;
        }

        if (projectSetupProgressChecker.isCompaniesHouseDetailsComplete(project, user, organisation)) {
            return;
        }

        throwForbiddenException(failureMessage);
    }
}
