package org.innovateuk.ifs.assessment.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentInviteService;
import org.innovateuk.ifs.invite.resource.*;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.innovateuk.ifs.user.resource.Role.*;

public class InterviewAssignmentInviteServiceSecurityTest extends BaseServiceSecurityTest<InterviewAssignmentInviteService> {

    @Override
    protected Class<? extends InterviewAssignmentInviteService> getClassUnderTest() {
        return TestInterviewAssignmentInviteService.class;
    }

    private static Pageable PAGE_REQUEST = new PageRequest(0,20);

    @Test
    public void getAvailableApplications() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAvailableApplications(1L, PAGE_REQUEST),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void getStagedApplications() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getStagedApplications(1L,PAGE_REQUEST),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void getAvailableApplicationIds() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getAvailableApplicationIds(1L),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void assignApplications() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.assignApplications(newStagedApplicationResource().build(2)),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void getEmailTemplate() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getEmailTemplate(),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void sendInvites() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.sendInvites(1L, new AssessorInviteSendResource("Subject", "Content")),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    public static class TestInterviewAssignmentInviteService implements InterviewAssignmentInviteService {

        @Override
        public ServiceResult<AvailableApplicationPageResource> getAvailableApplications(long competitionId, Pageable pageable) {
            return null;
        }

        @Override
        public ServiceResult<InterviewAssignmentStagedApplicationPageResource> getStagedApplications(long competitionId, Pageable pageable) {
            return null;
        }

        @Override
        public ServiceResult<List<Long>> getAvailableApplicationIds(long competitionId) {
            return null;
        }

        @Override
        public ServiceResult<Void> assignApplications(List<StagedApplicationResource> invites) {
            return null;
        }

        @Override
        public ServiceResult<ApplicantInterviewInviteResource> getEmailTemplate() {
            return null;
        }

        @Override
        public ServiceResult<Void> sendInvites(long competitionId, AssessorInviteSendResource assessorInviteSendResource) {
            return null;
        }
    }
}