package org.innovateuk.ifs.project.status.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.user.resource.OrganisationResource;

import static org.innovateuk.ifs.project.sections.SectionAccess.*;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
public class SetupSectionAccessibilityHelper {

    private static final Log LOG = LogFactory.getLog(SetupSectionAccessibilityHelper.class);

    private SetupProgressChecker setupProgressChecker;

    public SetupSectionAccessibilityHelper(ProjectTeamStatusResource projectTeamStatus) {
        this.setupProgressChecker = new SetupProgressChecker(projectTeamStatus);
    }

    public SectionAccess canAccessCompaniesHouseSection(OrganisationResource organisation) {

        if (setupProgressChecker.isCompaniesHouseSectionRequired(organisation)) {
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

    public SectionAccess leadCanAccessProjectManagerPage(OrganisationResource organisation) {

        if (isCompaniesHouseIncompleteOrProjectDetailsSubmittedOrNotLeadPartner(organisation)) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess leadCanAccessProjectStartDatePage(OrganisationResource organisation) {

        if (isCompaniesHouseIncompleteOrProjectDetailsSubmittedOrNotLeadPartner(organisation)) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess leadCanAccessProjectAddressPage(OrganisationResource organisation) {

        if (isCompaniesHouseIncompleteOrProjectDetailsSubmittedOrNotLeadPartner(organisation)) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    private boolean isCompaniesHouseIncompleteOrProjectDetailsSubmittedOrNotLeadPartner(OrganisationResource organisation) {

        return !isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access until Companies House details are complete for Organisation")

                //|| setupProgressChecker.isProjectDetailsSubmitted()
                || !setupProgressChecker.isLeadPartnerOrganisation(organisation);

    }

    public SectionAccess canAccessMonitoringOfficerSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Monitoring Officer section until Companies House details are complete for Organisation")) {
            return NOT_ACCESSIBLE;
        }

        if (!setupProgressChecker.isProjectDetailsSubmitted()) {
            return fail("Unable to access Monitoring Officer section until Project Details are submitted");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessBankDetailsSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Bank Details section until Companies House information is complete")) {
            return NOT_ACCESSIBLE;
        }

        if(!setupProgressChecker.isOrganisationRequiringFunding(organisation)){
            return NOT_ACCESSIBLE;
        }

        if (!setupProgressChecker.isFinanceContactSubmitted(organisation)) {

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

        if (!setupProgressChecker.isProjectDetailsSubmitted()) {
            return fail("Unable to access Finance Checks section until the Project Details section is complete");
        }

        if (!setupProgressChecker.isFinanceContactSubmitted(organisation)) {

            return fail("Unable to access Bank Details section until this Partner Organisation has submitted " +
                    "its Finance Contact");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessSpendProfileSection(OrganisationResource organisation) {

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Spend Profile section until Companies House information is complete")) {
            return NOT_ACCESSIBLE;
        }

        if (!setupProgressChecker.isProjectDetailsSubmitted()) {

            return fail("Unable to access Spend Profile section until the Project Details section is complete");
        }

        if (!isBankDetailsApproved(organisation)) {

            return fail("Unable to access Spend Profile section until this Organisation's Bank Details have been " +
                    "approved or queried");
        }

        if (!setupProgressChecker.isSpendProfileGenerated()) {

            return fail("Unable to access Spend Profile section until this Partner Organisation has had its " +
                    "Spend Profile generated");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessOtherDocumentsSection(OrganisationResource organisation) {

        if (setupProgressChecker.isLeadPartnerOrganisation(organisation)) {
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

        if (setupProgressChecker.isSpendProfileApproved() && setupProgressChecker.isOtherDocumentsApproved()
                && setupProgressChecker.isGrantOfferLetterAvailable() && setupProgressChecker.isGrantOfferLetterSent()) {
            return ACCESSIBLE;
        }

        return NOT_ACCESSIBLE;
    }

    public boolean isProjectDetailsSubmitted() {
        return setupProgressChecker.isProjectDetailsSubmitted();
    }

    public boolean isSpendProfileGenerated() {
        return setupProgressChecker.isSpendProfileGenerated();
    }

    public boolean isFinanceContactSubmitted(OrganisationResource organisationResource) {
        return setupProgressChecker.isFinanceContactSubmitted(organisationResource);
    }

    private boolean isBankDetailsApproved(OrganisationResource organisation) {
        return setupProgressChecker.isBankDetailsApproved(organisation);
    }

    private SectionAccess fail(String message) {
        LOG.info(message);
        return NOT_ACCESSIBLE;
    }

    private boolean isCompaniesHouseSectionIsUnnecessaryOrComplete(OrganisationResource organisation, String failureMessage) {

        if (!setupProgressChecker.isCompaniesHouseSectionRequired(organisation)) {
            return true;
        }

        if (setupProgressChecker.isCompaniesHouseDetailsComplete(organisation)) {
            return true;
        }

        LOG.info(failureMessage);
        return false;
    }
}
