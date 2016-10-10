package com.worth.ifs.project;

import com.worth.ifs.commons.error.exception.ForbiddenActionException;
import com.worth.ifs.commons.security.PermissionRule;
import com.worth.ifs.commons.security.PermissionRules;
import com.worth.ifs.project.sections.ProjectSetupSectionInternalUser;
import com.worth.ifs.project.sections.SectionAccess;
import com.worth.ifs.project.status.resource.ProjectStatusResource;
import com.worth.ifs.user.resource.UserResource;
import com.worth.ifs.user.resource.UserRoleType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiFunction;

import static com.worth.ifs.project.sections.SectionAccess.ACCESSIBLE;

/**
 * Permission checker around the access to various sections within the Project Setup process
 */
@PermissionRules
@Component
public class ProjectSetupSectionsPermissionRules {

    private static final Log LOG = LogFactory.getLog(ProjectSetupSectionsPermissionRules.class);

    @Autowired
    private ProjectService projectService;

    @PermissionRule(value = "ACCESS_PROJECT_DETAILS_SECTION", description = "A internal user can access the Project Details section when submitted by Partners (Individual)")
    public boolean internalCanAccessProjectDetailsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessProjectDetailsSection);
    }

    @PermissionRule(value = "ACCESS_MONITORING_OFFICER_SECTION", description = "A internal user can access after project details are submitted by the lead")
    public boolean internalCanAccessMonitoringOfficerSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessMonitoringOfficerSection);
    }

    @PermissionRule(value = "ACCESS_BANK_DETAILS_SECTION", description = "A internal can access the Bank Details " +
            "section when submitted by Partners (Individual)")
    public boolean internalCanAccessBankDetailsSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessBankDetailsSection);
    }

    @PermissionRule(value = "ACCESS_FINANCE_CHECKS_SECTION", description = "A internal can always access the Finance checks section")
    public boolean internalCanAccessFinanceChecksSection(Long projectId, UserResource user) {
        return doSectionCheck(projectId, user, ProjectSetupSectionInternalUser::canAccessFinanceChecksSection);
    }

    private boolean doSectionCheck(Long projectId, UserResource user, BiFunction<ProjectSetupSectionInternalUser, UserResource, SectionAccess> sectionCheckFn) {
        ProjectStatusResource projectStatusResource;

        if (!isCompAdminOrFinanceTeam(user)) {
            return false;
        }

        try {
            projectStatusResource = projectService.getProjectStatus(projectId);
        } catch (ForbiddenActionException e) {
            LOG.error("Internal user  is not allowed to access this project " + projectId);
            return false;
        }

        ProjectSetupSectionInternalUser sectionAccessor = new ProjectSetupSectionInternalUser(projectStatusResource);

        return sectionCheckFn.apply(sectionAccessor, user) == ACCESSIBLE;
    }


    private boolean isCompAdminOrFinanceTeam(UserResource user) {
        return user.hasRole(UserRoleType.COMP_ADMIN) || user.hasRole(UserRoleType.PROJECT_FINANCE);
    }
}
