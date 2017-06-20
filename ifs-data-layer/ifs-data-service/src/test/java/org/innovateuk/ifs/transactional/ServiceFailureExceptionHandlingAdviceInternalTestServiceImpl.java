package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.stereotype.Service;

import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_UNEXPECTED_ERROR;
import static org.innovateuk.ifs.commons.service.ServiceResult.serviceFailure;

@Service
public class ServiceFailureExceptionHandlingAdviceInternalTestServiceImpl implements ServiceFailureExceptionHandlingAdviceInternalTestService {

    @Override
    public ServiceResult<String> internalFailingCall() {
        return serviceFailure(GENERAL_UNEXPECTED_ERROR);
    }
}
