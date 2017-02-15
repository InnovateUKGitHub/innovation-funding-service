package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.service.ServiceResult;

/**
 * A test Service for tests in {@link ServiceFailureExceptionHandlingAdviceTest}
 */
public interface ServiceFailureExceptionHandlingAdviceInternalTestService {

    @NotSecured("just a test method")
    ServiceResult<String> internalFailingCall();
}
