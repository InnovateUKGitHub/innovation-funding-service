package org.innovateuk.ifs.project.status.security;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.innovateuk.ifs.user.resource.Role.COMPETITION_FINANCE;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
public class SetupSectionInternalUser {

    private static final Log LOG = LogFactory.getLog(SetupSectionInternalUser.class);

    private SetupProgressChecker projectSetupProgressChecker;

    public SetupSectionInternalUser(ProjectStatusResource projectStatusResource) {
        this.projectSetupProgressChecker = new SetupProgressChecker(projectStatusResource);
    }

    public SectionAccess canAccessCompaniesHouseSection() {
        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessProjectDetailsSection(UserResource userResource) {
        return ACCESSIBLE;
    }

    public SectionAccess canAccessMonitoringOfficerSection(UserResource userResource) {
        if (!projectSetupProgressChecker.canAccessMonitoringOfficer()) {
            return fail("Unable to access Monitoring Officer section until Project Details are submitted");
        }

        if (isSupport(userResource) || isInnovationLead(userResource) || isStakeholder(userResource) ||  isCompetitionFinance(userResource)) {
            return projectSetupProgressChecker.isMonitoringOfficerSubmitted() ? ACCESSIBLE : NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessBankDetailsSection(UserResource userResource) {

        if (!userResource.hasRole(PROJECT_FINANCE) || !projectSetupProgressChecker.isBankDetailsAccessible()) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessFinanceChecksSection(UserResource userResource) {
        return (userResource.hasRole(PROJECT_FINANCE) || userResource.hasRole(COMPETITION_FINANCE)) ?
                ACCESSIBLE : NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessSpendProfileSection(UserResource userResource) {
        boolean approved = projectSetupProgressChecker.isSpendProfileApproved();
        boolean submitted = projectSetupProgressChecker.isSpendProfileSubmitted();
        if (approved || submitted) {
            if (isSupport(userResource) || isInnovationLead(userResource) || isStakeholder(userResource) || isCompetitionFinance(userResource)) {
                if (approved) {
                    return ACCESSIBLE;
                } else {
                    return NOT_ACCESSIBLE;
                }
            } else {
                return ACCESSIBLE;
            }
        }

        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessDocumentsSection(UserResource userResource) {

        if ((isSupport(userResource) || isInnovationLead(userResource) || isStakeholder(userResource) || isCompetitionFinance(userResource)) && !projectSetupProgressChecker.allDocumentsApproved()) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessGrantOfferLetterSection(UserResource userResource) {
        if (!projectSetupProgressChecker.isGrantOfferLetterSent()) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessGrantOfferLetterSendSection(UserResource userResource) {
        if (documentsApproved()
                && projectSetupProgressChecker.isSpendProfileApproved()
                && projectSetupProgressChecker.isBankDetailsApproved()) {
            if (isSupport(userResource) || isInnovationLead(userResource) || isStakeholder(userResource)) {
                if (projectSetupProgressChecker.isGrantOfferLetterApproved()) {
                    return ACCESSIBLE;
                } else {
                    return NOT_ACCESSIBLE;
                }
            } else {
                return ACCESSIBLE;
            }
        }

        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessProjectSetupComplete(UserResource user) {
        if (user.hasRole(PROJECT_FINANCE)
                && documentsApproved()
                && projectSetupProgressChecker.isSpendProfileApproved()) {
            return ACCESSIBLE;
        }
        return NOT_ACCESSIBLE;
    }

    private boolean documentsApproved() {
        return projectSetupProgressChecker.allDocumentsApproved();
    }

    public SectionAccess canAccessFinanceChecksQueriesSection(UserResource userResource) {
        return (userResource.hasRole(PROJECT_FINANCE) || userResource.hasRole(COMPETITION_FINANCE) || userResource.hasRole(COMPETITION_FINANCE))  ?
                ACCESSIBLE : NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessFinanceChecksNotesSection(UserResource userResource) {
        return (userResource.hasRole(PROJECT_FINANCE) || userResource.hasRole(COMPETITION_FINANCE)) ?
                ACCESSIBLE : NOT_ACCESSIBLE;
    }

    private SectionAccess fail(String message) {
        LOG.info(message);
        return NOT_ACCESSIBLE;
    }

}
