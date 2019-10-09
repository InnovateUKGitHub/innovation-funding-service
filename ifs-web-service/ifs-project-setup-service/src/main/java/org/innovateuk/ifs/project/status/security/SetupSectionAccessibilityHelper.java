package org.innovateuk.ifs.project.status.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.organisation.resource.OrganisationResource;
import org.innovateuk.ifs.project.status.resource.ProjectTeamStatusResource;
import org.innovateuk.ifs.sections.SectionAccess;

import static org.innovateuk.ifs.sections.SectionAccess.*;

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
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (setupProgressChecker.isCompaniesHouseSectionRequired(organisation)) {
            return ACCESSIBLE;
        }

        LOG.debug("No need to access Companies House section if not a Business Organisation");
        return NOT_REQUIRED;
    }

    public SectionAccess canAccessProjectDetailsSection(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Project Details section until Companies House details are complete for Organisation")) {
            return ACCESSIBLE;
        }

        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessProjectTeamSection(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                 "Unable to access Project Team section until Companies House details are complete for Organisation")) {
            return ACCESSIBLE;
        }

        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessFinanceContactPage(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (isCompaniesHouseIncompleteOrGOLAlreadyGenerated(organisation)) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessPartnerProjectLocationPage(OrganisationResource organisation, boolean partnerProjectLocationRequired) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (!partnerProjectLocationRequired) {
            return NOT_ACCESSIBLE;
        }

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Partner Project Location page until Companies House details are complete for Organisation")) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public boolean isMonitoringOfficerAssigned() {
        return setupProgressChecker.isMonitoringOfficerAssigned();
    }

    public SectionAccess leadCanAccessProjectManagerPage(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (isCompaniesHouseIncompleteOrGOLAlreadyGeneratedOrNotLeadPartner(organisation)) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess leadCanAccessProjectStartDatePage(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (isCompaniesHouseIncompleteOrNotLeadPartner(organisation)) {
            return NOT_ACCESSIBLE;
        }

        if (isSpendProfileGenerated()) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess leadCanAccessProjectAddressPage(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (isCompaniesHouseIncompleteOrGOLAlreadyGeneratedOrNotLeadPartner(organisation)) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    private boolean isCompaniesHouseIncompleteOrNotLeadPartner(OrganisationResource organisation) {

        return !isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access until Companies House details are complete for Organisation")
                || !setupProgressChecker.isLeadPartnerOrganisation(organisation);

    }

    private boolean isCompaniesHouseIncompleteOrGOLAlreadyGenerated(OrganisationResource organisation) {

        return !isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access until Companies House details are complete for Organisation")

                || isGrantOfferLetterGenerated();

    }

    private boolean isCompaniesHouseIncompleteOrGOLAlreadyGeneratedOrNotLeadPartner(OrganisationResource organisation) {

        return !isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access until Companies House details are complete for Organisation")

                || isGrantOfferLetterGenerated()
                || !setupProgressChecker.isLeadPartnerOrganisation(organisation);

    }

    public boolean isGrantOfferLetterGenerated(){
        return setupProgressChecker.isGrantOfferLetterAvailable();
    }

    public SectionAccess canAccessMonitoringOfficerSection(OrganisationResource organisation, boolean partnerProjectLocationRequired) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Monitoring Officer section until Companies House details are complete for Organisation")) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessBankDetailsSection(OrganisationResource organisation) {

        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

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

        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Finance Checks section until Companies House information is complete")) {
            return NOT_ACCESSIBLE;
        }

        if (!setupProgressChecker.isFinanceContactSubmitted(organisation)) {

            return fail("Unable to access Finance Checks section until this Partner Organisation has submitted " +
                    "its Finance Contact");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessSpendProfileSection(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (!isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Unable to access Spend Profile section until Companies House information is complete")) {
            return NOT_ACCESSIBLE;
        }

        if (!setupProgressChecker.isProjectDetailsSubmitted()) {

            return fail("Unable to access Spend Profile section until the Project Details section is complete");
        }

        if (!setupProgressChecker.isProjectTeamCompleted()) {
            return fail("Unable to access Spend Profile section until the Project Team section is complete");
        }

        if (!setupProgressChecker.isSpendProfileGenerated()) {

            return fail("Unable to access Spend Profile section until this Partner Organisation has had its " +
                    "Spend Profile generated");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canEditSpendProfileSection(OrganisationResource userOrganisation, Long organisationIdFromUrl) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (canAccessSpendProfileSection(userOrganisation) == NOT_ACCESSIBLE) {
            return NOT_ACCESSIBLE;
        } else if (isFromOwnOrganisation(userOrganisation, organisationIdFromUrl)) {
            return ACCESSIBLE;
        } else {
            return fail("Unable to edit Spend Profile section as user does not belong to this organisation");
        }
    }

    private boolean isFromOwnOrganisation(OrganisationResource userOrganisation, Long organisationIdFromUrl) {

        return userOrganisation.getId().equals(organisationIdFromUrl);
    }

    public SectionAccess canAccessDocumentsSection(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (setupProgressChecker.isLeadPartnerOrganisation(organisation)) {
            return ACCESSIBLE;
        }

        if (isCompaniesHouseSectionIsUnnecessaryOrComplete(organisation,
                "Non-lead Partners are unable to access Documents section until their Companies House information " +
                        "is complete")) {
            return ACCESSIBLE;
        }

        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessGrantOfferLetterSection(OrganisationResource organisation) {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (setupProgressChecker.isSpendProfileApproved() && documentsApproved()
                && (isBankDetailsApproved(organisation))
                && setupProgressChecker.isGrantOfferLetterAvailable()
                && setupProgressChecker.isGrantOfferLetterSent()) {
            return ACCESSIBLE;
        }

        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessProjectSetupCompleteSection() {
        if (setupProgressChecker.isOfflineOrWithdrawn()) {
            return NOT_ACCESSIBLE;
        }

        if (setupProgressChecker.isSpendProfileApproved() && documentsApproved()) {
            return ACCESSIBLE;
        }

        return NOT_ACCESSIBLE;
    }

    private boolean documentsApproved() {
        return setupProgressChecker.isDocumentsApproved();
    }

    public boolean isSpendProfileGenerated() {
        return setupProgressChecker.isSpendProfileGenerated();
    }

    public boolean isFinanceContactSubmitted(OrganisationResource organisationResource) {
        return setupProgressChecker.isFinanceContactSubmitted(organisationResource);
    }

    public boolean isPartnerProjectLocationSubmitted(OrganisationResource organisationResource) {
        return setupProgressChecker.isPartnerProjectLocationSubmitted(organisationResource);
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
