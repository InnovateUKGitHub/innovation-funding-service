package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationFeedbackService;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationFeedbackServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class InterviewApplicationFeedbackServiceSecurityTest extends BaseServiceSecurityTest<InterviewApplicationFeedbackService> {

    @Override
    protected Class<? extends InterviewApplicationFeedbackService> getClassUnderTest() {
        return InterviewApplicationFeedbackServiceImpl.class;
    }

    @Test
    public void uploadFeedback() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.uploadFeedback("", "", "", 1L, null),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void downloadFeedback() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.downloadFeedback(1L),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void deleteFeedback() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.deleteFeedback(1L),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void findFeedback() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.findFeedback(1L),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

}