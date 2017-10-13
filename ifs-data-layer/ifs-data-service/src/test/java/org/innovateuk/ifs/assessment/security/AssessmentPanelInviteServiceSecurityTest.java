package org.innovateuk.ifs.assessment.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.transactional.AssessmentPanelInviteService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static java.util.Collections.singletonList;
import static org.innovateuk.ifs.invite.builder.AssessorInviteSendResourceBuilder.newAssessorInviteSendResource;
import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class AssessmentPanelInviteServiceSecurityTest extends BaseServiceSecurityTest<AssessmentPanelInviteService> {

    private CompetitionParticipantPermissionRules competitionParticipantPermissionRules;
    private CompetitionParticipantLookupStrategy competitionParticipantLookupStrategy;

    @Override
    protected Class<? extends AssessmentPanelInviteService> getClassUnderTest() {
        return TestAssessmentPanelInviteService.class;
    }

    @Before
    public void setUp() throws Exception {
        competitionParticipantPermissionRules = getMockPermissionRulesBean(CompetitionParticipantPermissionRules.class);
        competitionParticipantLookupStrategy = getMockPermissionEntityLookupStrategiesBean(CompetitionParticipantLookupStrategy.class);
    }

    @Test
    public void getCreatedInvites() {
        Pageable pageable = new PageRequest(0, 20);

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getCreatedInvites(1L, pageable),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void inviteExistingUsers() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.inviteUsers(
                newExistingUserStagedInviteResource().build(2)), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAvailableAssessors() throws Exception {
        Pageable pageable = new PageRequest(0, 20);

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAvailableAssessors(1L, pageable),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAvailableAssessorIds() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAvailableAssessorIds(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getNonAcceptedAssessorInviteIds() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getNonAcceptedAssessorInviteIds(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getAllInvitesToSend() throws Exception {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAllInvitesToSend(1L),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void sendAllInvites() throws Exception {
        AssessorInviteSendResource assessorInviteSendResource = new AssessorInviteSendResource();
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.sendAllInvites(1L, assessorInviteSendResource),
                COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getResendInvites() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getAllInvitesToResend(1L, singletonList(2L)), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void resendInvites() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.resendInvites(singletonList(2L), newAssessorInviteSendResource().build()), COMP_ADMIN, PROJECT_FINANCE);
    }

    @Test
    public void getInvitationOverview() {
        Pageable pageable = new PageRequest(0, 20);

        testOnlyAUserWithOneOfTheGlobalRolesCan(() -> classUnderTest.getInvitationOverview(1L, pageable), COMP_ADMIN,PROJECT_FINANCE);
    }

    public static class TestAssessmentPanelInviteService implements AssessmentPanelInviteService {

        @Override
        public ServiceResult<AssessorCreatedInvitePageResource> getCreatedInvites(long competitionId, Pageable pageable) {
            return null;
        }

        @Override
        public ServiceResult<Void> inviteUsers(List<ExistingUserStagedInviteResource> existingUserStagedInviteResource) {
            return null;
        }

        @Override
        public ServiceResult<AssessorInviteOverviewPageResource> getInvitationOverview(long competitionId, Pageable pageable) {
            return null;
        }

        @Override
        public ServiceResult<List<Long>> getNonAcceptedAssessorInviteIds(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<AvailableAssessorPageResource> getAvailableAssessors(long competitionId, Pageable pageable) {
            return null;
        }

        @Override
        public ServiceResult<List<Long>> getAvailableAssessorIds(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> sendAllInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
            return null;
        }

        @Override
        public ServiceResult<Void> resendInvites(List<Long> inviteIds, AssessorInviteSendResource assessorInviteSendResource) {
            return null;
        }

        @Override
        public ServiceResult<AssessorInvitesToSendResource> getAllInvitesToSend(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<AssessorInvitesToSendResource> getAllInvitesToResend(long competitionId, List<Long> inviteIds) {
            return null;
        }
    }
}
