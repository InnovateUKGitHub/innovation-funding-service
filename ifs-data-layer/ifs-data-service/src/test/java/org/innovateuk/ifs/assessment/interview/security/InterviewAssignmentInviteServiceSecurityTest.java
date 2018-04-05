package org.innovateuk.ifs.assessment.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentInviteService;
import org.innovateuk.ifs.interview.transactional.InterviewAssignmentInviteServiceImpl;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.junit.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.innovateuk.ifs.invite.builder.StagedApplicationResourceBuilder.newStagedApplicationResource;
import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class InterviewAssignmentInviteServiceSecurityTest extends BaseServiceSecurityTest<InterviewAssignmentInviteService> {

    @Override
    protected Class<? extends InterviewAssignmentInviteService> getClassUnderTest() {
        return InterviewAssignmentInviteServiceImpl.class;
    }

    private static Pageable PAGE_REQUEST = new PageRequest(0, 20);

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
                () -> classUnderTest.getStagedApplications(1L, PAGE_REQUEST),
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

    public void getEmailTemplate() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getEmailTemplate(),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    @Test
    public void unstageApplications() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.unstageApplications(1L),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }

    public void sendInvites() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.sendInvites(1L, new AssessorInviteSendResource("Subject", "Content")),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }
}