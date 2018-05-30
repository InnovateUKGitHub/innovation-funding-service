package org.innovateuk.ifs.interview.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationInviteService;
import org.innovateuk.ifs.interview.transactional.InterviewApplicationInviteServiceImpl;
import org.innovateuk.ifs.invite.resource.AssessorInviteSendResource;
import org.junit.Test;

import static org.innovateuk.ifs.user.resource.Role.COMP_ADMIN;
import static org.innovateuk.ifs.user.resource.Role.PROJECT_FINANCE;

public class InterviewApplicationInviteServiceSecurityTest extends BaseServiceSecurityTest<InterviewApplicationInviteService> {

    @Override
    protected Class<? extends InterviewApplicationInviteService> getClassUnderTest() {
        return InterviewApplicationInviteServiceImpl.class;
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

    @Test
    public void getSentInvite() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.getSentInvite(1L),
                COMP_ADMIN, PROJECT_FINANCE
        );
    }
}