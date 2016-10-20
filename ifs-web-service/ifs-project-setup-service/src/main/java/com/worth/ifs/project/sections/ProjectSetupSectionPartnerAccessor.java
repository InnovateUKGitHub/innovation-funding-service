package com.worth.ifs.project.sections;

import com.worth.ifs.project.resource.ProjectTeamStatusResource;
import com.worth.ifs.user.resource.OrganisationResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static com.worth.ifs.project.sections.SectionAccess.ACCESSIBLE;
import static com.worth.ifs.project.sections.SectionAccess.NOT_ACCESSIBLE;
import static com.worth.ifs.project.sections.SectionAccess.NOT_REQUIRED;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
public class ProjectSetupSectionPartnerAccessor {

    private static final Log LOG = LogFactory.getLog(ProjectSetupSectionPartnerAccessor.class);

    private ProjectSetupProgressChecker projectSetupProgressChecker;

    public ProjectSetupSectionPartnerAccessor(ProjectTeamStatusResource projectTeamStatus) {
        this.projectSetupProgressChecker = new ProjectSetupProgressChecker(projectTeamStatus);
    }

    public SectionAccess canAccessCompaniesHouseSection(OrganisationResource organisation) {

        if (projectSetupProgressChecker.isCompaniesHouseSectionRequired(organisation)) {
            return ACCESSIBLE;
        }

        LOG.debug("No need to access Companies House section if not a Business Organisation");
        return NOT_REQUIRED;
    }

    public SectionAccess canAccessProjectDetailsSection(OrganisationResource organisation) {

        if (isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Project Details section until Companies House details are complete for Organisation")) {
            return ACCESSIBLE;
        }

        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessMonitoringOfficerSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Monitoring Officer section until Companies House details are complete for Organisation")) {
            return NOT_ACCESSIBLE;
        }

        if (!projectSetupProgressChecker.isProjectDetailsSubmitted()) {
            return fail("Unable to access Monitoring Officer section until Project Details are submitted");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessBankDetailsSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Bank Details section until Companies House information is complete")) {
            return NOT_ACCESSIBLE;
        }

        if (!projectSetupProgressChecker.isFinanceContactSubmitted(organisation)) {

            return fail("Unable to access Bank Details section until this Partner Organisation has submitted " +
                    "its Finance Contact");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessFinanceChecksSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Bank Details section until Companies House information is complete")) {
            return NOT_ACCESSIBLE;
        }

        if (!projectSetupProgressChecker.isProjectDetailsSubmitted()) {
            return fail("Unable to access Finance Checks section until the Project Details section is complete");
        }

        if (!isBankDetailsApprovedOrQueried(organisation)) {

            return fail("Unable to access Finance Checks section until this Partner Organisation has had its " +
                    "Bank Details approved or queried");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessSpendProfileSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Spend Profile section until Companies House information is complete")) {
            return NOT_ACCESSIBLE;
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

        return ACCESSIBLE;
    }

    public SectionAccess canAccessOtherDocumentsSection(OrganisationResource organisation) {

        if (projectSetupProgressChecker.isLeadPartnerOrganisation(organisation)) {
            return ACCESSIBLE;
        }

        if (isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Non-lead Partners are unable to access Other Documents section until their Companies House information " +
                        "is complete")) {
            return ACCESSIBLE;
        }

        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessGrantOfferLetterSection(OrganisationResource organisation) {

        // TODO DW - implement when the ability to generate a Grant Offer Letter is enabled
        return NOT_ACCESSIBLE;
    }

    public boolean isProjectDetailsSubmitted() {
        return projectSetupProgressChecker.isProjectDetailsSubmitted();
    }

    private boolean isBankDetailsApprovedOrQueried(OrganisationResource organisation) {

        // TODO DW - INFUND-4428 - reinstate when bank details are approvable or queryable
        return true;
//        return projectSetupProgressChecker.isBankDetailsApproved(organisation) ||
//                projectSetupProgressChecker.isBankDetailsQueried(organisation);
    }

    private SectionAccess fail(String message) {
        LOG.info(message);
        return NOT_ACCESSIBLE;
    }

    private boolean isCompaniesHouseSectionIsUnnecessaryOrComplete(OrganisationResource organisation, String failureMessage) {

        if (!projectSetupProgressChecker.isCompaniesHouseSectionRequired(organisation)) {
            return true;
        }

        if (projectSetupProgressChecker.isCompaniesHouseDetailsComplete(organisation)) {
            return true;
        }

        LOG.info(failureMessage);
        return false;
    }
}
