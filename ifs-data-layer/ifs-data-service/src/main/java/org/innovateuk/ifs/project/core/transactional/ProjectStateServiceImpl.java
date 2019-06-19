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

    @Autowired
    private ProjectWorkflowHandler projectWorkflowHandler;

    @Override
    @Transactional
    public ServiceResult<Void> withdrawProject(long projectId) {

        return getProject(projectId).andOnSuccess(
                existingProject -> getCurrentlyLoggedInUser().andOnSuccess(user ->
                        projectWorkflowHandler.projectWithdrawn(existingProject, user) ?
                                serviceSuccess() : serviceFailure(PROJECT_CANNOT_BE_WITHDRAWN))
        );
    }

    @Override
    @Transactional
    public ServiceResult<Void> handleProjectOffline(long projectId) {

        return getProject(projectId).andOnSuccess(
                existingProject -> getCurrentlyLoggedInUser().andOnSuccess(user ->
                        projectWorkflowHandler.handleProjectOffline(existingProject, user) ?
                                serviceSuccess() : serviceFailure(PROJECT_CANNOT_BE_HANDLED_OFFLINE)));
    }

    @Override
    @Transactional
    public ServiceResult<Void> completeProjectOffline(long projectId) {

        return getProject(projectId).andOnSuccess(
                existingProject -> getCurrentlyLoggedInUser().andOnSuccess(user ->
                        projectWorkflowHandler.completeProjectOffline(existingProject, user) ?
                                serviceSuccess() : serviceFailure(PROJECT_CANNOT_BE_COMPLETED_OFFLINE)));
    }
}
