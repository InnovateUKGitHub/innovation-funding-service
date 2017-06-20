package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.security.NotSecured;
import org.innovateuk.ifs.commons.security.SecuredBySpring;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * A test Service for tests in {@link ServiceFailureExceptionHandlingAdviceTest}
 */
public interface ServiceFailureExceptionHandlingAdviceTestService {

    @NotSecured(value = "just a test method", mustBeSecuredByOtherServices = false)
    ServiceResult<String> successfulMethod();

    @NotSecured(value = "just a test method", mustBeSecuredByOtherServices = false)
    ServiceResult<String> restoreSuccessfulMethod();

    @NotSecured(value = "just a test method", mustBeSecuredByOtherServices = false)
    ServiceResult<String> failingMethod();

    @NotSecured(value = "just a test method", mustBeSecuredByOtherServices = false)
    ServiceResult<String> exceptionThrowingMethod();

    @SecuredBySpring(value="TODO", description = "TODO")
    @PreAuthorize("hasAuthority('nonexistentrole')")
    ServiceResult<String> accessDeniedMethod();

    @NotSecured(value = "just a test method", mustBeSecuredByOtherServices = false)
    ServiceResult<String> successfulMethodWithInternalFailingCall();

    @NotSecured(value = "just a test method", mustBeSecuredByOtherServices = false)
    ServiceResult<String> failingMethodWithInternalFailingCall();
}
