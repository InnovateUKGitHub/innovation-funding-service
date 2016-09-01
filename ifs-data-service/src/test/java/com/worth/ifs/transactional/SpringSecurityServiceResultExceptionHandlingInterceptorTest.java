package com.worth.ifs.transactional;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.commons.service.ServiceResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.annotation.Rollback;

import static com.worth.ifs.commons.error.CommonErrors.forbiddenError;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link SpringSecurityServiceResultExceptionHandlingInterceptor}, to ensure that Spring Security exceptions
 * are successfully converted into failing "forbidden" ServiceResults.
 */
public class SpringSecurityServiceResultExceptionHandlingInterceptorTest extends BaseIntegrationTest {

    @Autowired
    private ServiceFailureExceptionHandlingAdviceTestService testService;

    @Test
    @Rollback
    public void testTryingToAccessSecuredMethodHandlesAccessDeniedExceptionSuccessfully() {
        SecurityContextHolder.clearContext();

        ServiceResult<String> result = testService.accessDeniedMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(forbiddenError()));
    }
}
