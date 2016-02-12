package com.worth.ifs.transactional;

import com.worth.ifs.commons.service.ServiceResult;

/**
 *
 */
public interface ServiceFailureTransactionRollbackAdvisorTestService {

    ServiceResult<String> successfulMethod();

    ServiceResult<String> failingMethod();

    ServiceResult<String> exceptionThrowingMethod();
}
