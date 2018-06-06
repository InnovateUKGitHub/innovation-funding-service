package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.interview.transactional.InterviewResponseService;
import org.innovateuk.ifs.interview.transactional.InterviewResponseServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.APPLICANT;
import static org.innovateuk.ifs.user.resource.Role.ASSESSOR;

public class InterviewResponseServiceSecurityTest extends BaseServiceSecurityTest<InterviewResponseService> {

    @Override
    protected Class<? extends InterviewResponseService> getClassUnderTest() {
        return InterviewResponseServiceImpl.class;
    }

    @Test
    public void uploadResponse() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.uploadResponse("", "", "", 1L, null),
                APPLICANT
        );
    }

    @Test
    public void downloadResponse() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.downloadResponse(1L),
                APPLICANT, ASSESSOR
        );
    }

    @Test
    public void deleteResponse() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.deleteResponse(1L),
                APPLICANT
        );
    }

    @Test
    public void findResponse() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.findResponse(1L),
                APPLICANT, ASSESSOR
        );
    }

}