package com.worth.ifs.project.sections;

import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
public class ProjectSetupSectionPartnerAccessor {

    private ProjectSetupProgressChecker projectSetupProgressChecker;

    public ProjectSetupSectionPartnerAccessor(ProjectTeamStatusResource projectTeamStatus) {
        this.projectSetupProgressChecker = new ProjectSetupProgressChecker(projectTeamStatus);
    }

    public void checkAccessToCompaniesHouseSection(OrganisationResource organisation) {

        if (projectSetupProgressChecker.isBusinessOrganisationType(organisation)) {
            return;
        }

        throwForbiddenException("Unable to access Companies House section if not a Business Organisation");
    }

    public void checkAccessToProjectDetailsSection(OrganisationResource organisation) {

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Project Details section until Companies House details are complete for Organisation");
    }

    public void checkAccessToMonitoringOfficerSection(OrganisationResource organisation) {

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Monitoring Officer section until Companies House details are complete for Organisation");

        if (!projectSetupProgressChecker.isProjectDetailsSectionComplete()) {
            throwForbiddenException("Unable to access Monitoring Officer section until Project Details are submitted");
        }
    }

    public void checkAccessToBankDetailsSection(OrganisationResource organisation) {

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Bank Details section until Companies House information is complete");

        if (!projectSetupProgressChecker.isFinanceContactSubmitted(organisation)) {

            throwForbiddenException("Unable to access Bank Details section until this Partner Organisation has submitted " +
                    "its Finance Contact");
        }
    }

    public void checkAccessToFinanceChecksSection(OrganisationResource organisation) {

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Bank Details section until Companies House information is complete");

        if (!projectSetupProgressChecker.isProjectDetailsSectionComplete()) {
            throwForbiddenException("Unable to access Finance Checks section until the Project Details section is complete");
        }

        if (!isBankDetailsApprovedOrQueried(organisation)) {

            throwForbiddenException("Unable to access Finance Checks section until this Partner Organisation has had its " +
                    "Bank Details approved or queried");
        }
    }

    public void checkAccessToSpendProfileSection(OrganisationResource organisation) {

        checkCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Spend Profile section until Companies House information is complete");

        if (!projectSetupProgressChecker.isProjectDetailsSectionComplete()) {

            throwForbiddenException("Unable to access Spend Profile section until the Project Details section is complete");
        }

        if (!isBankDetailsApprovedOrQueried(organisation)) {

            throwForbiddenException("Unable to access Spend Profile section until this Organisation's Bank Details have been " +
                    "approved or queried");
        }

        if (!projectSetupProgressChecker.isSpendProfileGenerated()) {

            throwForbiddenException("Unable to access Spend Profile section until this Partner Organisation has had its " +
                    "Spend Profile generated");
        }
    }

    private boolean isBankDetailsApprovedOrQueried(OrganisationResource organisation) {
        return projectSetupProgressChecker.isBankDetailsApproved(organisation) ||
                projectSetupProgressChecker.isBankDetailsQueried(organisation);
    }

    private void throwForbiddenException(String message) {
        throw new ForbiddenActionException(message);
    }

    private void checkCompaniesHouseSectionIsUnnecessaryOrComplete(OrganisationResource organisation, String failureMessage) {

        if (!projectSetupProgressChecker.isBusinessOrganisationType(organisation)) {
            return;
        }

        if (projectSetupProgressChecker.isCompaniesHouseDetailsComplete(organisation)) {
            return;
        }

        throwForbiddenException(failureMessage);
    }
}
