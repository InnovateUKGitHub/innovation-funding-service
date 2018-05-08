package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.security.CompetitionParticipantLookupStrategy;
import org.innovateuk.ifs.assessment.security.CompetitionParticipantPermissionRules;
import org.innovateuk.ifs.interview.transactional.InterviewAllocationService;
import org.innovateuk.ifs.interview.transactional.InterviewAllocationServiceImpl;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class InterviewAllocationServiceSecurityTest extends BaseServiceSecurityTest<InterviewAllocationService> {

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private InterviewInvitePermissionRules interviewInvitePermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;
    private UserLookupStrategies userLookupStrategies;
    private InterviewParticipantPermissionRules interviewParticipantPermissionRules;
    private InterviewParticipantLookupStrategy interviewParticipantLookupStrategy;

    @Override
    protected Class<? extends InterviewAllocationService> getClassUnderTest() {
        return InterviewAllocationServiceImpl.class;
    }

    @Before
    public void setUp() throws Exception {
        competitionParticipantPermissionRules = getMockPermissionRulesBean(CompetitionParticipantPermissionRules.class);
        competitionParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean
                (CompetitionParticipantLookupStrategy.class);
        interviewInvitePermissionRules = getMockPermissionRulesBean(InterviewInvitePermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
        interviewParticipantPermissionRules = getMockPermissionRulesBean(InterviewParticipantPermissionRules.class);
        interviewParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean
                (InterviewParticipantLookupStrategy.class);
    }

    @Test
    public void getAllocateApplicationsOverview() {
        Pageable pageable = new PageRequest(0, 20);

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getInterviewAcceptedAssessors(1L, pageable),
                COMP_ADMIN, PROJECT_FINANCE);
    }

}
