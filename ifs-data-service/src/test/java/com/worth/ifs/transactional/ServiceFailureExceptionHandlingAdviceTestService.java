package com.worth.ifs.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.commons.security.NotSecured;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * A test Service for tests in {@link ServiceFailureExceptionHandlingAdviceTest}
 */
public interface ServiceFailureExceptionHandlingAdviceTestService {

    @NotSecured("just a test method")
    ServiceResult<String> successfulMethod();

    @NotSecured("just a test method")
    ServiceResult<String> restoreSuccessfulMethod();

    @NotSecured("just a test method")
    ServiceResult<String> failingMethod();

    @NotSecured("just a test method")
    ServiceResult<String> exceptionThrowingMethod();

    @PreAuthorize("hasAuthority('nonexistentrole')")
    ServiceResult<String> accessDeniedMethod();
}
