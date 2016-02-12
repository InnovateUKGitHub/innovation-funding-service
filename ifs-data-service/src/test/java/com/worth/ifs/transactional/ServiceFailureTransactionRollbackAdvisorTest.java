package com.worth.ifs.transactional;

import com.worth.ifs.BaseIntegrationTest;
import com.worth.ifs.commons.service.ServiceResult;
import com.worth.ifs.user.domain.User;
import com.worth.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import static com.worth.ifs.commons.error.Errors.internalServerErrorError;
import static com.worth.ifs.commons.error.Errors.notFoundError;
import static com.worth.ifs.util.CollectionFunctions.getOnlyElement;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 *
 */
public class ServiceFailureTransactionRollbackAdvisorTest extends BaseIntegrationTest {

    @Autowired
    private ServiceFailureTransactionRollbackAdvisorTestService testService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    public void testSuccessfulMethodUpdatesDatabaseSuccessfully() {

        testService.successfulMethod();
        assertEquals("Successful", getUser().getName());

        testService.restoreSuccessfulMethod();
        assertEquals("Steve Smith", getUser().getName());
    }

    @Test
    @Rollback
    public void testFailingMethodRollsBackDatabaseSuccessfully() {

        ServiceResult<String> result = testService.failingMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, "Failure")));

        assertEquals("Steve Smith", getUser().getName());
    }

    @Test
    @Rollback
    public void testExceptionThrowingMethodCreatesServiceFailureAndRollsBackDatabaseSuccessfully() {

        ServiceResult<String> result = testService.exceptionThrowingMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(internalServerErrorError("Exception")));

        assertEquals("Steve Smith", getUser().getName());
    }

    private User getUser() {
        return getOnlyElement(userRepository.findByEmail("steve.smith@empire.com"));
    }
}
