package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.transactional.AcceptInviteService;
import org.innovateuk.ifs.user.resource.UserRoleType;
import org.junit.Test;

public class AcceptInviteServiceSecurityTest extends BaseServiceSecurityTest<AcceptInviteService> {

    @Override
    protected Class<? extends AcceptInviteService> getClassUnderTest() {
        return TestAcceptInviteService.class;
    }

    @Test
    public void acceptInvite() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.acceptInvite("abcdef", 1L),
                UserRoleType.SYSTEM_REGISTRATION_USER
        );
    }

    static class TestAcceptInviteService implements AcceptInviteService {

        @Override
        public ServiceResult<Void> acceptInvite(String inviteHash, Long userId) {
            return null;
        }
    }
}
