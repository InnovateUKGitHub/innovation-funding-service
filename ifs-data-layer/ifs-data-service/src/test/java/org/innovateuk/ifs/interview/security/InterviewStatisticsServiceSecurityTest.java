package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.interview.transactional.InterviewStatisticsService;
import org.innovateuk.ifs.interview.transactional.InterviewStatisticsServiceImpl;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class InterviewStatisticsServiceSecurityTest extends BaseServiceSecurityTest<InterviewStatisticsService> {

    @Override
    protected Class<? extends InterviewStatisticsService> getClassUnderTest() {
        return InterviewStatisticsServiceImpl.class;
    }

    @Test
    public void getInterviewInviteStatistics() {
        long competitionId = 1L;

        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getInterviewInviteStatistics(competitionId),
                COMP_ADMIN,
                PROJECT_FINANCE
        );
    }
}