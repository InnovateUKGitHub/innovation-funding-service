package org.innovateuk.ifs.project.sections.security;

import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.sections.ProjectSetupSectionInternalUser;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

import static org.innovateuk.ifs.project.sections.SectionAccess.ACCESSIBLE;

/**
 * Permission checker around the access to various sections within the Project Setup process
 */
@PermissionRules
@Component
public class ProjectSetupSectionsPermissionRules {

    private static final Log LOG = LogFactory.getLog(ProjectSetupSectionsPermissionRules.class);

    @Autowired
    private ProjectService projectService;

    @PermissionRule(value = "ACCESS_PROJECT_DETAILS_SECTION", description = "An internal user can access the Project Details section when submitted by Partners (Individual)")
    public boolean internalCanAccessProjectDetailsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessProjectDetailsSection);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "An internal user can access after project details are submitted by the lead")
    public boolean internalCanAccessMonitoringOfficerSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessMonitoringOfficerSection);
    }

    @PermissionRule(value = "ACCESS_BANK_DETAILS_SECTION", description = "An internal user can access the Bank Details " +
            "section when submitted by Partners (Individual)")
    public boolean internalCanAccessBankDetailsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessBankDetailsSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_SECTION", description = "An internal user can always access the Finance checks section")
    public boolean internalCanAccessFinanceChecksSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessFinanceChecksSection);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "An internal user can access the Spend Profile " +
            "section when the lead partner submits the project spendprofile")
    public boolean internalCanAccessSpendProfileSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessSpendProfileSection);
    }

    @PermissionRule(value = "ACCESS_OTHER_DOCUMENTS_SECTION", description = "An internal user can access the Other Documents " +
            "section when the lead partner submits the documents")
    public boolean internalCanAccessOtherDocumentsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessOtherDocumentsSection);
    }

    @PermissionRule(value = "ACCESS_GRANT_OFFER_LETTER_SEND_SECTION", description = "An internal user can access the Grant Offer Letter send " +
            "section when the lead partner submits the documents")
    public boolean internalCanAccessGrantOfferLetterSendSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessGrantOfferLetterSendSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_QUERIES_SECTION", description = "A finance team user can always access the Finance checks queries section")
    public boolean internalCanAccessFinanceChecksQueriesSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessFinanceChecksQueriesSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_NOTES_SECTION", description = "A finance team can always access the Finance checks notes section")
    public boolean internalCanAccessFinanceChecksNotesSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessFinanceChecksNotesSection);
    }


    private boolean doSectionCheck(Long projectId, UserResource user, BiFunction<ProjectSetupSectionInternalUser, UserResource, SectionAccess> sectionCheckFn) {
        ProjectStatusResource projectStatusResource;

        if (!isInternal(user)) {
            return false;
        }

        try {
            projectStatusResource = projectService.getProjectStatus(projectId);
        } catch (ForbiddenActionException e) {
            LOG.error("Internal user is not allowed to access this project " + projectId);
            return false;
        }

        ProjectSetupSectionInternalUser sectionAccessor = new ProjectSetupSectionInternalUser(projectStatusResource);

        return sectionCheckFn.apply(sectionAccessor, user) == ACCESSIBLE;
    }


    private boolean isInternal(UserResource user) {
        return user.hasRole(UserRoleType.COMP_ADMIN)
                || user.hasRole(UserRoleType.PROJECT_FINANCE);
    }
}
