package org.innovateuk.ifs.activitylog.advice;

import org.innovateuk.ifs.commons.error.Error;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.project.resource.ProjectOrganisationCompositeId;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceSuccess;

@Service
public class TestActivityLogServiceImpl implements TestActivityLogService {
    @Override
    public ServiceResult<Void> withApplicationId(long applicationId) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> withProjectId(long projectId) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> withProjectOrganisationCompositeId(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> withNotMatchingApplicationId(long applicationId) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> withNotMatchingProjectId(long projectId) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> withNotMatchingProjectOrganisationCompositeId(ProjectOrganisationCompositeId projectOrganisationCompositeId) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> withApplicationIdConditional(long applicationId, boolean conditional) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> withApplicationIdNotMatchingConditional(long applicationId, boolean conditional) {
        return serviceSuccess();
    }

    @Override
    public ServiceResult<Void> withApplicationIdServiceFailure(long applicationId) {
        return serviceFailure(new Error("", HttpStatus.NOT_FOUND));
    }

    @Override
    public void withApplicationIdNotServiceResult(long applicationId) {

    }
}
