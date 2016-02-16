package com.worth.ifs.transactional;

import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.security.NotSecured;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 *
 */
public interface ServiceFailureTransactionRollbackAdvisorTestService {

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
