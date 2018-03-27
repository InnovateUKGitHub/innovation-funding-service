package org.innovateuk.ifs.assessment.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentInviteService;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.InterviewAssignmentStagedApplicationPageResource;
import org.innovateuk.ifs.invite.resource.StagedApplicationResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

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
    public void unstageApplication() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.unstageApplication(1L),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void unstageApplications() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.unstageApplications(),
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
        public ServiceResult<Void> unstageApplication(long applicationId) {
            return null;
        }

        @Override
        public ServiceResult<Void> unstageApplications() {
            return null;
        }
    }
}