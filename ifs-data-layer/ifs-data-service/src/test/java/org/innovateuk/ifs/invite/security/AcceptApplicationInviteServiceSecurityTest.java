package org.innovateuk.ifs.invite.security;

import org.innovateuk.ifs.BaseServiceSecurityTest;
import org.innovateuk.ifs.commons.service.ServiceResult;
import org.innovateuk.ifs.invite.transactional.AcceptApplicationInviteService;
import org.innovateuk.ifs.user.resource.Role;
import org.junit.Test;

public class AcceptApplicationInviteServiceSecurityTest extends BaseServiceSecurityTest<AcceptApplicationInviteService> {

    @Override
    protected Class<? extends AcceptApplicationInviteService> getClassUnderTest() {
        return TestAcceptApplicationInviteService.class;
    }

    @Test
    public void acceptInvite() {
        testOnlyAUserWithOneOfTheGlobalRolesCan(
                () -> classUnderTest.acceptInvite("abcdef", 1L),
                Role.SYSTEM_REGISTRATION_USER
        );
    }

    static class TestAcceptApplicationInviteService implements AcceptApplicationInviteService {

        @Override
        public ServiceResult<Void> acceptInvite(String inviteHash, Long userId) {
            return null;
        }
    }
}
