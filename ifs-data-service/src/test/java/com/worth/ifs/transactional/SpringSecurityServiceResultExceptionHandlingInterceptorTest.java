package com.worth.ifs.transactional;

import com.worth.ifs.BaseIntegrationTest;
import org.junit.Test;
import org.springframework.test.annotation.Rollback;

/**
 * Tests for {@link SpringSecurityServiceResultExceptionHandlingInterceptor}, to ensure that Spring Security exceptions
 * are successfully converted into failing "forbidden" ServiceResults.
 */
// TODO RP DW - why is this causing cyclical refs
public class SpringSecurityServiceResultExceptionHandlingInterceptorTest extends BaseIntegrationTest {

//    @Autowired
//    private ServiceFailureExceptionHandlingAdviceTestService testService;

    @Test
    @Rollback
    public void testTryingToAccessSecuredMethodHandlesAccessDeniedExceptionSuccessfully() {

//        SecurityContextHolder.clearContext();
//
//        ServiceResult<String> result = testService.accessDeniedMethod();
//        assertTrue(result.isFailure());
//        assertTrue(result.getFailure().is(forbiddenError("This action is not permitted.")));
    }
}
