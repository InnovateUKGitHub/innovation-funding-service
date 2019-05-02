package org.innovateuk.ifs.project.projectteam.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.domain.Project;
import org.innovateuk.ifs.project.core.domain.ProjectParticipantRole;
import org.innovateuk.ifs.project.core.domain.ProjectUser;
import org.innovateuk.ifs.project.core.repository.ProjectUserRepository;
import org.innovateuk.ifs.project.core.transactional.AbstractProjectServiceImpl;
import org.innovateuk.ifs.project.resource.ProjectUserCompositeId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_FINANCE_CONTACT;
import static org.innovateuk.ifs.project.core.domain.ProjectParticipantRole.PROJECT_MANAGER;
import static org.innovateuk.ifs.util.CollectionFunctions.simpleAnyMatch;

/**
 * Transactional and secure service for Project team processing work
 */
@Service
public class ProjectTeamServiceImpl extends AbstractProjectServiceImpl implements ProjectTeamService {

    @Autowired
    private ProjectUserRepository projectUserRepository;

    @Override
    @Transactional
    public ServiceResult<Void> removeUser(ProjectUserCompositeId composite) {
        return getProject(composite.getProjectId()).andOnSuccess(
                project -> validateUserNotPm(project, composite.getUserId()).andOnSuccess(
                        () -> validateUserNotFc(project, composite.getUserId()).andOnSuccess(
                                () -> validateUserNotRemovingThemselves(composite.getUserId()).andOnSuccess(
                                        () -> removeUserFromProject(composite.getUserId(), project)))
                ));
    }

    private ServiceResult<Void> validateUserNotPm(Project project, long userId) {
        return userHasRoleOnProject(PROJECT_MANAGER, userId, project) ?
                serviceFailure(CANNOT_REMOVE_PROJECT_MANAGER_FROM_PROJECT) :
                serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotFc(Project project, long userId) {
        return userHasRoleOnProject(PROJECT_FINANCE_CONTACT, userId, project) ?
                serviceFailure(CANNOT_REMOVE_FINANCE_CONTACT_FROM_PROJECT) :
                serviceSuccess();
    }

    private ServiceResult<Void> validateUserNotRemovingThemselves(long userId) {
        return getCurrentlyLoggedInUser().andOnSuccess(
                user -> user.getId().equals(userId) ?
                        serviceFailure(CANNOT_REMOVE_YOURSELF_FROM_PROJECT) :
                        serviceSuccess());
    }

    private boolean userHasRoleOnProject(ProjectParticipantRole role, long userId, Project project) {
        return simpleAnyMatch(project.getProjectUsersWithRole(role),
                              pu -> pu.getUser().getId().equals(userId));
    }

    private ServiceResult<Void> removeUserFromProject(long userId, Project project) {

        List<ProjectUser> projectUsers = projectUserRepository.findByProjectIdAndUserId(project.getId(), userId);
        projectUserRepository.deleteAll(projectUsers);
        project.removeProjectUsers(projectUsers);

        return serviceSuccess();
    }

}
