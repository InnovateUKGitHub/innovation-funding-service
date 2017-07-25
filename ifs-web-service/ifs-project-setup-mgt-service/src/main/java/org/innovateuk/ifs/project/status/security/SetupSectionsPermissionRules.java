package org.innovateuk.ifs.project.status.security;

import org.innovateuk.ifs.commons.error.exception.ForbiddenActionException;
import org.innovateuk.ifs.commons.error.exception.ObjectNotFoundException;
import org.innovateuk.ifs.commons.security.PermissionRule;
import org.innovateuk.ifs.commons.security.PermissionRules;
import org.innovateuk.ifs.project.ProjectService;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.innovateuk.ifs.project.resource.ProjectUserResource;
import org.innovateuk.ifs.project.sections.SectionAccess;
import org.innovateuk.ifs.project.status.StatusService;
import org.innovateuk.ifs.project.status.resource.ProjectStatusResource;
import org.innovateuk.ifs.user.resource.UserResource;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

import static org.innovateuk.ifs.project.sections.SectionAccess.ACCESSIBLE;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleFindFirst;

/**
 * Permission checker around the access to various sections within the Project Setup process
 */
@PermissionRules
@Component
public class SetupSectionsPermissionRules {

    private static final Log LOG = LogFactory.getLog(SetupSectionsPermissionRules.class);

    @Autowired
    private StatusService statusService;

    @Autowired
    private ProjectService projectService;

    @PermissionRule(value = "ACCESS_PROJECT_DETAILS_SECTION", description = "An internal user can access the Project Details section when submitted by Partners (Individual)")
    public boolean internalCanAccessProjectDetailsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessProjectDetailsSection);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "An internal user can access after project details are submitted by the lead")
    public boolean internalCanAccessMonitoringOfficerSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessMonitoringOfficerSection);
    }

    @PermissionRule(value = "ACCESS_BANK_DETAILS_SECTION", description = "An internal user can access the Bank Details " +
            "section when submitted by Partners (Individual)")
    public boolean internalCanAccessBankDetailsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessBankDetailsSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_SECTION", description = "An internal user can always access the Finance checks section")
    public boolean internalCanAccessFinanceChecksSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessFinanceChecksSection);
    }

    @PermissionRule(value = "ACCESS_SPEND_PROFILE_SECTION", description = "An internal user can access the Spend Profile " +
            "section when the lead partner submits the project spendprofile")
    public boolean internalCanAccessSpendProfileSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessSpendProfileSection);
    }

    @PermissionRule(value = "ACCESS_OTHER_DOCUMENTS_SECTION", description = "An internal user can access the Other Documents " +
            "section when the lead partner submits the documents")
    public boolean internalCanAccessOtherDocumentsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessOtherDocumentsSection);
    }

    @PermissionRule(value = "ACCESS_GRANT_OFFER_LETTER_SEND_SECTION", description = "An internal user can access the Grant Offer Letter send " +
            "section when the lead partner submits the documents")
    public boolean internalCanAccessGrantOfferLetterSendSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessGrantOfferLetterSendSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_QUERIES_SECTION", description = "A finance team user can always access the Finance checks queries section")
    public boolean internalCanAccessFinanceChecksQueriesSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessFinanceChecksQueriesSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_QUERIES_SECTION_ADD_QUERY", description = "A finance team user cannot add a query until a finance contact has been allocated for the organisation")
    public boolean internalCanAccessFinanceChecksAddQuery(ProjectOrganisationCompositeId target, UserResource user) {
        List<ProjectUserResource> projectUsers = projectService.getProjectUsersForProject(target.getProjectId());
        return simpleFindFirst(projectUsers, pu -> pu.isFinanceContact() && Objects.equals(pu.getOrganisation(), target.getOrganisationId())).isPresent() && doSectionCheck(target.getProjectId(), user, SetupSectionInternalUser::canAccessFinanceChecksQueriesSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_NOTES_SECTION", description = "A finance team can always access the Finance checks notes section")
    public boolean internalCanAccessFinanceChecksNotesSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, SetupSectionInternalUser::canAccessFinanceChecksNotesSection);
    }

    private boolean doSectionCheck(Long projectId, UserResource user, BiFunction<SetupSectionInternalUser, UserResource, SectionAccess> sectionCheckFn) {
        ProjectStatusResource projectStatusResource;

        if (!isInternal(user)) {
            return false;
        }

        try {
            projectStatusResource = statusService.getProjectStatus(projectId);
        } catch (ForbiddenActionException e) {
            LOG.error("Internal user is not allowed to access this project " + projectId);
            return false;
        } catch (ObjectNotFoundException e) {
            LOG.error("Status for project " + projectId + " cannot be found.");
            return false;
        }

        SetupSectionInternalUser sectionAccessor = new SetupSectionInternalUser(projectStatusResource);

        return sectionCheckFn.apply(sectionAccessor, user) == ACCESSIBLE;
    }

    private boolean isInternal(UserResource user) {
        return user.hasRole(UserRoleType.COMP_ADMIN)
                || user.hasRole(UserRoleType.PROJECT_FINANCE);
    }
}
