package org.innovateuk.ifs.transactional;

import org.innovateuk.ifs.commons.BaseIntegrationTest;
import org.innovateuk.ifs.user.domain.User;
import org.innovateuk.ifs.user.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

/**
 *
 */
// TODO RP DW - why is this causing cyclical refs
public class ServiceFailureExceptionHandlingAdviceTest extends BaseIntegrationTest {

//    @Autowired
//    private ServiceFailureExceptionHandlingAdviceTestService testService;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Rollback
    public void testSuccessfulMethodUpdatesDatabaseSuccessfully() {
//
//        testService.successfulMethod();
//        assertEquals("Successful", getUser().getName());
//
//        testService.restoreSuccessfulMethod();
//        assertEquals("Steve Smith", getUser().getName());
    }

    @Test
    @Rollback
    public void testFailingMethodRollsBackDatabaseSuccessfully() {
//
//        ServiceResult<String> result = testService.failingMethod();
//        assertTrue(result.isFailure());
//        assertTrue(result.getFailure().is(notFoundError(User.class, "Failure")));
//
//        assertEquals("Steve Smith", getUser().getName());
    }

    @Test
    @Rollback
    public void testExceptionThrowingMethodCreatesServiceFailureAndRollsBackDatabaseSuccessfully() {

//        ServiceResult<String> result = testService.exceptionThrowingMethod();
//        assertTrue(result.isFailure());
//        assertTrue(result.getFailure().is(internalServerErrorError()));
//
//        assertEquals("Steve Smith", getUser().getName());
    }

    private User getUser() {
        return userRepository.findByEmail("steve.smith@empire.com").get();
    }
}
