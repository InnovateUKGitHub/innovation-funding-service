package org.innovateuk.ifs.project.core.transactional;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.innovateuk.ifs.commons.error.CommonFailureKeys;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.core.repository.ProjectRepository;
import org.innovateuk.ifs.util.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

@Service
public class ProjectCreationAsyncServiceImpl implements ProjectCreationAsyncService {
    private static final Log LOG = LogFactory.getLog(ProjectCreationAsyncServiceImpl.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private AuthenticationHelper authenticationHelper;

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    @Async("ProjectCreationAsyncExecutor")
    public Future<ServiceResult<Void>> createAsync(Function<Long, ServiceResult<Void>> postCreationFunction, Long applicationId, Consumer<Long> rollbackFunction) {
        authenticationHelper.loginSystemUser();
        try {
            return new AsyncResult<>(projectService.createProjectFromApplication(applicationId)
                    .andOnSuccess(() -> postCreationFunction.apply(applicationId))
                    .andOnSuccessReturnVoid()
                    .andOnFailure(() -> rollbackFunction.accept(applicationId)));
        } catch (Exception e) {
            LOG.error(e);
            rollbackFunction.accept(applicationId);
            return new AsyncResult<>(serviceFailure(CommonFailureKeys.GENERAL_UNEXPECTED_ERROR));
        }
    }
}
