package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.project.constant.ProjectActivityStates;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import static org.innovateuk.ifs.project.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.project.sections.SectionAccess.NOT_ACCESSIBLE;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInnovationLead;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternalAdmin;
import static org.innovateuk.ifs.util.SecurityRuleUtil.isSupport;

/**
 * This is a helper class for determining whether or not a given Project Setup section is available to access
 */
public class SetupSectionInternalUser {

    private static final Log LOG = LogFactory.getLog(SetupSectionInternalUser.class);

    private SetupProgressChecker projectSetupProgressChecker;

    public SetupSectionInternalUser(ProjectStatusResource projectStatusResource) {
        this.projectSetupProgressChecker = new SetupProgressChecker(projectStatusResource);
    }

    public SectionAccess canAccessCompaniesHouseSection(UserResource userResource) {
        return NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessProjectDetailsSection(UserResource userResource) {
        if (!projectSetupProgressChecker.isProjectDetailsSubmitted()) {
            return fail("Unable to access Project Details section until Project Details are submitted");
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessMonitoringOfficerSection(UserResource userResource) {
        if (!projectSetupProgressChecker.canAccessMonitoringOfficer()) {
            return fail("Unable to access Monitoring Officer section until Project Details are submitted");
        }

        if(isSupport(userResource) || isInnovationLead(userResource)){
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
        return userResource.hasRole(PROJECT_FINANCE) ? ACCESSIBLE : NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessSpendProfileSection(UserResource userResource) {
        boolean approved = projectSetupProgressChecker.isSpendProfileApproved();
        boolean submitted = projectSetupProgressChecker.isSpendProfileSubmitted();
        if (approved || submitted) {
            if(isSupport(userResource) || isInnovationLead(userResource)) {
                if(approved) {
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

    public SectionAccess canAccessOtherDocumentsSection(UserResource userResource) {
        if(!projectSetupProgressChecker.isOtherDocumentsSubmitted() && !(projectSetupProgressChecker.isOtherDocumentsApproved() || projectSetupProgressChecker.isOtherDocumentsRejected())) {
            return NOT_ACCESSIBLE;
        }

        if((isSupport(userResource) || isInnovationLead(userResource)) && !projectSetupProgressChecker.isOtherDocumentsApproved()){
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessGrantOfferLetterSection(UserResource userResource) {
        if(!projectSetupProgressChecker.isGrantOfferLetterSent()) {
            return NOT_ACCESSIBLE;
        }

        return ACCESSIBLE;
    }

    public SectionAccess canAccessGrantOfferLetterSendSection(UserResource userResource) {
        if(projectSetupProgressChecker.isOtherDocumentsApproved() && projectSetupProgressChecker.isSpendProfileApproved()) {
            if(isSupport(userResource) || isInnovationLead(userResource)) {
                if(projectSetupProgressChecker.isGrantOfferLetterApproved()){
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

    public SectionAccess canAccessFinanceChecksQueriesSection(UserResource userResource) {
        return userResource.hasRole(PROJECT_FINANCE) ? ACCESSIBLE : NOT_ACCESSIBLE;
    }

    public SectionAccess canAccessFinanceChecksNotesSection(UserResource userResource) {
        return userResource.hasRole(PROJECT_FINANCE) ? ACCESSIBLE : NOT_ACCESSIBLE;
    }


    private SectionAccess fail(String message) {
        LOG.info(message);
        return NOT_ACCESSIBLE;
    }

    public ProjectActivityStates grantOfferLetterActivityStatus(UserResource userResource) {
        if(isInternalAdmin(userResource)) {
            return projectSetupProgressChecker.getRoleSpecificActivityState().get(COMP_ADMIN);
        } else {
            return projectSetupProgressChecker.getGrantOfferLetterState();
        }

    }
}
