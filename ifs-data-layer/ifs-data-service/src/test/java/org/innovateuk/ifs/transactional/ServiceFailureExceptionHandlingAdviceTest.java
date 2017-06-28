package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.innovateuk.ifs.commons.error.CommonErrors.notFoundError;
import static org.innovateuk.ifs.commons.error.CommonFailureKeys.GENERAL_SERVICE_RESULT_EXCEPTION_THROWN_DURING_PROCESSING;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ServiceFailureExceptionHandlingAdviceTest extends BaseIntegrationTest {

    @Autowired
    private ServiceFailureExceptionHandlingAdviceTestService testService;

    @Autowired
    private UserRepository userRepository;

    @Before
    public void assertExpectedStartingStateBeforeEachTest() {
        assertEquals("Steve Smith", getUser().getName());
    }

    @Test
    public void testSuccessfulMethodUpdatesDatabaseSuccessfully() {

        testService.successfulMethod();
        assertEquals("Successful Smith", getUser().getName());

        testService.restoreSuccessfulMethod();
        assertEquals("Steve Smith", getUser().getName());
    }

    @Test
    public void testFailingMethodRollsBackDatabaseSuccessfully() {

        ServiceResult<String> result = testService.failingMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(notFoundError(User.class, "Failure")));

        assertEquals("Steve Smith", getUser().getName());
    }

    /**
     * Test that if the top-level service method return success, transactions are not rolled back even if internal
     * service methods fail (as in legitimate failures, like handled "Not Founds"
     */
    @Test
    public void testSuccessfulMethodWithFailingInternalCallUpdatesDatabaseSuccessfully() {

        testService.successfulMethodWithInternalFailingCall();
        assertEquals("Successful Internal Smith", getUser().getName());

        testService.restoreSuccessfulMethod();
        assertEquals("Steve Smith", getUser().getName());
    }

    /**
     * Test that if the top-level service method return failure, transactions are rolled back and is not affected by
     * prior internal calls failing 
     */
    @Test
    public void testFailingMethodWithFailingInternalCallRollsBackDatabaseSuccessfully() {

        testService.failingMethodWithInternalFailingCall();
        assertEquals("Steve Smith", getUser().getName());
    }

    @Test
    public void testExceptionThrowingMethodCreatesServiceFailureAndRollsBackDatabaseSuccessfully() {

        ServiceResult<String> result = testService.exceptionThrowingMethod();
        assertTrue(result.isFailure());
        assertTrue(result.getFailure().is(GENERAL_SERVICE_RESULT_EXCEPTION_THROWN_DURING_PROCESSING));

        assertEquals("Steve Smith", getUser().getName());
    }

    private User getUser() {
        return userRepository.findByEmail("steve.smith@empire.com").get();
    }
}
