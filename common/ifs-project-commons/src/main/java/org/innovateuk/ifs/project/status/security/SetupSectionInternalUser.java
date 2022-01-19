package org.innovateuk.ifs.project.status.security;

import lombok.extern.slf4j.Slf4j;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.sections.SectionAccess;
import org.innovateuk.ifs.user.resource.Authority;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;

import static org.innovateuk.ifs.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.innovateuk.ifs.user.resource.Role.*;
import static org.innovateuk.ifs.util.SecurityRuleUtil.*;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
@Slf4j
public class SetupSectionInternalUser {

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

        if (isSupport(userResource) || isInnovationLead(userResource) || userResource.hasAuthority(Authority.STAKEHOLDER)) {
            return projectSetupProgressChecker.isMonitoringOfficerSubmitted() ? ACCESSIBLE : NOT_ACCESSIBLE;
        }

        if (isExternalFinanceUser(userResource)) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessBankDetailsSection(UserResource userResource) {

        if (userResource.hasAuthority(Authority.AUDITOR) && projectSetupProgressChecker.isBankDetailsApproved()) {
            return ACCESSIBLE;
        }

        if (!userResource.hasAuthority(Authority.PROJECT_FINANCE) || !projectSetupProgressChecker.isBankDetailsAccessible()) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessFinanceChecksSection(UserResource userResource) {
        return (userResource.hasAuthority(Authority.PROJECT_FINANCE)
                || userResource.hasRole(EXTERNAL_FINANCE)
                || userResource.hasAuthority(Authority.AUDITOR))
                ? ACCESSIBLE : NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessSpendProfileSection(UserResource userResource) {

        if (isExternalFinanceUser(userResource)) {
            return NOT_ACCESSIBLE;
        }

        boolean approved = projectSetupProgressChecker.isSpendProfileApproved();
        boolean submitted = projectSetupProgressChecker.isSpendProfileSubmitted();
        if (approved || submitted) {
            if (isSupport(userResource) || isInnovationLead(userResource) || userResource.hasRole(STAKEHOLDER)) {
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

        if (isExternalFinanceUser(userResource))  {
            return NOT_ACCESSIBLE;
        }

        if ((isSupport(userResource) || isInnovationLead(userResource) || userResource.hasRole(Role.STAKEHOLDER)) && !projectSetupProgressChecker.allDocumentsApproved()) {
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

        if (isExternalFinanceUser(userResource)) {
            return NOT_ACCESSIBLE;
        }

        if (documentsApproved()
                && projectSetupProgressChecker.isSpendProfileApproved()
                && projectSetupProgressChecker.isBankDetailsApproved()
                && projectSetupProgressChecker.isApplicationSuccessful()) {
            if (isSupport(userResource) || isInnovationLead(userResource) || userResource.hasRole(STAKEHOLDER)) {
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
        if (user.hasAuthority(Authority.PROJECT_FINANCE) || user.hasAuthority(Authority.COMP_ADMIN))
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
        return (userResource.hasAuthority(Authority.PROJECT_FINANCE)
                || userResource.hasRole(EXTERNAL_FINANCE)
                || userResource.hasRole(AUDITOR))
                ? ACCESSIBLE : NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessFinanceChecksNotesSection(UserResource userResource) {
        return (userResource.hasAuthority(Authority.PROJECT_FINANCE)
                || userResource.hasRole(EXTERNAL_FINANCE)
                || userResource.hasAuthority(Authority.AUDITOR))
                ? ACCESSIBLE : NOT_ACCESSIBLE;
    }

    private SectionAccess fail(String message) {
        log.info(message);
        return NOT_ACCESSIBLE;
    }

}
