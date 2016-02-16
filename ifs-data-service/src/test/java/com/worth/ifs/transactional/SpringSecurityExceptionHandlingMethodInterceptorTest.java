package com.worth.ifs.transactional;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static com.worth.ifs.commons.error.Errors.forbiddenError;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class SpringSecurityExceptionHandlingMethodInterceptorTest extends BaseIntegrationTest {

    @Autowired
    private ServiceFailureTransactionRollbackAdvisorTestService testService;

    @Test
    @Rollback
    public void testTryingToAccessSecuredMethodHandlesAccessDeniedExceptionSuccessfully() {

        ServiceResult<String> result = testService.accessDeniedMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError("This action is not permitted.")));
    }
}
