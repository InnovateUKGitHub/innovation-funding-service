package org.innovateuk.ifs.application.security;

import org.innovateuk.ifs.application.domain.Application;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.security.BasePermissionRules;
import org.innovateuk.ifs.user.resource.Role;
import org.innovateuk.ifs.user.resource.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static org.innovateuk.ifs.util.SecurityRuleUtil.isInternal;

@Component
public class ApplicationSecurityHelper extends BasePermissionRules {

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * This method can be used anywhere giving permission for a user to see an application and related data.
     */
    public boolean canViewApplication(long applicationId, UserResource user) {
        return isInternal(user)
        || hasProcessRole(applicationId, user)
        || isInterviewAssessor(applicationId, user)
        || isExternalFinance(applicationId, user)
        || isMonitoringOfficerForProjectLinkedToApplication(applicationId, user.getId())
        || isStakeHolder(applicationId, user)
        || isSupporterForApplication(applicationId, user.getId())
        || isLinkedToProject(applicationId, user);
    }

    private boolean isExternalFinance(long applicationId, final UserResource user) {
        if (!user.hasRole(Role.EXTERNAL_FINANCE)) {
            return false;
        }
        Optional<Project> project = projectRepository.findByApplicationId(applicationId);

        if (project.isPresent()) {
            return userIsExternalFinanceOnCompetitionForProject(project.get().getId(), user.getId());
        }
        return false;
    }

    private boolean isStakeHolder(final long applicationId, final UserResource user) {
        if (!user.hasRole(Role.STAKEHOLDER)) {
            return false;
        }
        Application application = applicationRepository.findById(applicationId).get();
        return userIsStakeholderInCompetition(application.getCompetition().getId(), user.getId());
    }

    private boolean isLinkedToProject(long applicationId, final UserResource user) {
        Project linkedProject = projectRepository.findOneByApplicationId(applicationId);
        if (linkedProject == null) {
            return false;
        }
        return isPartner(linkedProject.getId(), user.getId());
    }

}
