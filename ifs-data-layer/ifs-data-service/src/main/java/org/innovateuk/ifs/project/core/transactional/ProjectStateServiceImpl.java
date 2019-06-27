package org.innovateuk.ifs.project.core.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.workflow.configuration.ProjectWorkflowHandler;
import org.innovateuk.ifs.transactional.BaseTransactionalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.*;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class ProjectStateServiceImpl extends BaseTransactionalService implements ProjectStateService {

    private ProjectWorkflowHandler projectWorkflowHandler;

    @Autowired
    public ProjectStateServiceImpl(ProjectWorkflowHandler projectWorkflowHandler) {
        this.projectWorkflowHandler = projectWorkflowHandler;
    }

    @Override
    @Transactional
    public ServiceResult<Void> withdrawProject(long projectId) {
        return getProject(projectId).andOnSuccess(
                project -> getCurrentlyLoggedInUser().andOnSuccess(user ->
                        projectWorkflowHandler.projectWithdrawn(project, user) ?
                                serviceSuccess() : serviceFailure(PROJECT_CANNOT_BE_WITHDRAWN)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> handleProjectOffline(long projectId) {
        return getProject(projectId).andOnSuccess(
                project -> getCurrentlyLoggedInUser().andOnSuccess(user ->
                        projectWorkflowHandler.handleProjectOffline(project, user) ?
                                serviceSuccess() : serviceFailure(PROJECT_CANNOT_BE_HANDLED_OFFLINE)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> completeProjectOffline(long projectId) {
        return getProject(projectId).andOnSuccess(
                project -> getCurrentlyLoggedInUser().andOnSuccess(user ->
                        projectWorkflowHandler.completeProjectOffline(project, user) ?
                                serviceSuccess() : serviceFailure(PROJECT_CANNOT_BE_COMPLETED_OFFLINE)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> putProjectOnHold(long projectId) {
        return getProject(projectId).andOnSuccess(
                project -> getCurrentlyLoggedInUser().andOnSuccess(user ->
                        projectWorkflowHandler.putProjectOnHold(project, user) ?
                                serviceSuccess() : serviceFailure(PROJECT_CANNOT_BE_PUT_ON_HOLD)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> resumeProject(long projectId) {
        return getProject(projectId).andOnSuccess(
                project -> getCurrentlyLoggedInUser().andOnSuccess(user ->
                        projectWorkflowHandler.resumeProject(project, user) ?
                                serviceSuccess() : serviceFailure(PROJECT_CANNOT_BE_RESUMED)));
    }
}
