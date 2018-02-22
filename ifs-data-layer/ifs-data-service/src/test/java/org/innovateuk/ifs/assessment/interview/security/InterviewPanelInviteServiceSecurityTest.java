package org.innovateuk.ifs.assessment.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.assessment.interview.transactional.InterviewPanelInviteService;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.resource.AvailableApplicationPageResource;
import org.innovateuk.ifs.invite.resource.ExistingUserStagedInviteResource;
import org.innovateuk.ifs.invite.resource.InterviewPanelStagedApplicationPageResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.innovateuk.ifs.invite.builder.ExistingUserStagedInviteResourceBuilder.newExistingUserStagedInviteResource;
import static org.innovateuk.ifs.user.resource.UserRoleType.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.UserRoleType.PROJECT_FINANCE;

public class InterviewPanelInviteServiceSecurityTest extends BaseServiceSecurityTest<InterviewPanelInviteService> {

    @Override
    protected Class<? extends InterviewPanelInviteService> getClassUnderTest() {
        return TestInterviewPanelInviteService.class;
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
                () -> classUnderTest.assignApplications(newExistingUserStagedInviteResource().build(2)),
                COMP_ADMIN, PROJECT_FINANCE
        );
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