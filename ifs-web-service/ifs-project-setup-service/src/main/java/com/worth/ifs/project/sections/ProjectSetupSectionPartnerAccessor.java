package com.worth.ifs.project.sections;

import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
public class ProjectSetupSectionPartnerAccessor {

    private static final Log LOG = LogFactory.getLog(ProjectSetupSectionPartnerAccessor.class);

    private ProjectSetupProgressChecker projectSetupProgressChecker;

    public ProjectSetupSectionPartnerAccessor(ProjectTeamStatusResource projectTeamStatus) {
        this.projectSetupProgressChecker = new ProjectSetupProgressChecker(projectTeamStatus);
    }

    public boolean canAccessCompaniesHouseSection(OrganisationResource organisation) {

        if (projectSetupProgressChecker.isBusinessOrganisationType(organisation)) {
            return true;
        }

        return fail("Unable to access Companies House section if not a Business Organisation");
    }

    public boolean canAccessProjectDetailsSection(OrganisationResource organisation) {

        return isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Project Details section until Companies House details are complete for Organisation");
    }

    public boolean canAccessMonitoringOfficerSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Monitoring Officer section until Companies House details are complete for Organisation")) {
            return false;
        }

        if (!projectSetupProgressChecker.isProjectDetailsSubmitted()) {
            return fail("Unable to access Monitoring Officer section until Project Details are submitted");
        }

        return true;
    }

    public boolean canAccessBankDetailsSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Bank Details section until Companies House information is complete")) {
            return false;
        }

        if (!projectSetupProgressChecker.isFinanceContactSubmitted(organisation)) {

            return fail("Unable to access Bank Details section until this Partner Organisation has submitted " +
                    "its Finance Contact");
        }

        return true;
    }

    public boolean canAccessFinanceChecksSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Bank Details section until Companies House information is complete")) {
            return false;
        }

        if (!projectSetupProgressChecker.isProjectDetailsSubmitted()) {
            return fail("Unable to access Finance Checks section until the Project Details section is complete");
        }

        if (!isBankDetailsApprovedOrQueried(organisation)) {

            return fail("Unable to access Finance Checks section until this Partner Organisation has had its " +
                    "Bank Details approved or queried");
        }

        return true;
    }

    public boolean canAccessSpendProfileSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Spend Profile section until Companies House information is complete")) {
            return false;
        }

        if (!projectSetupProgressChecker.isProjectDetailsSubmitted()) {

            return fail("Unable to access Spend Profile section until the Project Details section is complete");
        }

        if (!isBankDetailsApprovedOrQueried(organisation)) {

            return fail("Unable to access Spend Profile section until this Organisation's Bank Details have been " +
                    "approved or queried");
        }

        if (!projectSetupProgressChecker.isSpendProfileGenerated()) {

            return fail("Unable to access Spend Profile section until this Partner Organisation has had its " +
                    "Spend Profile generated");
        }

        return true;
    }

    public boolean isProjectDetailsSubmitted() {
        return projectSetupProgressChecker.isProjectDetailsSubmitted();
    }

    private boolean isBankDetailsApprovedOrQueried(OrganisationResource organisation) {
        return projectSetupProgressChecker.isBankDetailsApproved(organisation) ||
                projectSetupProgressChecker.isBankDetailsQueried(organisation);
    }

    private boolean fail(String message) {
        LOG.info(message);
        return false;
    }

    private boolean isCompaniesHouseSectionIsUnnecessaryOrComplete(OrganisationResource organisation, String failureMessage) {

        if (!projectSetupProgressChecker.isBusinessOrganisationType(organisation)) {
            return true;
        }

        if (projectSetupProgressChecker.isCompaniesHouseDetailsComplete(organisation)) {
            return true;
        }

        LOG.info(failureMessage);
        return false;
    }
}
