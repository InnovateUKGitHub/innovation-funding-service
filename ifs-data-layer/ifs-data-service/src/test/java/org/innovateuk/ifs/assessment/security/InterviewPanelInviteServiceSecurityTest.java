package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.interview.transactional.InterviewPanelInviteService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.innovateuk.ifs.user.security.UserLookupStrategies;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.util.Arrays.asList;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class InterviewPanelInviteServiceSecurityTest extends BaseServiceSecurityTest<InterviewPanelInviteService> {

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private AssessmentInterviewPanelInvitePermissionRules assessmentInterviewPanelInvitePermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;
    private UserLookupStrategies userLookupStrategies;
    private AssessmentInterviewPanelParticipantPermissionRules assessmentInterviewPanelParticipantPermissionRules;
    private AssessmentInterviewPanelParticipantLookupStrategy assessmentInterviewPanelParticipantLookupStrategy;

    Pageable pageable = new PageRequest(0, 5);

    @Before
    public void setUp() throws Exception {
        competitionParticipantPermissionRules = getMockPermissionRulesBean(CompetitionParticipantPermissionRules.class);
        competitionParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionParticipantLookupStrategy.class);
        assessmentInterviewPanelInvitePermissionRules = getMockPermissionRulesBean(AssessmentInterviewPanelInvitePermissionRules.class);
        userLookupStrategies = getMockPermissionEntityLookupStrategiesBean(UserLookupStrategies.class);
        assessmentInterviewPanelParticipantPermissionRules = getMockPermissionRulesBean(AssessmentInterviewPanelParticipantPermissionRules.class);
        assessmentInterviewPanelParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean(AssessmentInterviewPanelParticipantLookupStrategy.class);
    }

    @Test
    public void getAvailableApplications() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAvailableApplications(1L, pageable),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getStagedApplications() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getStagedApplications(1L, pageable),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAvailableApplicationIds() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAvailableApplicationIds(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void assignApplications() {
        List<ExistingUserStagedInviteResource> invites = asList(newExistingUserStagedInviteResource().build());
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.assignApplications(invites),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Override
    protected Class<? extends InterviewPanelInviteService> getClassUnderTest() {
        return InterviewPanelInviteServiceSecurityTest.TestInterviewPanelInviteService.class;
    }

    public static class TestInterviewPanelInviteService implements InterviewPanelInviteService {

        @Override
        public ServiceResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, Pageable pageable) {
            return null;
        }

        @Override
        public ServiceResult<InterviewPanelStagedApplicationPageResource> getStagedApplications(long competitionId, Pageable pageable) {
            return null;
        }

        @Override
        public ServiceResult<List<Long>> getAvailableApplicationIds(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> assignApplications(List<ExistingUserStagedInviteResource> invites) {
            return null;
        }
    }
}